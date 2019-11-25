package com.zjhousing.egov.proposal.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.SolrCloudClient.util.ATOMIC;
import com.rongji.egov.attachutil.model.EgovAtt;
import com.rongji.egov.attachutil.service.EgovAttMng;
import com.rongji.egov.docconfig.business.dao.RecSequenceDao;
import com.rongji.egov.docconfig.business.model.RecSequence;
import com.rongji.egov.docconfig.business.service.DisUnitMng;
import com.rongji.egov.docconfig.business.service.DocOperatorLogMng;
import com.rongji.egov.flowutil.business.service.DocResourceMng;
import com.rongji.egov.maximunno.dao.EgovMaximumNoDao;
import com.rongji.egov.maximunno.model.EgovMaximumNo;
import com.rongji.egov.maximunno.service.EgovMaximumNoMng;
import com.rongji.egov.solrData.business.dao.SolrDataDao;
import com.rongji.egov.user.business.dao.UserDao;
import com.rongji.egov.user.business.model.RmsRole;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.business.model.UmsGroup;
import com.rongji.egov.user.business.model.vo.DraftUser;
import com.rongji.egov.user.business.util.SecurityUtils;
import com.rongji.egov.user.business.util.UserDraftUtils;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.rongji.egov.utils.api.paging.Sort;
import com.rongji.egov.utils.common.IdUtil;
import com.rongji.egov.utils.exception.BusinessException;
import com.rongji.egov.wflow.business.service.engine.manage.ProcessManageMng;
import com.rongji.egov.wflow.business.service.engine.transfer.TodoTransferMng;
import com.zjhousing.egov.proposal.business.constant.DocLogConstant;
import com.zjhousing.egov.proposal.business.dao.ReceivalDao;
import com.zjhousing.egov.proposal.business.model.EgovDocUpdateItemLog;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.model.Receival;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import com.zjhousing.egov.proposal.business.service.ReceivalMng;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author luzhangfei
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReceivalMngImpl implements ReceivalMng {
  @Resource
  private ReceivalDao receivalDao;
  @Resource
  private SolrDataDao solrDataDao;
  @Resource
  private EgovAttMng egovAttMng;
  @Resource
  private TodoTransferMng todoTransferMng;
  @Resource
  private DisUnitMng disUnitMng;
  @Resource
  private DocResourceMng docResourceMng;
  @Resource
  private DocOperatorLogMng operatorLogMng;
  @Lazy
  @Resource
  private ProposalMng proposalMng;
  @Resource
  private UserDao userDao;
  @Resource
  private EgovDocUpdateItemLogMngImpl egovDocUpdateItemLogMng;
  @Resource
  private RecSequenceDao recSequenceDao;
  @Resource
  private EgovMaximumNoDao egovMaximumNoDao;
  @Resource
  private EgovMaximumNoMng egovMaximumNoMng;
  @Resource
  private ProcessManageMng processManageMng;

  @Override
  public int insertReceival(Receival receival) {
    SecurityUser securityUser = SecurityUtils.getPrincipal();
    if (null != securityUser) {
      receival.setDraftUser(securityUser.getUserName());
      receival.setDraftUserNo(securityUser.getUserNo());
      DraftUser draftUser = UserDraftUtils.getDraftUser(securityUser.getUmsUser());
      receival.setDraftUserDeptNo(draftUser.getDraftDeptNo());
      receival.setDraftUserDept(draftUser.getDraftDeptName());

      receival.setDraftUserUnitNo(securityUser.getUnitNo());
      receival.setDraftUserUnit(securityUser.getUnitName());
    }
    receival.setDraftDate(new Date());
    receival.setFlowStatus("0");
    // 0未归电子公文库，1已归电子公文库
    receival.setArchiveFlag("0");

    if (StringUtils.isBlank(receival.getId())) {
      receival.setId(IdUtil.getUID());
    }
    // 添加查阅用户
    HashSet<String> readers = new HashSet<>();
    readers.add(receival.getDraftUserNo());
    receival.setReaders(readers);
    int result = this.receivalDao.insertReceival(receival);
    if (result < 1) {
      throw new BusinessException("登记收文失败");
    }
    this.disUnitMng.insertNoExistDisUnit(receival.getSystemNo(), receival.getSourceUnit(), receival.getFileCategory());
    // 添加数据到solr
    try {
      this.solrDataDao.add(receival.toSolrMap());
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessException("solr数据添加失败");
    }
    return result;
  }

  @Override
  public int deleteReceival(List<String> list) {
    List<Proposal> proposalList = new ArrayList<>();
    List<Receival> receivalList = this.receivalDao.getReceivalByIdList(list);
    for (Receival receival : receivalList) {
      this.cleanUpDocSequence(receival);
      this.operatorLogMng.insertOperatorLog("RECEIVAL", "收文", receival.getSubject(), DocLogConstant.LOG_DEL, receival.getId());

      String id = receival.getId();
      // 从solr中删除数据
      try {
        this.solrDataDao.delById(id);
      } catch (Exception e) {
        e.printStackTrace();
      }
      // 删除附件
      this.egovAttMng.deleteEgovAttByDocId(id);
      //删除收文关联
      this.docResourceMng.deleteDocResourceByDocId(id);
      //删除发文关联
      this.docResourceMng.deleteDocResourceByResourceDocId(id);
      // 删除发文-关联文件、收文文号、收文日期、文件来源字段值
      String relDocMark = receival.getRelDocMark();
      if (StringUtils.isNotBlank(relDocMark)) {
        String[] relRecFileArr = relDocMark.split(";");
        if (relRecFileArr.length > 1) {
          String docId = relRecFileArr[1];
          Proposal proposal = new Proposal(docId, null);
          proposalList.add(proposal);
        }
      }
    }
    //更新发文-关联文件的字段值
    if (proposalList.size() > 0) {
      this.proposalMng.batchUpdateProposalRelReceivalMark(proposalList);
    }
    return this.receivalDao.deleteReceival(list);
  }

  @Override
  public int updateReceival(Receival receival) {
    // 添加数据到solr
    try {
      this.solrDataDao.update(ATOMIC.SET, receival.toSolrMap());
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessException("solr数据添加失败");
    }
    Receival oldReceival = this.getReceivalById(receival.getId());
    //添加-重要信息修改（异步）
    this.addReceivalUpdateLog(receival, oldReceival);
    this.disUnitMng.insertNoExistDisUnit(receival.getSystemNo(), receival.getSourceUnit(), receival.getFileCategory());
    return this.receivalDao.updateReceival(receival);
  }


  @Override
  public int updateReturnFlagByIds(List<String> list) {
    return this.receivalDao.updateReturnFlagByIds(list);
  }

  @Override
  public JSONObject updateDealForm(String oldArr, String name, String id, String startTime) {
    JSONObject result = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    result.put("flag", true);
    try {
      if (StringUtils.isNotBlank(oldArr)) {
        try {
          jsonArray = JSON.parseArray(oldArr);
        } catch (Exception e) {
          e.printStackTrace();
          jsonArray = JSON.parseArray("[]");
        }

      }
      int dealFormSize = jsonArray.size();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date date = sdf.parse(startTime);
      //新增的阅办单时间
      Long nowTime = date.getTime();
      if (dealFormSize == 0) {
        JSONObject newDealForm = new JSONObject();
        newDealForm.put("fileId", id);
        newDealForm.put("fileName", name);
        newDealForm.put("startTime", nowTime);
        jsonArray.add(newDealForm);
        result.put("list", jsonArray);
        result.put("listStr", jsonArray.toJSONString());
        return result;
      }
      //原有的最后一个阅办单
      JSONObject last = (JSONObject) jsonArray.get(dealFormSize - 1);
      //原有的第一个阅办单
      JSONObject first = (JSONObject) jsonArray.get(0);
      //当前阅办单时间最新或最早
      if (last.getLong("startTime") < nowTime || first.getLong("startTime") > nowTime) {
        //添加新阅办单
        JSONObject newDealForm = new JSONObject();
        newDealForm.put("fileId", id);
        newDealForm.put("fileName", name);
        newDealForm.put("startTime", nowTime);
        if (last.getLong("startTime") < nowTime) {//当前阅办单最新
          //更新旧阅办单最后一个截止时间（由于阅办单可能多个，遍历）
          boolean flag = true;
          for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject temp = (JSONObject) jsonArray.get(i);
            if (null == temp.get("endTime")) {
              temp.put("endTime", nowTime);
            }
          }

          jsonArray.add(newDealForm);
        } else {//当前阅办单最旧
          newDealForm.put("endTime", first.getLong("startTime"));
          jsonArray.add(0, newDealForm);
        }
        //时间一致.
      } else if (last.getLong("startTime").equals(nowTime) || first.getLong("startTime").equals(nowTime)) {
        //添加新阅办单
        JSONObject newDealForm = new JSONObject();
        newDealForm.put("fileId", id);
        newDealForm.put("fileName", name);
        newDealForm.put("startTime", nowTime);
        //跟最旧时间一致
        if (first.getLong("startTime").equals(nowTime)) {
          newDealForm.put("endTime", first.getLong("endTime"));
          jsonArray.add(0, newDealForm);
        } else {
          jsonArray.add(newDealForm);
        }

      } else {//新增的阅办单时间在旧阅办单之间,插入到列表中并更新时间
        JSONArray newArr = new JSONArray();
        for (int i = 0; i < dealFormSize - 1; i++) {
          JSONObject temp = (JSONObject) jsonArray.get(i);
          JSONObject newDealForm = new JSONObject();
          newDealForm.put("fileId", id);
          newDealForm.put("fileName", name);
          newDealForm.put("startTime", nowTime);
          if (temp.getLong("startTime").equals(nowTime)) {
            newDealForm.put("endTime", temp.getLong("endTime"));
            newArr.add(temp);
            newArr.add(newDealForm);
          } else if (temp.getLong("startTime") < nowTime && nowTime < temp.getLong("endTime")) {
            newDealForm.put("endTime", temp.getLong("endTime"));
            temp.put("endTime", nowTime);
            newArr.add(temp);
            newArr.add(newDealForm);
          } else {
            newArr.add(temp);
          }
        }
        newArr.add(jsonArray.get(dealFormSize - 1));
        jsonArray = newArr;
      }
      result.put("list", jsonArray);
      result.put("listStr", jsonArray.toJSONString());
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessException("添加阅办单出错");
    }
    return result;
  }

  @Override
  public Receival getReceivalById(String id) {
    return this.receivalDao.getReceivalById(id);
  }

  @Override
  public JSONObject getReceivalDetailById(String docId, String aid) {
    JSONObject result = new JSONObject();
    ObjectMapper mapper = new ObjectMapper();
    mapper.setTimeZone(TimeZone.getDefault());
    Receival receival = this.receivalDao.getReceivalById(docId);
    if (null == receival) {
      throw new BusinessException("文件可能被异常删除");
    }
    List<EgovAtt> egovAtts = this.egovAttMng.getEgovAttsByDocId(docId, false);
    JSONObject permission = null;
    // 不为拟稿文件
    if (!StringUtils.equals("0", receival.getFlowStatus())) {
      try {
        permission = this.todoTransferMng.getProcessPermission(aid, receival.toMap());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    try {
      result.put("form", JSON.parse(mapper.writeValueAsString(receival)));
      result.put("atts", JSON.parse(mapper.writeValueAsString(egovAtts)));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    result.put("permission", permission);
    return result;
  }

  @Override
  public Receival getReceival4docMark(String docMark) {
    return this.receivalDao.getReceival4docMark(docMark);
  }

  @Override
  public Page<Receival> getReceival4Page(PagingRequest<Receival> paging, Receival receival, String[] word) {
    return this.receivalDao.getReceival4Page(paging, receival, word);
  }

  @Override
  public Page<SolrDocument> getReceivalBySolr(PagingRequest paging, Receival receival, String word, Integer draftYear, Integer draftMonth, Integer draftDay) {
    SecurityUser securityUser = SecurityUtils.getPrincipal();
    Page<SolrDocument> page = new Page<>();
    StringBuffer sqStr = new StringBuffer();
    List<RmsRole> roles = userDao.listUserRole(securityUser.getUserNo(), securityUser.getSystemNo());
    Boolean manage = false;
    for (RmsRole role : roles) {
      if ("receival_manager".equals(role.getRoleNo()) || "sys_manager".equals(role.getRoleNo())) {
        manage = true;
      }
    }
    if (manage) {
      sqStr.append("R_readers:*");
    } else {
      // 默认必须参数
      sqStr.append("(R_readers:" + securityUser.getUserNo());
      List<UmsGroup> groups = this.userDao.listUserGroup(securityUser.getUserNo(), securityUser.getSystemNo());
      for (UmsGroup group : groups) {
        sqStr.append(" OR R_readers:" + group.getGroupNo());
      }
      sqStr.append(" OR R_readers:" + securityUser.getOrgNo() + ")");
    }



    // word关键字模糊查询
    if (StringUtils.isNotBlank(word)) {
      StringBuffer sqStrWord = new StringBuffer();
      String[] strings = word.trim().split("\\s+");
      if (strings != null && strings.length > 0) {
        for (int i = strings.length - 1; i >= 0; i--) {
          sqStrWord.append(" OR S_subject2:*" + strings[i] + "*");
          sqStrWord.append(" OR S_docMark:*" + strings[i] + "*");
          if (receival.isFulltext()) {
            sqStrWord.append(" OR BODY_zw:" + strings[i]);
          }
          sqStrWord.append(" OR S_sourceUnit:*" + strings[i] + "*");
        }
        sqStrWord.replace(0,3," AND (");
        sqStrWord.append(" )");
        sqStr.append(sqStrWord);
      }
    }
    // 收文查询字段
    if (StringUtils.isNotBlank(receival.getSystemNo())) {
      sqStr.append(" AND S_systemNo:" + receival.getSystemNo());
    }
    if (StringUtils.isNotBlank(receival.getSubject())) {
      sqStr.append(" AND S_subject2:*" + receival.getSubject() + "*");
    }
    if (StringUtils.isNotBlank(receival.getDocMark())) {
      sqStr.append(" AND S_docMark:*" + receival.getDocMark() + "*");
    }
    if (StringUtils.isNotBlank(receival.getDocSequence())) {
      sqStr.append(" AND S_docSequence:*" + receival.getDocSequence() + "*");
    }
    if (StringUtils.isNotBlank(receival.getSourceUnit())) {
      sqStr.append(" AND S_sourceUnit:*" + receival.getSourceUnit() + "*");
    }
    if (StringUtils.isNotBlank(receival.getUrgenLevel())) {
      sqStr.append(" AND S_urgenLevel:*" + receival.getUrgenLevel() + "*");
    }
    if (StringUtils.isNotBlank(receival.getSecLevel())) {
      sqStr.append(" AND S_secLevel:*" + receival.getSecLevel() + "*");
    }
    if (StringUtils.isNotBlank(receival.getArchiveFlag())) {
      sqStr.append(" AND S_archiveFlag:*" + receival.getArchiveFlag() + "*");
    }
    if (null != receival.getArchiveType()) {
      sqStr.append(" AND S_archiveType:*" + receival.getArchiveType() + "*");
    }
    if (StringUtils.isNotBlank(receival.getHandleUnit())) {
      sqStr.append(" AND S_handleUnit:*" + receival.getHandleUnit() + "*");
    }
    if (StringUtils.isNotBlank(receival.getFileCategory())) {
      sqStr.append(" AND S_fileCategory:*" + receival.getFileCategory() + "*");
    }
    if (null != draftYear) {
      sqStr.append(" AND I_draftYear:" + draftYear);
    }
    if (null != draftMonth) {
      sqStr.append(" AND I_draftMonth:" + draftMonth);
    }
    if (null != draftDay) {
      sqStr.append(" AND I_draftDay:" + draftDay);
    }

    // 流程相关查询字段
    if (StringUtils.isNotBlank(receival.getFlowStatus())) {
      sqStr.append(" AND S_flowStatus:*" + receival.getFlowStatus() + "*");
    }
//        sqStr.append(" AND -S_flowStatus:*8*");
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    String beginStr = "*";
    String endStr = "*";

    if (null != receival.getBeginDate()) {
      beginStr = sdf.format(receival.getBeginDate());
    }
    if (null != receival.getEndDate()) {
      endStr = sdf.format(receival.getEndDate());
    }
    sqStr.append(" AND T_draftTime:[" + beginStr + " TO " + endStr + "]");

    SolrQuery sq = new SolrQuery();
    sq.addFilterQuery("S_module:RECEIVAL");
    sq.setQuery(sqStr.toString());

    if (paging.getSort() != null && ((Sort.Order) paging.getSort().getOrders().get(0)).getDirection().name().equals("ASC")) {
      sq.addSort(((Sort.Order) paging.getSort().getOrders().get(0)).getProperty(), SolrQuery.ORDER.asc);
    } else if (paging.getSort() != null) {
      sq.addSort(((Sort.Order) paging.getSort().getOrders().get(0)).getProperty(), SolrQuery.ORDER.desc);
    }

    QueryResponse queryResult = this.solrDataDao.queryAll(sq, paging.getOffset(), paging.getLimit());
    page.setList(queryResult.getResults());
    page.setTotal(queryResult.getResults().getNumFound());
    return page;
  }

  @Override
  public Proposal transferToProposalMotion(String id) {
    Receival receival = this.receivalDao.getReceivalById(id);
    if (null == receival) {
      throw new BusinessException("文件不存在");
    }
    SecurityUser user = SecurityUtils.getPrincipal();
    if (null == user) {
      throw new BusinessException("用户信息为空");
    }

    //1、保存正文信息
    Proposal proposal = new Proposal();
    String tagetId = IdUtil.getUID();
    proposal.setId(tagetId);
    //文件字号
    proposal.setDocMark("");
    //拟稿单位
    proposal.setDraftUnit(user.getUnitName());
    //拟稿人
    proposal.setDraftUserName(user.getUserName());
    //拟稿人编码
    proposal.setDraftUserNo(user.getUserNo());
    DraftUser draftUser = UserDraftUtils.getDraftUser(user.getUmsUser());
    //拟稿人部门
    proposal.setDraftDept(draftUser.getDraftDeptName());
    //拟稿人部门编码
    proposal.setDraftDeptNo(draftUser.getDraftDeptNo());
    //文件标题
    proposal.setSubject(receival.getSubject());
    //内容缓急
    proposal.setUrgentLevel(receival.getUrgenLevel());
    //文件密级
    proposal.setSecLevel(receival.getSecLevel());
    //文件种类
    proposal.setDocType(receival.getDocType());
    //关联文件
    proposal.setRelReceivalMark("RECEIVAL;" + receival.getId() + ";" + receival.getSubject());
    //文件类型
    proposal.setDocCate("");
    //归档类型
    proposal.setArchiveType(receival.getArchiveType());
    //拟稿日期
    proposal.setDraftDate(new Date());
    // 设置默认值
    // 局务公开标识
//        proposal.setJwPublicFlag("否");
//        // 党务公开标识
//        proposal.setDwPublicFlag("否");
    // 页数
    proposal.setPageNum(receival.getPageNum());
    //印发份数
    proposal.setPrintNum(1);
    //公开属性
//        proposal.setPublicFlag(PublicFlagEnum.PUBLIC);
    //2、保存附件信息
    //this.egovAttMng.copyEgovAttByDocId(receival.getId(), tagetId);
//    //保密期限
//    proposal.setSecureTerm(receival.getSecureTerm());
//    //定密依据
//    proposal.setSecLevelBase(receival.getSecLevelBase());
//    //分发份数
//    proposal.setSendNum(receival.getCopyNum());
    return proposal;
  }

  @Override
  public int updateSwapReturnById(String id, String swapReturn) {
    return this.receivalDao.updateSwapReturnById(id, swapReturn);
  }

  @Override
  public List<String> isDuplicateWithModel(Receival receival) {
    return this.receivalDao.isDuplicateWithModel(receival);
  }

  @Override
  public int batchUpdateReceivalRelDocMark(List<Receival> list) {
    return this.receivalDao.batchUpdateReceivalRelDocMark(list);
  }

  /**
   * 添加重要信息修改日志
   *
   * @param newReceival
   * @param oldReceival
   */
  private void addReceivalUpdateLog(Receival newReceival, Receival oldReceival) {
    if (null != newReceival && null != oldReceival) {
      SecurityUser user = SecurityUtils.getPrincipal();
      String docId = newReceival.getId();
      if (!oldReceival.getId().equals(docId)) {
        throw new BusinessException("此文件不存在");
      }
      List<EgovDocUpdateItemLog> list = new ArrayList<>();
      String oldSubject = oldReceival.getSubject();
      String newSubject = newReceival.getSubject();
      if (!StringUtils.equals(oldSubject, newSubject)) {
        EgovDocUpdateItemLog egovDocUpdateItemLog = new EgovDocUpdateItemLog(docId, "标题", oldSubject, newSubject, DocLogConstant.RECEIVAL_MODULE_NO);
        list.add(egovDocUpdateItemLog);
      }
      String oldSecLevel = oldReceival.getSecLevel();
      String newSecLevel = newReceival.getSecLevel();
      if (!StringUtils.equals(oldSecLevel, newSecLevel)) {
        EgovDocUpdateItemLog egovDocUpdateItemLog = new EgovDocUpdateItemLog(docId, "密级", oldSecLevel, newSecLevel, DocLogConstant.RECEIVAL_MODULE_NO);
        list.add(egovDocUpdateItemLog);
      }
      if (list.size() > 0) {
        this.egovDocUpdateItemLogMng.batchInsertEgovDocUpdateItemLog(list, user);
      }
    }
  }

  @Override
  public void cleanUpDocSequence(Receival receival) {
    // 判断 receival.getDocSequenceNum() != null  因为就数据没有 docSequenceNum  字段
    if (StringUtils.isNotBlank(receival.getDocSequence()) && receival.getDocSequenceNum() != null) {
      RecSequence sequence = this.recSequenceDao.getSequenceByDocCateAndDealReadCate(receival.getDocCate(), receival.getHandleOrView(), receival.getSystemNo());
      if (sequence != null) {
        //得到组织
        SecurityUser securityUser = SecurityUtils.getPrincipal();
        //获取最大值依据是否需要单位编号
        //处室流水-最大值数据保存部门编号
        String egovMaximumOrgNo = securityUser.getOrgNo();
        //厅流水时-最大值数据不保存单位编号
        if ("1".equals(sequence.getSequenceCate())) {
          egovMaximumOrgNo = null;
        }
        this.egovMaximumNoMng.cleanUseNo(receival.getDocSequenceNum(), sequence.getId(), receival.getDocSequenceYear(), egovMaximumOrgNo);
        receival.setDocSequence("");
        receival.setDocSequenceNum(-1);
        receival.setDocSequenceYear(-1);
      }
    }
  }

  /**
   * 获取收文流水号页面展示数据
   * @param docCate 文件类型
   * @param handleOrView 办件、阅件标识
   * @param systemNo
   * @param docSequenceYear
   * @param docSequenceNum
   * @return
   */
  @Override
  public JSONObject getDocSequencePageJson (String docCate, String handleOrView, String systemNo, Integer docSequenceYear, Integer docSequenceNum) {
    if (StringUtils.isBlank(systemNo)) {
      throw new BusinessException("系统编码不能为空");
    }

    JSONObject jsonObject = new JSONObject();

    Integer maxNo;
    RecSequence sequence = this.recSequenceDao.getSequenceByDocCateAndDealReadCate(docCate, handleOrView, systemNo);
    if (null == sequence) {
      throw new BusinessException("流水号未配置");
    } else if ("0".equals(sequence.getStatus())) {
      throw new BusinessException("流水号已停用");
    }

    //获取当前年份
    Calendar calendar = Calendar.getInstance();
    if (null == docSequenceYear) {
      docSequenceYear = calendar.get(Calendar.YEAR);
    }
    //得到组织
    SecurityUser securityUser = SecurityUtils.getPrincipal();
    String deptNo = securityUser.getOrgNo();

    //获取最大值依据是否需要单位编号
    //处室流水-最大值数据保存部门编号
    String egovMaximumOrgNo = securityUser.getOrgNo();
    //厅流水时-最大值数据不保存单位编号
    if ("1".equals(sequence.getSequenceCate())) {
      egovMaximumOrgNo = null;
    }

    maxNo = this.egovMaximumNoMng.getMaximumNo(sequence.getId(), docSequenceYear, egovMaximumOrgNo, sequence.getInitValue()).getMaxNo();
    if (docSequenceNum == null) {
      docSequenceNum = maxNo;
    }
//        this.egovMaximumNoMng.updateMaxAndUsedNos(maxNo, sequence.getId(), year, egovMaximumOrgNo);

    String modeStr = sequence.getSequenceMode();
    if (StringUtils.isBlank(modeStr)) {
      throw new BusinessException("流水号模式未配置");
    }

    // 获取漏号
    Set<Integer> loseNumSet = this.egovMaximumNoMng.getLoseNum(sequence.getInitValue(), sequence.getId(), docSequenceYear, egovMaximumOrgNo);

    int length = sequence.getSequenceLength();

    String prefixRegex = "(\\{|\\[|【|\\(|（|〔)";
    String suffixRegex = "(\\}|\\]|】|\\)|）|〕)";
    String yearRegex = prefixRegex + "年度" + suffixRegex;
    String noRegex = prefixRegex + "序号" + suffixRegex;
    String orgNoRegex = prefixRegex + "组织编码" + suffixRegex;
    modeStr = modeStr.replaceAll(yearRegex, String.valueOf(docSequenceYear));
    modeStr = modeStr.replaceAll(orgNoRegex, StringUtils.trimToEmpty(deptNo));
    modeStr = modeStr.replaceAll(noRegex, StringUtils.leftPad(docSequenceNum.toString(), length, "0"));

    jsonObject.put("docSequence", modeStr);
    jsonObject.put("docSequenceYear", docSequenceYear);
    jsonObject.put("docSequenceNum", docSequenceNum);
    jsonObject.put("noUsedMaxNo", maxNo);
    jsonObject.put("loseNum", StringUtils.join(loseNumSet, ","));
    return jsonObject;
  }

    /*public String getDocSequenceLoseNum (String docCate, String handleOrView, String systemNo, Integer docSequenceYear, Integer docSequenceNum) {
        RecSequence sequence = this.recSequenceDao.getSequenceByDocCateAndDealReadCate(docCate, handleOrView, systemNo);
        if (null == sequence) {
            throw new BusinessException("流水号未配置");
        } else if ("0".equals(sequence.getStatus())) {
            throw new BusinessException("流水号已停用");
        }
        Calendar calendar = Calendar.getInstance();
        if (null == docSequenceYear) {
            docSequenceYear = calendar.get(Calendar.YEAR);
        }
        SecurityUser securityUser = SecurityUtils.getPrincipal();
        String egovMaximumOrgNo = securityUser.getOrgNo();
        //厅流水时-最大值数据不保存单位编号
        if ("1".equals(sequence.getSequenceCate())) {
            egovMaximumOrgNo = null;
        }
        Set<Integer> loseNumSet = this.egovMaximumNoMng.getLoseNum(sequence.getInitValue(), sequence.getId(), docSequenceYear, egovMaximumOrgNo);
        return StringUtils.join(loseNumSet, ",");
    }*/

  /**
   * 获取收文流水号页面编号
   * @param docCate 文件类型
   * @param handleOrView 办件、阅件标识
   * @param systemNo
   * @param docSequence
   * @param docSequenceYear
   * @param docSequenceNum
   * @param docId
   */
  @Override
  public void serialDocSequenceNum (String docCate, String handleOrView, String systemNo, String docSequence, Integer docSequenceYear, Integer docSequenceNum, String docId) {
    RecSequence sequence = this.recSequenceDao.getSequenceByDocCateAndDealReadCate(docCate, handleOrView, systemNo);
    if (null == sequence) {
      throw new BusinessException("流水号未配置");
    } else if ("0".equals(sequence.getStatus())) {
      throw new BusinessException("流水号已停用");
    }
    SecurityUser securityUser = SecurityUtils.getPrincipal();
    //获取最大值依据是否需要单位编号
    //处室流水-最大值数据保存部门编号
    String egovMaximumOrgNo = securityUser.getOrgNo();
    //厅流水时-最大值数据不保存单位编号
    if ("1".equals(sequence.getSequenceCate())) {
      egovMaximumOrgNo = null;
    }
    if (this.egovMaximumNoMng.isExistInUsedNos(docSequenceNum, sequence.getId(), docSequenceYear, egovMaximumOrgNo)) {
      throw new BusinessException("编号失败，当前序号已被使用，请勿重复使用！");
    }
    Receival receival = this.getReceivalById(docId);
    if (StringUtils.isNotBlank(receival.getDocSequence())) {
      // 清号
      this.cleanOrserialNum(receival.getDocSequenceNum(), receival.getDocSequenceYear(), sequence.getId(), egovMaximumOrgNo, sequence.getInitValue(), false);
    }
    //  编号
    this.cleanOrserialNum(docSequenceNum, docSequenceYear, sequence.getId(), egovMaximumOrgNo, sequence.getInitValue(), true);
    receival.setDocSequence(docSequence);
    receival.setDocSequenceYear(docSequenceYear);
    receival.setDocSequenceNum(docSequenceNum);
    this.processManageMng.updateTodo(receival.toMap());
    this.updateReceival(receival);
  }



  /**
   * 清空或者编号
   * @param num
   * @param year
   * @param bindId
   * @param deptNo
   * @param initValue 对应配置的初始值
   * @param isSerial true 表示当前进行编流水号操作
   */
  public void cleanOrserialNum (Integer num, Integer year, String bindId, String deptNo, Integer initValue, boolean isSerial) {
    EgovMaximumNo maximumNo = this.egovMaximumNoMng.getMaximumNo(bindId, year, deptNo, initValue);
    Set<Integer> usedNos = maximumNo.getUsedNos();
    if (isSerial) {
      // 编号
      if (usedNos == null) {
        usedNos = new HashSet<>();
        maximumNo.setUsedNos(usedNos);
      }
      usedNos.add(num);
    } else {
      // 清号
      if (usedNos != null) {
        usedNos.remove(num);
      }
    }
    //默认为 已使用序号集合最大值+1
    //首次根据文件字编号，已使用过的序号集为空
    Integer maximun;
    if (null != usedNos && usedNos.size() > 0) {
      maximun = Collections.max(usedNos) + 1;
    } else {
      maximun = initValue;
    }
    maximumNo.setMaxNo(maximun);
    if (egovMaximumNoDao.updateMaximumNo(maximumNo) <= 0) {
      throw new BusinessException("更新最大值记录数据出错");
    }
  }
}
