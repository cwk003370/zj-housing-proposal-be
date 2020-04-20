package com.zjhousing.egov.proposal.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.attachutil.model.EgovAtt;
import com.rongji.egov.attachutil.service.EgovAttMng;
import com.rongji.egov.doc.business.DocBusinessProperties;
import com.rongji.egov.doc.business.constant.ArchiveConstant;
import com.rongji.egov.doc.business.constant.DepartmentConstant;
import com.rongji.egov.doc.business.constant.ExternalToOthersConstant;
import com.rongji.egov.doc.business.constant.TaskLibraryConstant;
import com.rongji.egov.doc.business.external.query.CommonToOthersQuery;
import com.rongji.egov.doc.business.external.query.DealForm;
import com.rongji.egov.doc.business.external.service.ComToOthersMng;
import com.rongji.egov.doc.business.external.util.ArchiveUtil;
import com.rongji.egov.doc.business.external.util.WebServiceUtil;
import com.rongji.egov.flowrelation.business.constant.FlowTypeConstant;
import com.rongji.egov.flowrelation.business.constant.UnitTypeConstant;
import com.rongji.egov.flowrelation.business.model.FlowRelation;
import com.rongji.egov.flowrelation.business.service.FlowRelationMng;
import com.rongji.egov.user.business.dao.RmsParamDao;
import com.rongji.egov.user.business.dao.UserDao;
import com.rongji.egov.user.business.ex.ExCommon;
import com.rongji.egov.user.business.model.*;
import com.rongji.egov.user.business.util.SecurityUtils;
import com.rongji.egov.utils.common.IdUtil;
import com.rongji.egov.utils.exception.BusinessException;
import com.rongji.egov.wflow.business.dao.engine.FlowWorkItemInstanceDao;
import com.rongji.egov.wflow.business.dao.engine.FlowWorkTodoDao;
import com.rongji.egov.wflow.business.model.po.engine.FlowWorkTodo;
import com.zjhousing.egov.proposal.business.constant.ReceivalConstant;
import com.zjhousing.egov.proposal.business.model.Proposal;

import com.zjhousing.egov.proposal.business.query.ProToOthersQuery;
import com.zjhousing.egov.proposal.business.service.ProToOthersMng;
import com.zjhousing.egov.proposal.business.service.ProposalMng;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.rongji.egov.doc.business.constant.ExternalToOthersConstant.*;
/**
 * 提案转其他文 mng impl
 *
 * @author lindongmei
 * @date 2018/10/31
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProToOthersMngImpl implements ProToOthersMng {

  @Resource
  private ProposalMng proposalMng;
  @Resource
  private RestTemplate withTokenRestTemplate;
  @Resource
  private EgovAttMng egovAttMng;
  @Resource
  private RmsParamDao rmsParamDao;
  @Resource
  private DocBusinessProperties docBusinessProperties;
  @Resource
  private FlowWorkTodoDao flowWorkTodoDao;
  @Resource
  private UserDao userDao;
  @Resource
  private ComToOthersMng comToOthersMng;
  @Resource
  private FlowRelationMng flowRelationMng;

  @Override
  public boolean proToOthers(ProToOthersQuery proToOthersQuery) {
    HashMap<String, Object> map;
    boolean fianl = true;
    JSONObject result;
    String docId = proToOthersQuery.getDocId();
    Proposal pro = this.proposalMng.getProposalMotionById(docId);
    final String MODULE_NO = "PROPOSALMOTION";
    JSONObject turnNumJson = new JSONObject();
    int turnNumInt = -1;
    if (null == pro) {
      throw new BusinessException("文件不存在");
    }
    //  转重要文件、转工作提醒次数 json格式的字符串"{ExternalToOthersConstant.TO_WORK_REMINDER: 转工作提醒次数, ExternalToOthersConstant.TO_VITAL_DOCUMENT: 转重要文件次数}"
    String turnNum = pro.getTurnNum();
    switch (proToOthersQuery.getType()) {
      case ExternalToOthersConstant.TO_ARCHIVE:
        map = this.getProToArchiveHashMap(pro, proToOthersQuery);
        result = this.withTokenRestTemplate.postForObject(this.docBusinessProperties.getRequestPrefix() + "/archive/insertOthersToArchive", map, JSONObject.class);
        if (ExCommon.getFlag(result)) {
          Proposal proposal = new Proposal();
          proposal.setId(docId);
          proposal.setArchiveFlag("1");
          if (this.proposalMng.updateProposalMotion(proposal) < 1) {
            fianl = false;
          }
        }
        break;
      case ExternalToOthersConstant.TO_DEPARTMENT:
        map = this.getToDepartmentHashMap(pro, proToOthersQuery);
        JSONObject jsonObject = this.withTokenRestTemplate.postForObject(this.docBusinessProperties.getRequestPrefix() + "/deptReceival/insertFileToDeptReceival", map, JSONObject.class);
        boolean res = ExCommon.getFlag(jsonObject);
        if (!res) {
          throw new BusinessException(jsonObject.getString("msg"));
        }
        fianl = res;
        break;
      case ExternalToOthersConstant.INNER_RECEIVAL:
        String deptNo = proToOthersQuery.getDeptNo();
        boolean recResult =false;
        if (StringUtils.isBlank(deptNo)) {
          throw new BusinessException("请选择部门");
        }
        String[] deptNos = deptNo.split(";");
        for(String deptId :deptNos){
          proToOthersQuery.setDeptNo(deptId);
          map = this.getToReceivalHashMap(pro, proToOthersQuery);
          JSONObject recJsonObject = this.withTokenRestTemplate.postForObject(this.docBusinessProperties.getRequestPrefix() + "/receival/insertOtherToReceival", map, JSONObject.class);
          if (!"1".equals(recJsonObject.getString("status"))) {
            throw new BusinessException(recJsonObject.getString("msg"));
          }else {
            recResult = true;
          }
          //添加流程关系
          String mainDocId =map.get(ReceivalConstant.RESOURCE_ID).toString();
          String recTargetId = map.get(ReceivalConstant.ID).toString();
          FlowRelation flowRelation = new FlowRelation();
          flowRelation.setId(IdUtil.getUID());
          flowRelation.setParentDocId(mainDocId);
          flowRelation.setSonDocId(recTargetId);
          flowRelation.setParentModuleNo(MODULE_NO);
          flowRelation.setSonModuleNo("RECEIVAL");
          flowRelation.setFlowType(FlowTypeConstant.TO_READ);
          flowRelation.setCreateTime((Date) map.get(ReceivalConstant.DRAFT_DATE));
          flowRelation.setSonDept(map.get(ReceivalConstant.DRAFT_USER_DEPT).toString());
          flowRelation.setSonDeptNo(map.get(ReceivalConstant.DRAFT_USER_DEPT_NO).toString());
          flowRelation.setFlowVersion(Long.toString(System.currentTimeMillis()));
          flowRelation.setUnitType(UnitTypeConstant.MAIN);
          try{
            flowRelationMng.addFlowRelationToFlow(flowRelation);
          }catch (Exception e) {
            e.printStackTrace();
          }
          //拷贝正文
          this.egovAttMng.copyEgovAttByDocId(mainDocId,recTargetId, "attach", "main_doc");
        }
        fianl = recResult;
        break;
      case ExternalToOthersConstant.TO_OFFICE:
        break;
      case ExternalToOthersConstant.TO_PUBLIC:
        break;
      case TO_FILE:
        //保存办理单为附件
        if (proToOthersQuery.getDealForm() != null && proToOthersQuery.getDealForm().size() > 0) {
          SecurityUser user = SecurityUtils.getPrincipal();
          CommonToOthersQuery dealFormCtq = new CommonToOthersQuery(docId, MODULE_NO, user, proToOthersQuery.getDealForm().get(0));
          this.comToOthersMng.insertDealFormToAtt(dealFormCtq);
        }
        HashMap<String, Object> hashMap = this.getProToFileXml(pro, proToOthersQuery);
        String xml = (String) hashMap.get("xml");
        System.out.println("发送的数据：\n\r" + xml);
        String toFileWsdl = this.docBusinessProperties.getToFileWsdl();
        if (StringUtils.isBlank(toFileWsdl)) {
          throw new BusinessException("归档案wsdl地址为空，请检查配置！");
        }
        try {
          String re = WebServiceUtil.executeWsRequest(toFileWsdl, TO_FILE_METHOD_NAME, xml);
          final String FAIL = "fail";
          if (FAIL.equals(re)) {
            fianl = false;
          }
        } catch (Exception e) {
          e.printStackTrace();
          throw new BusinessException("收文归档案异常");
        } finally {
          List<String> attachId = (ArrayList) hashMap.get("attachId");
          if (null != attachId && attachId.size() > 0) {
            this.egovAttMng.deleteEgovAtt(attachId);
          }
        }
        break;
      case TO_TASK_LIBRARY:
        if (StringUtils.isBlank(pro.getTaskFlag()) || !("1".equals(pro.getTaskFlag().trim()))) {
          String targetId = IdUtil.getUID();
          //保存办理单为附件
          if (proToOthersQuery.getDealForm() != null && proToOthersQuery.getDealForm().size() > 0) {
            SecurityUser user = SecurityUtils.getPrincipal();
            CommonToOthersQuery dealFormCtq = new CommonToOthersQuery(targetId, MODULE_NO, user, proToOthersQuery.getDealForm().get(0));
            this.comToOthersMng.insertDealFormToAtt(dealFormCtq);
          }
          //拷贝附件
          this.egovAttMng.copyEgovAttByDocId(pro.getId(), targetId);
          map = this.getProToTaskLibraryHashMap(pro, targetId);
          // 关注领导编码
          map.put(TaskLibraryConstant.FOLLOW_LEADER_NO, "");
          // 关注领导名称
          map.put(TaskLibraryConstant.FOLLOW_LEADER_NAME, "");
          // 签发意见
          map.put(TaskLibraryConstant.SPECIFIC_CONTENT_OPINION, "");
          Set<String> readers = proToOthersQuery.getReaders();
          Iterator<String> iterator = readers.iterator();
          while (iterator.hasNext()) {
            String next = iterator.next();
            JSONObject jsonObj = JSONObject.parseObject(next);
            String singUserNo = jsonObj.getString("singUserNo");
            map.put(TaskLibraryConstant.FOLLOW_LEADER_NO, singUserNo);
            UmsUser umsUser = this.userDao.getUmsUser(singUserNo);
            map.put(TaskLibraryConstant.FOLLOW_LEADER_NAME, umsUser != null ? umsUser.getUserName().toString() : "");
            map.put(TaskLibraryConstant.SPECIFIC_CONTENT_OPINION, jsonObj.getString("opinionContent"));
            break;
          }
          this.withTokenRestTemplate.postForObject(this.docBusinessProperties.getRequestPrefix() + "/efficiencySupervision/insertExternalTask", map, JSONObject.class);
          Proposal proposal = new Proposal();
          proposal.setId(docId);
          proposal.setTaskFlag("1");
          if (this.proposalMng.updateProposalMotion(proposal) < 1) {
            fianl = false;
          }
        }
        break;
      case TO_WORK_REMINDER:
        if (turnNum != null && !StringUtils.equals(turnNum.trim(), "")) {
          turnNumJson = JSONObject.parseObject(turnNum);
          String turnNumString = turnNumJson.getString(TO_WORK_REMINDER);
          if (turnNumString != null && !StringUtils.equals(turnNumString.trim(), "")) {
            int i = Integer.parseInt(turnNumString);
            if (i >= 1) {
              throw new BusinessException(TO_WORK_REMINDER + "不能转两次，请联系管理员！");
            } else {
              turnNumInt = i;
            }
          }
        }
        String targetId = IdUtil.getUID();
        //拷贝正文
        this.egovAttMng.copyEgovAttByDocId(pro.getId(), targetId, "main_doc", "main_doc");
        //拷贝普通附件
        this.egovAttMng.copyEgovAttByDocId(pro.getId(), targetId, "attach", "attach");
        //保存办理单为附件
        if (proToOthersQuery.getDealForm() != null && proToOthersQuery.getDealForm().size() > 0) {
          SecurityUser user = SecurityUtils.getPrincipal();
          CommonToOthersQuery dealFormCtq = new CommonToOthersQuery(targetId, MODULE_NO, user, proToOthersQuery.getDealForm().get(0));
          this.comToOthersMng.insertDealFormToAtt(dealFormCtq);
        }
        map = new HashMap<>(16);
        map.put("id", targetId);
        map.put("subject", pro.getSubject());
        map.put("draftUser", pro.getDraftUserName());
        map.put("draftUserNo", pro.getDraftUserNo());
        map.put("draftDept", pro.getDraftDept());
        map.put("draftDeptNo", pro.getDraftDeptNo());
        map.put("draftDate", pro.getDraftDate());
        map.put("relateId", pro.getId());
        map.put("systemNo", pro.getSystemNo());
        map.put("moduleNo", MODULE_NO);
        map.put("category", proToOthersQuery.getIdList().get(0));
//                HttpHeaders header = new HttpHeaders();
        result= this.withTokenRestTemplate.postForObject(this.docBusinessProperties.getRequestPrefix() + "/workReminder/toWorkReminder", map, JSONObject.class);
        if (result != null && "1".equals(result.getString("status"))) {
          Proposal proposal = new Proposal();
          proposal.setId(docId);
          if (turnNumInt == -1) {
            turnNumJson.put(TO_WORK_REMINDER, 1);
          } else {
            turnNumJson.put(TO_WORK_REMINDER, turnNumInt + 1);
          }
          proposal.setTurnNum(turnNumJson.toJSONString());
          if (this.proposalMng.updateProposalMotion(proposal) < 1) {
            fianl = false;
          } else {
            fianl = true;
          }
        } else {
          throw new BusinessException(result.getString("message"));
        }
        break;
      case TO_VITAL_DOCUMENT:
        if (turnNum != null && !StringUtils.equals(turnNum.trim(), "")) {
          turnNumJson = JSONObject.parseObject(turnNum);
          String turnNumString = turnNumJson.getString(TO_VITAL_DOCUMENT);
          if (turnNumString != null && !StringUtils.equals(turnNumString.trim(), "")) {
            int i = Integer.parseInt(turnNumString);
            if (i >= 1) {
              throw new BusinessException(TO_VITAL_DOCUMENT + "不能转两次，请联系管理员！");
            } else {
              turnNumInt = i;
            }
          }
        }
        map = new HashMap<>(16);
        map.put("sourceCategory", "收文");
        map.put("sourceId", pro.getId());
        map.put("subject", pro.getSubject());
        map.put("dealForm", proToOthersQuery.getDealForm());
        map.put("sort", proToOthersQuery.getIdList().get(0));
        map.put("source", "本级重要文件");
        result= this.withTokenRestTemplate.postForObject(this.docBusinessProperties.getRequestPrefix() + "/vitalDocument/transformationFile", map, JSONObject.class);
        if (result != null && result.getBooleanValue("flag")) {
          Proposal proposal = new Proposal();
          proposal.setId(docId);
          if (turnNumInt == -1) {
            turnNumJson.put(TO_VITAL_DOCUMENT, 1);
          } else {
            turnNumJson.put(TO_VITAL_DOCUMENT, turnNumInt + 1);
          }
          proposal.setTurnNum(turnNumJson.toJSONString());
          if (this.proposalMng.updateProposalMotion(proposal) < 1) {
            fianl = false;
          } else {
            fianl = true;
          }
        } else {
          throw new BusinessException(result.getString("message"));
        }
        break;
      default:
        break;
    }
    return fianl;
  }

  /**
   * 格式转换：转历史公文库
   *
   * @param proposal         提案对象
   * @param proToOthersQuery
   * @return
   */
  public HashMap<String, Object> getProToArchiveHashMap(Proposal proposal, ProToOthersQuery proToOthersQuery) {
    HashMap<String, Object> map = new HashMap<>(16);
    // 文档id
    map.put(ArchiveConstant.RESOURCE_ID, proposal.getId());
    // 文件标题
    map.put(ArchiveConstant.TITLE, proposal.getSubject());
    // 文件字号
    String docMark = proposal.getRelReceivalMark();
    map.put(ArchiveConstant.DOC_MARK, docMark);
//    // 文件单位
//    map.put(ArchiveConstant.FILE_UNIT, proposal.getSourceUnit());
    // 文件日期
    Date signDate = proposal.getSignDate();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    if (null != signDate) {
      map.put(ArchiveConstant.SIGN_DATE, sdf.format(signDate));
    }
    // 文件密级
    map.put(ArchiveConstant.SEC_LEVEL, proposal.getSecLevel());
    // 文件种类
    map.put(ArchiveConstant.DOC_TYPE, proposal.getDocType());
    // 描述
    map.put(ArchiveConstant.DESCRIPT, proposal.getRemark());
    // 公开属性
    map.put(ArchiveConstant.PUBLIC_FLAT, proToOthersQuery.getPublicFlag());
    map.put(ArchiveConstant.SOURCE, "proposal");
    //拟稿部门
    map.put(ArchiveConstant.DRAFT_DEPT, proposal.getDraftDept());
    //拟稿人
    map.put(ArchiveConstant.DRAFT_USER_NAME, proposal.getDraftUserName());
    //可读人员，收文都是办结转，(流程经办用户+公文库管理员)
    List<FlowWorkTodo> list;
    try {
      FlowWorkTodo flowWorkTodo = new FlowWorkTodo();
      flowWorkTodo.setBusinessDocId(proposal.getId());
      list = this.flowWorkTodoDao.getFlowWorkTodoList(flowWorkTodo);
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessException("设置可读域异常");
    }
    Set<String> reader = ArchiveUtil.getReader(list, this.docBusinessProperties.getArchiveAdmin());
    map.put(ArchiveConstant.READER, reader);

    //归档日期
    String archiveDate = sdf.format(new Date());
    map.put(ArchiveConstant.ARCHIVE_DATE, archiveDate);
    //收文日期
    map.put(ArchiveConstant.DRAFT_DATE, sdf.format(proposal.getDraftDate()));
//    //收文分类
//    map.put(ArchiveConstant.FILE_CATEGORY, proposal.getFileCategory());
    //流水号
    map.put(ArchiveConstant.DOC_SEQUENCE, proposal.getDocSequence());

    SecurityUser user = SecurityUtils.getPrincipal();
    map.put(ArchiveConstant.SYSTEM_NO, user.getSystemNo());
    map.put(ArchiveConstant.REG_USER_NO, user.getUserNo());
    map.put(ArchiveConstant.REG_USER_NAME, user.getUserName());
    return map;
  }
  /**
   * 格式转换：转任务库
   *
   * @param proposal         收文对象
   * @param targetId   附件拷贝目标id
   * @return
   */
  public HashMap<String, Object> getProToTaskLibraryHashMap(Proposal proposal, String targetId) {
    HashMap<String, Object> map = new HashMap<>(16);
    // 默认传入0    （0:非周期   1：周期任务）   释义：是否为周期任务
    map.put(TaskLibraryConstant.IS_CYCLE, "0");
    // 文件标题
    map.put(TaskLibraryConstant.TARGET_ID, targetId);
    // 文件标题
    map.put(TaskLibraryConstant.CONTENT, proposal.getSubject());
    // 拟稿人编码
    map.put(TaskLibraryConstant.DRAFT_USER_NO, proposal.getDraftUserNo());
    // 拟稿人名称
    map.put(TaskLibraryConstant.DRAFT_USER_NAME, proposal.getDraftUserName());
    // 拟稿人所在部门编号
    map.put(TaskLibraryConstant.DRAFT_DEPT_NO, proposal.getDraftDeptNo());
    // 拟稿人所在部门名称
    map.put(TaskLibraryConstant.DRAFT_DEPT_Name, proposal.getDraftDept());
    // 拟稿人所在单位编号
    map.put(TaskLibraryConstant.DRAFT_UNIT_NO, proposal.getDraftUnitNo());
    // 拟稿人所在单位名称
    map.put(TaskLibraryConstant.DRAFT_UNIT_Name, proposal.getDraftUnit());
    // 默认值为1（启用）  释义： 启用标识
    map.put(TaskLibraryConstant.IS_ENABLE, "1");
    // 根据手动和自动转入自主选取 [0:系统自动转入 1：手动转入/手动登记] 释义：转入标识
    map.put(TaskLibraryConstant.INTO_FLAG, "0");
    // 根据上方的转入类型选取属于自己模块的转入类型
    map.put(TaskLibraryConstant.TASK_STYLE_FLAG, "RECEIVAL_LEADER_AUDIT");
    // 关联ID，存入原文档的Id实现关联
    map.put(TaskLibraryConstant.RELATE_ID, proposal.getId());
    // 原文的Id
    map.put(TaskLibraryConstant.ORIGINAL_DOC_ID, proposal.getId());
    // 原文的Aid
    map.put(TaskLibraryConstant.ORIGINAL_AID, StringUtils.isNotBlank(proposal.getFlowPid()) ? proposal.getFlowPid() : "");
    return map;
  }

  /**
   * 格式转换：转部门阅办<br/>
   * - map字段说明
   * - 来文字号（收文） docMark 必填
   * - 编号（发文） docMark 必填
   * - 模块名称（收文、发文） sourceCategory 收文；发文；手动登记 必填
   * - 来文单位（收文） sourceUnit 必填
   * - 拟稿单位（发文） sourceUnit 必填
   * - 文件标题（收文、发文） subject 必填
   * - 部门编码 deptNo 要转给哪个部门阅办 必填
   * - 紧急程度 urgenLevel 必填
   * - 文件密级 secLevel 默认无 只有这个选项 必填
   * - 转入人编码 sourceUserNo 必填
   * - 转入人名称 sourceUser 必填
   * - 源文件id sourceId 收发文文件id
   * - 阅办单（收文、发文）dealFormId 阅办单【文件】id， 多个以分号隔开
   * - 文件链接 fileLink 文件链接
   *
   * @param proposal 发文对象
   * @return
   */
  public HashMap<String, Object> getToDepartmentHashMap(Proposal proposal, ProToOthersQuery proToOthersQuery) {
    String deptNo = proToOthersQuery.getDeptNo();
    if (StringUtils.isBlank(deptNo)) {
      throw new BusinessException("请选择部门");
    }
    HashMap<String, Object> map = new HashMap<>(16);
    map.put(DepartmentConstant.DOCMARK, proposal.getRelReceivalMark());
    map.put(DepartmentConstant.SOURCE_CATEGORY, "提案议案");
    SecurityUser user = SecurityUtils.getPrincipal();
    map.put(DepartmentConstant.SOURCE_UNIT, user.getUnitName());
    map.put(DepartmentConstant.SUBJECT, proposal.getSubject());
    map.put(DepartmentConstant.DEPT_NO, deptNo);
    map.put(DepartmentConstant.URGEN_LEVEL, proposal.getUrgentLevel());
    map.put(DepartmentConstant.SEC_LEVEL, proposal.getSecLevel());
    map.put(DepartmentConstant.SOURCE_USER_NO, user.getUserNo());
    map.put(DepartmentConstant.SOURCE_USER, user.getUserName());
    map.put(DepartmentConstant.SOURCE_ID, proposal.getId());
    //{"docWord":"搜救简报","options":[{"fileVersion":"发文阅办单1","id":"W8_NB6yzrXHd7fWe"}]}
    // 解析阅办单id
//        String dealFormId = proposal.getDealFormId();
//        String id = ExternalUtil.getDealFormIdFromJson(dealFormId, "id");

    // file：阅办单html字符串，fileName: 阅办单名称， fileSuffix：阅办单文件后缀（默认为html）
    List<DealForm> dealForm = proToOthersQuery.getDealForm();
    JSONArray dealFromJsonArr = JSONArray.parseArray(JSON.toJSONString(dealForm));
    map.put(DepartmentConstant.DEAL_FORM, dealFromJsonArr);
    map.put(DepartmentConstant.FILE_LINK, "");
    return map;
  }
  /**
   * 格式转换：转部门阅办<br/>
   * - map字段说明
   * - 来文字号（收文） docMark 必填
   * - 编号（发文） docMark 必填
   * - 模块名称（收文、发文） sourceCategory 收文；发文；手动登记 必填
   * - 来文单位（收文） sourceUnit 必填
   * - 拟稿单位（发文） sourceUnit 必填
   * - 文件标题（收文、发文） subject 必填
   * - 部门编码 deptNo 要转给哪个部门阅办 必填
   * - 紧急程度 urgenLevel 必填
   * - 文件密级 secLevel 默认无 只有这个选项 必填
   * - 转入人编码 sourceUserNo 必填
   * - 转入人名称 sourceUser 必填
   * - 源文件id sourceId 收发文文件id
   * - 阅办单（收文、发文）dealFormId 阅办单【文件】id， 多个以分号隔开
   * - 文件链接 fileLink 文件链接
   *
   * @param proposal 发文对象
   * @return
   */
  public HashMap<String, Object> getToReceivalHashMap(Proposal proposal, ProToOthersQuery proToOthersQuery) {
    HashMap<String, Object> map = new HashMap<>(16);
    String receivalId = IdUtil.getUID();
    map.put(ReceivalConstant.ID, receivalId);
    map.put(ReceivalConstant.DOC_MARK, proposal.getDocMark());
    map.put(ReceivalConstant.DOC_SEQUENCE, proposal.getRelReceivalMark());
    map.put(ReceivalConstant.DOC_SEQUENCE_NUM,"-1");
    map.put(ReceivalConstant.DOC_SEQUENCE_YEAR,"-1");
    map.put(ReceivalConstant.SOURCE_UNIT,proposal.getDraftDept());


    SecurityUser securityUser = SecurityUtils.getPrincipal();
    HashSet<String> readers = new HashSet<>();
    UmsOrg umsOrg = userDao.getUmsOrg(proToOthersQuery.getDeptNo());
    UmsUser recProUser = new UmsUser();
    // 得到提案议案受理，根据群组拿到用户
    UmsGroup umsGroup = userDao.getUmsGroup(proToOthersQuery.getDeptNo() + "_" + "REG", securityUser.getSystemNo());
    UmsGroupCate umsGroupCate = userDao.getUmsGroupCate("REG", securityUser.getSystemNo());
    if (umsGroup != null && umsGroupCate != null) {
      List<UmsUser> umsUsers = userDao.listGroupUser(umsGroup.getGroupNo(), securityUser.getSystemNo());
      for (UmsUser umsUser : umsUsers) {
        List<UmsUserProxy> umsUserProxies = userDao.listUserProxy(umsUser.getUserNo(), umsUser.getOrgNo());
        long longTime = System.currentTimeMillis();
        for (UmsUserProxy umsUserProxy : umsUserProxies) {
          // 添加等效代理人，存在代理的情况
          if (umsUserProxy.getProxyEndTime().getTime() >= longTime
            && umsUserProxy.getProxyBeginTime().getTime() <= longTime) {
            readers.add(umsUserProxy.getUserNoProxy());
          }
        }
        readers.add(umsUser.getUserNo());
      }
      if (null != umsUsers && umsUsers.size() > 0) {
        recProUser = umsUsers.get(0);
      }
    }

    if (readers.size() == 0) {
      throw new BusinessException("【" + umsOrg.getOrgName() + "】未配置收文受理人，请及时联系管理员进行配置");
    }
    String unitName ="";
    if(umsOrg.getUnitNo()!=null){
      unitName = this.userDao.getUmsOrg(umsOrg.getUnitNo()).getOrgName();
    }
    map.put(ReceivalConstant.DRAFT_USER,recProUser.getUserName());
    map.put(ReceivalConstant.DRAFT_USER_NO,recProUser.getUserNo());
    map.put(ReceivalConstant.DRAFT_USER_DEPT,umsOrg.getOrgName());
    map.put(ReceivalConstant.DRAFT_USER_DEPT_NO,proToOthersQuery.getDeptNo());
    map.put(ReceivalConstant.DRAFT_USER_UNIT,unitName);
    map.put(ReceivalConstant.DRAFT_USER_UNIT_NO,umsOrg.getUnitNo());
    map.put(ReceivalConstant.SUBJECT,proposal.getSubject());
    Timestamp draftDate =new Timestamp(System.currentTimeMillis());
    map.put(ReceivalConstant.DRAFT_DATE,draftDate);
    map.put(ReceivalConstant.READERS,readers);
    map.put(ReceivalConstant.MAIN_SEND,proposal.getMainSend());
    map.put(ReceivalConstant.COPY_SEND,proposal.getCopySend());
    map.put(ReceivalConstant.RESOURCE_ID,proposal.getId());
    map.put(ReceivalConstant.SYSTEM_NO,securityUser.getSystemNo());
    map.put(ReceivalConstant.FLOW_STATUS,"0");
    map.put(ReceivalConstant.RETURN_FLAG,"NOT_RETURN");
    return map;
  }

  /**
   * 归档案系统：转为xml格式
   *
   * @param proposal 发文对象
   * @return map
   */
  public HashMap<String, Object> getProToFileXml(Proposal proposal, ProToOthersQuery proToOthersQuery) {
    String xml;
    Element list = DocumentHelper.createElement("list");
    Element writ = list.addElement("writ");
    writ.addAttribute("ext_id", "oaid");
    // 全宗号 默认 0450 （可配置）
    String writSect = this.docBusinessProperties.getWritSect();
    if (StringUtils.isBlank(writSect)) {
      writSect = "0450";
    }
    writ.addAttribute("writ_sect", writSect);
    //da_type固定值为1
    writ.addAttribute("da_type", "1");

    final String ELEMENT = "element";
    final String NAME = "name";
    //题名
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_name")
      .setText(StringUtils.defaultString(proposal.getSubject()));
    //文件种类 发文fw,收文sw,内部nb，其他qt（没有这个参数 不可以加（190801））
//        writ.addElement(ELEMENT).addAttribute(NAME, "writ_type").setText("fw");
    //文件分类：收文、发文、其他
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_file_type").setText("发文");
    //文号
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_code")
      .setText(StringUtils.defaultString(proposal.getRelReceivalMark()));
    //归档年度
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_year")
      .setText(StringUtils.defaultString(year + ""));
    //责任者
    SecurityUser user = SecurityUtils.getPrincipal();
    String userName = user.getUserName();
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_duty")
      .setText(StringUtils.defaultString(userName));
    //机构代码
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_dept")
      .setText(StringUtils.defaultString(user.getOrgNo()));
    //机构名称
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_dept_name")
      .setText(StringUtils.defaultString(user.getOrgName()));
    //整理（归档）人（中文名）
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_creator")
      .setText(StringUtils.defaultString(userName));
    // （序号  之前没有这个参数（190801） 新增加）默认 0
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_sort")
      .setText("0");
    // （保管时间 之前没有这个参数（190801） 新增加）
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_keep_time")
      .setText("D10");
    //页数（默认为1）
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_page_num")
      .setText(StringUtils.defaultString("1"));

    //份数（默认为1）
    String printNumStr = "1";
    Integer printNum = proposal.getPrintNum();
    if (null != printNum) {
      printNumStr = printNum + "";
    }
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_copy_num")
      .setText(StringUtils.defaultString(printNumStr));

    //形成日期（yyyymmdd）
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String doneDate = sdf.format(new Date());
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_done_date")
      .setText(StringUtils.defaultString(doneDate));
    //解密化控（开放、控制、未定）
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_control").setText("控制");
    //密级（公开、机密、秘密、绝密）非必填
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_level")
      .setText(StringUtils.defaultString(proposal.getSecLevel()));
    // 归档日期(默认为当前日期格式为 yyyymmdd)
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_pigeonhole_date")
      .setText(StringUtils.defaultString(doneDate));
    //分类号（非必填）（改为必填（190801）） 默认 0
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_entity")
      .setText(StringUtils.defaultString("0"));
    // 档案门类（默认为 WS）
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_daml")
      .setText("WS");
    //文种（非必填）
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_language")
      .setText(StringUtils.defaultString(""));
    //主题词（非必填）
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_theme")
      .setText(StringUtils.defaultString(""));
    List<EgovAtt> egovAttList = this.egovAttMng.getEgovAttsByDocId(proposal.getId(), false);
    int egovAttSize = 0;
    if (egovAttList != null && egovAttList.size() > 0) {
      egovAttSize = egovAttList.size();
    }
    //原文数量（默认为0）
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_upload")
      .setText(StringUtils.defaultString(egovAttSize + ""));
    //存放位置（非必填）
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_place")
      .setText(StringUtils.defaultString(""));
    //备注（非必填）
    writ.addElement(ELEMENT).addAttribute(NAME, "writ_remark")
      .setText(StringUtils.defaultString(proposal.getRemark()));
    //附件
    if (egovAttSize > 0) {
      // egovAtts = [正文1，正文2，... 阅办单2，阅办单1]
      EgovAtt[] egovAtts = new EgovAtt[egovAttSize];
      int mainEgovAttsNum = 0;
      int dealFormEgovAttsNum = 0;
      //发文的盖章文件、发文带修改痕迹的原稿、发文清稿文件、发文附件
      for (EgovAtt egovAtt : egovAttList) {
        if(egovAtt.getType() != null && egovAtt.getType().indexOf("main") != -1) {
          // 临时存储正文，在这个for循环后面 遍历添加正文
          egovAtts[mainEgovAttsNum++] = egovAtt;
        } else if (StringUtils.equals(egovAtt.getType(), "dealForm")) {
          // 临时存储阅办单(从后往前添加到数组 egovAtts )，在这个for循环后面 遍历添加阅办单
          dealFormEgovAttsNum++;
          egovAtts[egovAttSize - dealFormEgovAttsNum] = egovAtt;
        } else {
          writ.addElement("file").addAttribute("name", StringUtils.defaultString(egovAtt.getFileName()) + "." + StringUtils.defaultString(egovAtt.getFileSuffix()))
            .addAttribute("kind", "附件")
            .setText(ExCommon.getRequestUrl(this.rmsParamDao, "attachDownloadUrl") + egovAtt.getId() + "&x-auth-token=" + proToOthersQuery.getIdList().get(0));
        }
      }
      // 遍历添加正文
      for (int i = 0; i < mainEgovAttsNum; i++) {
        writ.addElement("file").addAttribute("name", StringUtils.defaultString(egovAtts[i].getFileName()) + "." + StringUtils.defaultString(egovAtts[i].getFileSuffix()))
          .addAttribute("kind", "正文")
          .setText(ExCommon.getRequestUrl(this.rmsParamDao, "attachDownloadUrl") + egovAtts[i].getId() + "&x-auth-token=" + proToOthersQuery.getIdList().get(0));
      }
      // 遍历取阅办单 从后往前取
      for (int i = 0; i < dealFormEgovAttsNum; i++) {
        writ.addElement("file").addAttribute("name", StringUtils.defaultString(egovAtts[egovAttSize -1 -i].getFileName()) + "." + StringUtils.defaultString(egovAtts[egovAttSize -1 -i].getFileSuffix()))
          .addAttribute("kind", "阅办单")
          .setText(ExCommon.getRequestUrl(this.rmsParamDao, "attachDownloadUrl") + egovAtts[egovAttSize -1 -i].getId() + "&x-auth-token=" + proToOthersQuery.getIdList().get(0));
      }
    }
    xml = list.asXML();
    HashMap<String, Object> map = new HashMap<>(16);
    map.put("xml", xml);
//        map.put("attachId", attachId);
    return map;
  }

}

