package com.zjhousing.egov.proposal.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.attachutil.model.EgovAtt;
import com.rongji.egov.attachutil.service.EgovAttMng;
import com.rongji.egov.user.business.dao.RmsParamDao;
import com.rongji.egov.user.business.dao.UserDao;
import com.rongji.egov.user.business.ex.ExCommon;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.business.model.UmsUser;
import com.rongji.egov.user.business.util.SecurityUtils;
import com.rongji.egov.utils.common.IdUtil;
import com.rongji.egov.utils.exception.BusinessException;
import com.rongji.egov.wflow.business.dao.engine.FlowWorkTodoDao;
import com.zjhousing.egov.proposal.business.constant.ArchiveConstant;
import com.zjhousing.egov.proposal.business.constant.DepartmentConstant;
import com.zjhousing.egov.proposal.business.constant.TaskLibraryConstant;
import com.zjhousing.egov.proposal.business.enums.TransferLibraryTypeEnum;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.query.CommonToOthersQuery;
import com.zjhousing.egov.proposal.business.query.DealForm;
import com.zjhousing.egov.proposal.business.query.DisToOthersQuery;
import com.zjhousing.egov.proposal.business.service.ComToOthersMng;
import com.zjhousing.egov.proposal.business.service.DisToOthersMng;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import com.zjhousing.egov.proposal.business.util.DocBusinessProperties;
import com.zjhousing.egov.proposal.business.util.WebServiceUtil;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.zjhousing.egov.proposal.business.constant.ExternalToOthersConstant.*;
/**
 * 发文转其他文 mng impl
 *
 * @author lindongmei
 * @date 2018/10/31
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DisToOthersMngImpl implements DisToOthersMng {

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

  @Override
  public boolean disToOthers(DisToOthersQuery disToOthersQuery) {
    HashMap<String, Object> map;
    boolean fianl = true;
    JSONObject result;
    Proposal dis = null;
    String docId = "";
    JSONObject turnNumJson = new JSONObject();
    //  转重要文件、转工作提醒次数 json格式的字符串"{ExternalToOthersConstant.TO_WORK_REMINDER: 转工作提醒次数, ExternalToOthersConstant.TO_VITAL_DOCUMENT: 转重要文件次数}"
    String turnNum = null;
    int turnNumInt = -1;
    final String MODULE_NO = "DISPATCH";
    if (!disToOthersQuery.getType().equals(CANCLE_TO_DEPARTMENT)) {
      docId = disToOthersQuery.getDocId();
      dis = this.proposalMng.getProposalMotionById(docId);
      if (null == dis) {
        throw new BusinessException("文件不存在");
      }
      turnNum = dis.getTurnNum();
    }
    switch (disToOthersQuery.getType()) {
      case TO_ARCHIVE:
        map = this.getDisToArchiveHashMap(dis, disToOthersQuery);
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
      case TO_DEPARTMENT:
        map = this.getToDepartmentHashMap(dis, disToOthersQuery);
        JSONObject jsonObject = this.withTokenRestTemplate.postForObject(this.docBusinessProperties.getRequestPrefix() + "/deptReceival/insertFileToDeptReceival", map, JSONObject.class);
        boolean res = ExCommon.getFlag(jsonObject);
        if (!res) {
          throw new BusinessException(jsonObject.getString("msg"));
        }
        fianl = res;
        break;
      case CANCLE_TO_DEPARTMENT:
        JSONObject jo = this.withTokenRestTemplate.postForObject(this.docBusinessProperties.getRequestPrefix() + "/deptReceival/deleteDeptReceivalByIdList", disToOthersQuery.getIdList(), JSONObject.class);
        boolean flag = ExCommon.getFlag(jo);
        if (!flag) {
          throw new BusinessException(jo.getString("msg"));
        }
        fianl = flag;
        break;
      case TO_OFFICE:
        break;
      case TO_PUBLIC:
        break;
      case TO_FILE:
        //保存办理单为附件
        if (disToOthersQuery.getDealForm() != null && disToOthersQuery.getDealForm().size() > 0) {
          SecurityUser user = SecurityUtils.getPrincipal();
          CommonToOthersQuery dealFormCtq = new CommonToOthersQuery(docId, MODULE_NO, user, disToOthersQuery.getDealForm().get(0));
          this.comToOthersMng.insertDealFormToAtt(dealFormCtq);
        }
        HashMap<String, Object> hashMap = this.getDisToFileXml(dis, disToOthersQuery);
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
          throw new BusinessException("发文归档案异常");
        } finally {
          List<String> attachId = (ArrayList) hashMap.get("attachId");
          if (null != attachId && attachId.size() > 0) {
            this.egovAttMng.deleteEgovAtt(attachId);
          }
        }
        break;
      case TO_TASK_LIBRARY:
        if(StringUtils.isBlank(dis.getTaskFlag()) || !("1".equals(dis.getTaskFlag().trim()))) {
          String targetId = IdUtil.getUID();
          //拷贝正式文
          String[] strings = {"main_pdf", "main_ofd", "main_trace", "main_doc"};
          for(int i = 0, j = 0, k = strings.length; i < 1 && j < k; j++) {
            i = this.egovAttMng.copyEgovAttByDocId(dis.getId(), targetId, strings[j], strings[j]);
          }
          //拷贝普通附件
          this.egovAttMng.copyEgovAttByDocId(dis.getId(), targetId, "attach", "attach");

          //保存办理单为附件
          if (disToOthersQuery.getDealForm() != null && disToOthersQuery.getDealForm().size() > 0) {
            SecurityUser user = SecurityUtils.getPrincipal();
            CommonToOthersQuery dealFormCtq = new CommonToOthersQuery(targetId, MODULE_NO, user, disToOthersQuery.getDealForm().get(0));
            this.comToOthersMng.insertDealFormToAtt(dealFormCtq);
          }
          map = this.getDisToTaskLibraryHashMap(dis);
          // 目标文件id
          map.put(TaskLibraryConstant.TARGET_ID, targetId);
          // 关注领导编码
          map.put(TaskLibraryConstant.FOLLOW_LEADER_NO, "");
          // 关注领导名称
          map.put(TaskLibraryConstant.FOLLOW_LEADER_NAME, "");
          // 签发意见
          map.put(TaskLibraryConstant.SPECIFIC_CONTENT_OPINION, "");
          Set<String> readers = disToOthersQuery.getReaders();
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
        //拷贝签章文件
        this.egovAttMng.copyEgovAttByDocId(dis.getId(), targetId, "main_sign", "main_sign");
        //拷贝普通附件
        this.egovAttMng.copyEgovAttByDocId(dis.getId(), targetId, "attach", "attach");
        //保存办理单为附件
                /*if (disToOthersQuery.getDealForm() != null && disToOthersQuery.getDealForm().size() > 0) {
                    SecurityUser user = SecurityUtils.getPrincipal();
                    CommonToOthersQuery dealFormCtq = new CommonToOthersQuery(targetId, MODULE_NO, user, disToOthersQuery.getDealForm().get(0));
                    this.comToOthersMng.insertDealFormToAtt(dealFormCtq);
                }*/
        map = new HashMap<>();
        map.put("id", targetId);
        map.put("subject", dis.getSubject());
        map.put("draftUser", dis.getDraftUserName());
        map.put("draftUserNo", dis.getDraftUserNo());
        map.put("draftDept", dis.getDraftDept());
        map.put("draftDeptNo", dis.getDraftDeptNo());
        map.put("draftDate", dis.getDraftDate());
        map.put("relateId", dis.getId());
        map.put("systemNo", dis.getSystemNo());
        map.put("moduleNo", MODULE_NO);
        map.put("category", disToOthersQuery.getIdList().get(0));
//                HttpHeaders header = new HttpHeaders();
        result = this.withTokenRestTemplate.postForObject(this.docBusinessProperties.getRequestPrefix() + "/workReminder/toWorkReminder", map, JSONObject.class);
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
        map = new HashMap<>();
        map.put("sourceCategory", "发文");
        map.put("sourceId", dis.getId());
        map.put("subject", dis.getSubject());
        map.put("dealForm", disToOthersQuery.getDealForm());
        map.put("sort", disToOthersQuery.getIdList().get(0));
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
          throw new BusinessException(result.getString("msg"));
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
   * @param proposal         发文对象
   * @param disToOthersQuery
   * @return
   */
  public HashMap<String, Object> getDisToArchiveHashMap(Proposal proposal, DisToOthersQuery disToOthersQuery) {
    HashMap<String, Object> map = new HashMap<>(16);
    // 文档id
    map.put(ArchiveConstant.RESOURCE_ID, proposal.getId());
    // 文件标题
    map.put(ArchiveConstant.TITLE, proposal.getSubject());
    // 文件字号
    String docMark = proposal.getDocMark();
    map.put(ArchiveConstant.DOC_MARK, docMark);
    // 文件单位
    map.put(ArchiveConstant.FILE_UNIT, proposal.getDraftDept());
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
    map.put(ArchiveConstant.PUBLIC_FLAT, disToOthersQuery.getPublicFlag());
    // 公开类别
    map.put(ArchiveConstant.PUBLIC_CATEGORY, proposal.getPublicCate());
    map.put(ArchiveConstant.SOURCE, "PROPOSALMOTION");
    //文件字
    map.put(ArchiveConstant.DOC_WORD, proposal.getDocWord());
    //拟稿部门
    map.put(ArchiveConstant.DRAFT_DEPT, proposal.getDraftDept());
    //拟稿人
    map.put(ArchiveConstant.DRAFT_USER_NAME, proposal.getDraftUserName());
    //可读人员,流程办结转，设置流程经办用户可看
    // todo 添加个人传阅用户
    if (TransferLibraryTypeEnum.FILE_DONE_TRANSFER.equals(proposal.getTransferLibraryType())) {
//            List<FlowWorkTodo> list;
//            try {
//                FlowWorkTodo flowWorkTodo = new FlowWorkTodo();
//                flowWorkTodo.setBusinessDocId(proposal.getId());
//                list = this.flowWorkTodoDao.getFlowWorkTodoList(flowWorkTodo);
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new BusinessException("设置可读域异常");
//            }
//            Set<String> reader = ArchiveUtil.getReader(list, this.docBusinessProperties.getArchiveAdmin());
      map.put(ArchiveConstant.READER, new HashSet<>());
    } else {
      //手动转（公开 或 指定权限转）
      map.put(ArchiveConstant.READER, disToOthersQuery.getReaders());
    }

    //归档日期
    String archiveDate = sdf.format(new Date());
    map.put(ArchiveConstant.ARCHIVE_DATE, archiveDate);
    //签发领导
    map.put(ArchiveConstant.SIGN_USER_NAME, proposal.getSignUserName());
    //拟稿日期
    map.put(ArchiveConstant.DRAFT_DATE, sdf.format(proposal.getDraftDate()));
    // 流水号
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
   * @param proposal         发文对象
   * @return
   */
  public HashMap<String, Object> getDisToTaskLibraryHashMap(Proposal proposal) {
    HashMap<String, Object> map = new HashMap<>(16);
        /*Calendar cale = Calendar.getInstance();
        // 年份
        map.put(TaskLibraryConstant.YEAR, String.valueOf(cale.get(Calendar.YEAR)));
        // 月份
        map.put(TaskLibraryConstant.MONTH, String.valueOf(cale.get(Calendar.MONTH)));*/
    // 默认传入0    （0:非周期   1：周期任务）   释义：是否为周期任务
    map.put(TaskLibraryConstant.IS_CYCLE, "0");
    // 目标文件id
    //map.put(TaskLibraryConstant.TARGET_ID, targetId);
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
    map.put(TaskLibraryConstant.TASK_STYLE_FLAG, "DISPATCH_MEETING");
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
  public HashMap<String, Object> getToDepartmentHashMap(Proposal proposal, DisToOthersQuery disToOthersQuery) {
    String deptNo = disToOthersQuery.getDeptNo();
    if (StringUtils.isBlank(deptNo)) {
      throw new BusinessException("请选择部门");
    }
    HashMap<String, Object> map = new HashMap<>(16);
    map.put(DepartmentConstant.DOCMARK, proposal.getDocMark());
    map.put(DepartmentConstant.SOURCE_CATEGORY, "发文");
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
    List<DealForm> dealForm = disToOthersQuery.getDealForm();
    JSONArray dealFromJsonArr = JSONArray.parseArray(JSON.toJSONString(dealForm));
    map.put(DepartmentConstant.DEAL_FORM, dealFromJsonArr);
    map.put(DepartmentConstant.FILE_LINK, "");
    return map;
  }

  /**
   * 归档案系统：转为xml格式
   *
   * @param proposal 发文对象
   * @return map
   */
  public HashMap<String, Object> getDisToFileXml(Proposal proposal, DisToOthersQuery disToOthersQuery) {
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
      .setText(StringUtils.defaultString(proposal.getDocMark()));
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
            .setText(ExCommon.getRequestUrl(this.rmsParamDao, "attachDownloadUrl") + egovAtt.getId() + "&x-auth-token=" + disToOthersQuery.getIdList().get(0));
        }
      }
      // 遍历添加正文
      for (int i = 0; i < mainEgovAttsNum; i++) {
        writ.addElement("file").addAttribute("name", StringUtils.defaultString(egovAtts[i].getFileName()) + "." + StringUtils.defaultString(egovAtts[i].getFileSuffix()))
          .addAttribute("kind", "正文")
          .setText(ExCommon.getRequestUrl(this.rmsParamDao, "attachDownloadUrl") + egovAtts[i].getId() + "&x-auth-token=" + disToOthersQuery.getIdList().get(0));
      }
      // 遍历取阅办单 从后往前取
      for (int i = 0; i < dealFormEgovAttsNum; i++) {
        writ.addElement("file").addAttribute("name", StringUtils.defaultString(egovAtts[egovAttSize -1 -i].getFileName()) + "." + StringUtils.defaultString(egovAtts[egovAttSize -1 -i].getFileSuffix()))
          .addAttribute("kind", "阅办单")
          .setText(ExCommon.getRequestUrl(this.rmsParamDao, "attachDownloadUrl") + egovAtts[egovAttSize -1 -i].getId() + "&x-auth-token=" + disToOthersQuery.getIdList().get(0));
      }
    }
//        List<EgovAtt> egovAttList = this.egovAttMng.getEgovAttsByDocId(proposal.getId(), false);
//        if (egovAttList.size() > 0) {
//            Element attEle;
    //发文的盖章文件、发文带修改痕迹的原稿、发文清稿文件、发文附件
//            for (EgovAtt egovAtt : egovAttList) {
//                writ.addElement("file").addAttribute("nam", StringUtils.defaultString(egovAtt.getFileName()))
//                        .setText(ExCommon.getRequestUrl(this.rmsParamDao, "attachDownloadUrl") + egovAtt.getId());
//            }
//        }

    //todo 发文办理单
//        List<String> attachId = new ArrayList<>();
//        // file：阅办单html字符串，fileName: 阅办单名称， fileSuffix：阅办单文件后缀（默认为html）
//        List<DealForm> dealForms = disToOthersQuery.getDealForm();
//        for (DealForm dealForm : dealForms) {
//            EgovAtt egovAtt = AttachUtil.createEgovAtt(dealForm, user.getSystemNo());
//            int result = this.egovAttMng.insertEgovAtt(egovAtt, user);
//            if (result == 1) {
//                writ.addElement("file").addAttribute("nam", StringUtils.defaultString(egovAtt.getFileName()))
//                        .setText(ExCommon.getRequestUrl(this.rmsParamDao, "attachDownloadUrl") + egovAtt.getId());
//                attachId.add(egovAtt.getId());
//            } else {
//                throw new BusinessException("归档办理单错误");
//            }
//        }

    //todo 发文流程记录(附件)
//        String pid = proposal.getFlowPid();
//        HashMap<String, Object> params = new HashMap<>(3);
//        params.put("processId", pid);
//        params.put("tableName", TABLE_NAME);
//        try {
//            List<FlowWorkItemInstance> records = this.flowWorkItemInstanceDao.getFlowWorkItemInstanceList(params);
//            if (null != records && records.size() > 0) {
//                String jsonObject = JSONObject.toJSONString(records);
//                // todo 暂时使用record
//                writ.addElement("record").setText(StringUtils.defaultString(jsonObject));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BusinessException("封装发文流程记录失败");
//        }
    xml = list.asXML();
    HashMap<String, Object> map = new HashMap<>(16);
    map.put("xml", xml);
//        map.put("attachId", attachId);
    return map;
  }

}

