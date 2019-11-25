package com.zjhousing.egov.proposal.business.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rongji.egov.utils.spring.validation.InsertValidate;
import com.rongji.egov.utils.spring.validation.UpdateValidate;
import com.rongji.egov.wflow.business.constant.ModuleFiledConst;
import com.rongji.egov.wflow.business.model.FlowObject;
import com.zjhousing.egov.proposal.business.enums.ArchiveTypeEnum;
import com.zjhousing.egov.proposal.business.enums.CleanFlagEnum;
import com.zjhousing.egov.proposal.business.enums.TransferLibraryTypeEnum;
import com.zjhousing.egov.proposal.business.util.ToSolrMapUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author chenwenkang
 * @create 2019/11/15
 * @desc 提案议案类
 **/
public class Proposal extends FlowObject implements Serializable {
  /**
   * id
   */
  @NotEmpty(message = "id不能为空", groups = {UpdateValidate.class})
  @Length(min = 16, max = 16, message = "id{fixedLength}", groups = {UpdateValidate.class})
  private String id;
  /**
   * 提案主题-SUBJECT
   */
  @NotEmpty(message = "标题不能为空", groups = {InsertValidate.class})
  @Length(max = 256, message = "标题{fixedLength}", groups = {UpdateValidate.class, InsertValidate.class})
  private String subject;
  /**
   * 文号-DOC_MARK
   */
  private String docMark;
  /**
   * 文号序号-DOC_MARK_NUM
   */
  private Integer docMarkNum;
  /**
   * 文号年度-DOC_MARK_YEAR
   */
  private Integer docMarkYear;
  /**
   * 流水号-DOC_SEQUENCE
   */
  private String docSequence;
  /**
   * 流水年份-DOC_SEQUENCE_YEAR
   */
  private Integer docSequenceYear;
  /**
   * 机关代字(文件字)-DOC_WORD
   */
  private String docWord;
  /**
   * 文件类型-DOC_CATE
   */
  private String docCate;
  /**
   * 拟稿日期-DRAFT_DATE
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date draftDate;
  /**
   * 拟稿人编码-DRAFT_USER_NO
   */
  private String draftUserNo;
  /**
   * 拟稿人姓名-DRAFT_USER_NAME
   */
  private String draftUserName;
  /**
   * 拟稿人部门-DRAFT_DEPT
   */
  private String draftDept;
  /**
   * 拟稿人部门编码-DRAFT_DEPT_NO
   */
  private String draftDeptNo;
  /**
   * 拟稿人单位-DRAFT_UNIT
   */
  private String draftUnit;
  /**
   * 拟稿人单位编码-DRAFT_UNIT_NO
   */
  private String draftUnitNo;
  /**
   * 签发人编码-SIGN_USER_NO
   */
  private String signUserNo;
  /**
   * 签发人姓名-SIGN_USER_NAME
   */
  private String signUserName;
  /**
   * 签发人部门-SIGN_DEPT
   */
  private String signDept;
  /**
   * 签发人部门编码-SIGN_DEPT_NO
   */
  private String signDeptNo;
  /**
   * 签发人单位-SIGN_UNIT
   */
  private String signUnit;
  /**
   * 签发人单位编码-SIGN_UNIT_NO
   */
  private String signUnitNo;
  /**
   * 签发日期-SIGN_DATE
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date signDate;
  private CleanFlagEnum signDateCleanFlag = CleanFlagEnum.NOT_CLEAN_UP;
  /**
   * 是否已签发（1是，0否）-SIGN_FLAG
   */
  private String signFlag;

  /**
   * 提案议案大类-PROPOSAL_PARENT_CLASS
   */
  private String parentClass;
  /**
   * 提案议案子类-PROPOSAL_CHILDREN_CLASS
   */
  private String childrenClass;
  /**
   * 反映类别-REFLECTION_CATEGORY
   */
  private String reflectionCategory;
  /**
   * 届次-MEETING_SESSION
   */
  private String meetingSession;
  /**
   * 提案议案编号-PROPOSAL_NUM
   */
  private String proposalNum;
  /**
   * 关联收文号-REL_RECEIVAL_MARK
   */
  private String relReceivalMark;
  /**
   * 领衔代表ID-LEADING_USER_NO
   */
  private String leadingNo;
  /**
   * 领衔代表姓名-LEADING_USER
   */
  private String leadingUser;
  /**
   * 联系电话-LEADING_USER_PHOTO
   */
  private String leadingPhoto;
  /**
   * 联系地址-LEADING_USER_PLACE
   */
  private String leadingPlace;
  /**
   * 附议代表ID-SECONDED_USERS_NO
   */
  private String secondedNo;
  /**
   * 附议代表-SECONDED_USERS
   */
  private Set<String> secondedUsers;
  /**
   * 附议人数***
   */
  private String secondedNum;
  /**
   * 案由-CAUSE_ACTION
   */
  private String causeAction;
  /**
   * 主办单位-MAIN_ORGANIZER
   */
  private Set<String> mainOrganizer;
  /**
   * 协办单位-ASSIST_ORGANIZER
   */
  private Set<String> assistOrganizer;
  /**
   * 附件***
   */
  private String attachDesc;
  /**
   * 交办编号-ASSIGNMENT_NUM
   */
  private String assignmentNum;
  /**
   * 交办时间-ASSIGNMENT_DATE
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date assignmentDate;
  /**
   * 要求反馈时间时间-REQUEST_DATE
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date requestDate;
  /**
   * 承办处-UNDERTAKE_DEPARTMENT
   */
  private String undertakeDepartment;
  /**
   * 办理方式-HANDLING_MODE
   */
  private String handlingMode;
  /**
   * 紧急程度-URGENT_LEVEL
   */
  private String urgentLevel;
  /**
   * 交办人ID-ASSIGNMENT_USER_NO
   */
  private String assignmentUserNo;
  /**
   * 交办人-ASSIGNMENT_USER
   */
  private String assignmentUser;
  /**
   * 联系方式-ASSIGNMENT_PHOTO
   */
  private String assignmentPhoto;
  /**
   * 重要程度-IMPORT_LEVEL
   */
  private String importLevel;
  /**
   * 交办要求-ASSIGNMENT_REQUIREMENTS
   */
  private String assignmentRequirements;
  /**
   * 主送-MAIN_SEND
   */
  private Set<String> mainSend;
  /**
   * 抄送-COPY_SEND
   */
  private Set<String> copySend;
  /**
   * 经办人ID-HANDLE_USER_NO
   */
  private String handleUserNo;
  /**
   * 经办人-HANDLE_USER
   */
  private String handleUser;
  /**
   * 联系方式-HANDLE_PHOTO
   */
  private String handlePhoto;
  /**
   * 服务意见-SERVICE_ATTITUDE
   */
  private String serviceAttitude;
  /**
   * 备注-REMARK
   */
  private String remark;
  /**
   * 阅办单ID  DEAL_FORM_ID
   */
  private String dealFormId;
  /**
   * 系统编码-SYSTEM_NO
   */
  private String systemNo;
  /**
   * 查阅人-READERS
   */
  private Set<String> readers;
  /**
   * 文件密级-SEC_LEVEL
   */
  private String secLevel;
  /**
   * 转历史文库类型-TRANSFER_LIBRARY_TYPE
   * 取值: 办结转、手动转、指定权限转
   */
  private TransferLibraryTypeEnum transferLibraryType;
  /**
   * 归档类型-ARCHIVE_TYPE
   */
  private ArchiveTypeEnum archiveType;
  /**
   * 归档状态-ARCHIVE_FLAG
   */
  private String archiveFlag;
  /**
   * 页数-PAGE_NUM
   */
  private Integer pageNum;
  /**
   * 印刷份数-PRINT_NUM
   */
  private Integer printNum;

  /**
   * 转任务标识,是否已转任务 1是 0否
   */
  private String taskFlag;
  /**
   * 转重要文件、转工作提醒次数、...   json格式的字符串"{转工作提醒: 转工作提醒次数, 转重要文件: 转重要文件次数,...}"
   */
  private String turnNum;
  /**
   * 归档需要
   * 文件种类
   */
  private String docType = "提案议案";
  /**
   * 归档需要
   * 公开类别
   */
  private String publicCate ;


  public Proposal() {
  }
  public Proposal(String id, String relReceivalMark) {
    this.id = id;
    this.relReceivalMark = relReceivalMark;
  }
  public Proposal(String id, String subject, String docMark, Integer docMarkNum, Integer docMarkYear, String docSequence, Integer docSequenceYear, String docWord, String docCate, Date draftDate, String draftUserNo, String draftUserName, String draftDept, String draftDeptNo, String draftUnit, String draftUnitNo, String signUserNo, String signUserName, String signDept, String signDeptNo, String signUnit, String signUnitNo, Date signDate, CleanFlagEnum signDateCleanFlag, String signFlag, String parentClass, String childrenClass, String reflectionCategory, String meetingSession, String proposalNum, String relReceivalMark, String leadingNo, String leadingUser, String leadingPhoto, String leadingPlace, String secondedNo, Set<String> secondedUsers, String secondedNum, String causeAction, Set<String> mainOrganizer, Set<String> assistOrganizer, String attachDesc, String assignmentNum, Date assignmentDate, Date requestDate, String undertakeDepartment, String handlingMode, String urgentLevel, String assignmentUserNo, String assignmentUser, String assignmentPhoto, String importLevel, String assignmentRequirements, Set<String> mainSend, Set<String> copySend, String handleUserNo, String handleUser, String handlePhoto, String serviceAttitude, String remark, String dealFormId, String systemNo, Set<String> readers, String secLevel, TransferLibraryTypeEnum transferLibraryType, ArchiveTypeEnum archiveType, String archiveFlag, Integer pageNum, Integer printNum, String taskFlag, String turnNum, String docType, String publicCate) {
    this.id = id;
    this.subject = subject;
    this.docMark = docMark;
    this.docMarkNum = docMarkNum;
    this.docMarkYear = docMarkYear;
    this.docSequence = docSequence;
    this.docSequenceYear = docSequenceYear;
    this.docWord = docWord;
    this.docCate = docCate;
    this.draftDate = draftDate;
    this.draftUserNo = draftUserNo;
    this.draftUserName = draftUserName;
    this.draftDept = draftDept;
    this.draftDeptNo = draftDeptNo;
    this.draftUnit = draftUnit;
    this.draftUnitNo = draftUnitNo;
    this.signUserNo = signUserNo;
    this.signUserName = signUserName;
    this.signDept = signDept;
    this.signDeptNo = signDeptNo;
    this.signUnit = signUnit;
    this.signUnitNo = signUnitNo;
    this.signDate = signDate;
    this.signDateCleanFlag = signDateCleanFlag;
    this.signFlag = signFlag;
    this.parentClass = parentClass;
    this.childrenClass = childrenClass;
    this.reflectionCategory = reflectionCategory;
    this.meetingSession = meetingSession;
    this.proposalNum = proposalNum;
    this.relReceivalMark = relReceivalMark;
    this.leadingNo = leadingNo;
    this.leadingUser = leadingUser;
    this.leadingPhoto = leadingPhoto;
    this.leadingPlace = leadingPlace;
    this.secondedNo = secondedNo;
    this.secondedUsers = secondedUsers;
    this.secondedNum = secondedNum;
    this.causeAction = causeAction;
    this.mainOrganizer = mainOrganizer;
    this.assistOrganizer = assistOrganizer;
    this.attachDesc = attachDesc;
    this.assignmentNum = assignmentNum;
    this.assignmentDate = assignmentDate;
    this.requestDate = requestDate;
    this.undertakeDepartment = undertakeDepartment;
    this.handlingMode = handlingMode;
    this.urgentLevel = urgentLevel;
    this.assignmentUserNo = assignmentUserNo;
    this.assignmentUser = assignmentUser;
    this.assignmentPhoto = assignmentPhoto;
    this.importLevel = importLevel;
    this.assignmentRequirements = assignmentRequirements;
    this.mainSend = mainSend;
    this.copySend = copySend;
    this.handleUserNo = handleUserNo;
    this.handleUser = handleUser;
    this.handlePhoto = handlePhoto;
    this.serviceAttitude = serviceAttitude;
    this.remark = remark;
    this.dealFormId = dealFormId;
    this.systemNo = systemNo;
    this.readers = readers;
    this.secLevel = secLevel;
    this.transferLibraryType = transferLibraryType;
    this.archiveType = archiveType;
    this.archiveFlag = archiveFlag;
    this.pageNum = pageNum;
    this.printNum = printNum;
    this.taskFlag = taskFlag;
    this.turnNum = turnNum;
    this.docType = docType;
    this.publicCate = publicCate;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Proposal{");
    sb.append("id='").append(id).append('\'');
    sb.append(", subject='").append(subject).append('\'');
    sb.append(", docMark='").append(docMark).append('\'');
    sb.append(", docMarkNum=").append(docMarkNum);
    sb.append(", docMarkYear=").append(docMarkYear);
    sb.append(", docSequence='").append(docSequence).append('\'');
    sb.append(", docSequenceYear=").append(docSequenceYear);
    sb.append(", docWord='").append(docWord).append('\'');
    sb.append(", docCate='").append(docCate).append('\'');
    sb.append(", draftDate=").append(draftDate);
    sb.append(", draftUserNo='").append(draftUserNo).append('\'');
    sb.append(", draftUserName='").append(draftUserName).append('\'');
    sb.append(", draftDept='").append(draftDept).append('\'');
    sb.append(", draftDeptNo='").append(draftDeptNo).append('\'');
    sb.append(", draftUnit='").append(draftUnit).append('\'');
    sb.append(", draftUnitNo='").append(draftUnitNo).append('\'');
    sb.append(", signUserNo='").append(signUserNo).append('\'');
    sb.append(", signUserName='").append(signUserName).append('\'');
    sb.append(", signDept='").append(signDept).append('\'');
    sb.append(", signDeptNo='").append(signDeptNo).append('\'');
    sb.append(", signUnit='").append(signUnit).append('\'');
    sb.append(", signUnitNo='").append(signUnitNo).append('\'');
    sb.append(", signDate=").append(signDate);
    sb.append(", signDateCleanFlag=").append(signDateCleanFlag);
    sb.append(", signFlag='").append(signFlag).append('\'');
    sb.append(", parentClass='").append(parentClass).append('\'');
    sb.append(", childrenClass='").append(childrenClass).append('\'');
    sb.append(", reflectionCategory='").append(reflectionCategory).append('\'');
    sb.append(", meetingSession='").append(meetingSession).append('\'');
    sb.append(", proposalNum='").append(proposalNum).append('\'');
    sb.append(", relReceivalMark='").append(relReceivalMark).append('\'');
    sb.append(", leadingNo='").append(leadingNo).append('\'');
    sb.append(", leadingUser='").append(leadingUser).append('\'');
    sb.append(", leadingPhoto='").append(leadingPhoto).append('\'');
    sb.append(", leadingPlace='").append(leadingPlace).append('\'');
    sb.append(", secondedNo='").append(secondedNo).append('\'');
    sb.append(", secondedUsers=").append(secondedUsers);
    sb.append(", secondedNum='").append(secondedNum).append('\'');
    sb.append(", causeAction='").append(causeAction).append('\'');
    sb.append(", mainOrganizer=").append(mainOrganizer);
    sb.append(", assistOrganizer=").append(assistOrganizer);
    sb.append(", attachDesc='").append(attachDesc).append('\'');
    sb.append(", assignmentNum='").append(assignmentNum).append('\'');
    sb.append(", assignmentDate=").append(assignmentDate);
    sb.append(", requestDate=").append(requestDate);
    sb.append(", undertakeDepartment='").append(undertakeDepartment).append('\'');
    sb.append(", handlingMode='").append(handlingMode).append('\'');
    sb.append(", urgentLevel='").append(urgentLevel).append('\'');
    sb.append(", assignmentUserNo='").append(assignmentUserNo).append('\'');
    sb.append(", assignmentUser='").append(assignmentUser).append('\'');
    sb.append(", assignmentPhoto='").append(assignmentPhoto).append('\'');
    sb.append(", importLevel='").append(importLevel).append('\'');
    sb.append(", assignmentRequirements='").append(assignmentRequirements).append('\'');
    sb.append(", mainSend=").append(mainSend);
    sb.append(", copySend=").append(copySend);
    sb.append(", handleUserNo='").append(handleUserNo).append('\'');
    sb.append(", handleUser='").append(handleUser).append('\'');
    sb.append(", handlePhoto='").append(handlePhoto).append('\'');
    sb.append(", serviceAttitude='").append(serviceAttitude).append('\'');
    sb.append(", remark='").append(remark).append('\'');
    sb.append(", dealFormId='").append(dealFormId).append('\'');
    sb.append(", systemNo='").append(systemNo).append('\'');
    sb.append(", readers=").append(readers);
    sb.append(", secLevel='").append(secLevel).append('\'');
    sb.append(", transferLibraryType=").append(transferLibraryType);
    sb.append(", archiveType=").append(archiveType);
    sb.append(", archiveFlag='").append(archiveFlag).append('\'');
    sb.append(", pageNum='").append(pageNum).append('\'');
    sb.append(", printNum=").append(printNum);
    sb.append(", taskFlag='").append(taskFlag).append('\'');
    sb.append(", turnNum='").append(turnNum).append('\'');
    sb.append(", docType='").append(docType).append('\'');
    sb.append(", publicCate='").append(publicCate).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public String getTaskFlag() {
    return taskFlag;
  }

  public void setTaskFlag(String taskFlag) {
    this.taskFlag = taskFlag;
  }

  public String getTurnNum() {
    return turnNum;
  }

  public void setTurnNum(String turnNum) {
    this.turnNum = turnNum;
  }

  public String getDocType() {
    return docType;
  }

  public void setDocType(String docType) {
    this.docType = docType;
  }

  public String getPublicCate() {
    return publicCate;
  }

  public void setPublicCate(String publicCate) {
    this.publicCate = publicCate;
  }

  public Integer getPrintNum() {
    return printNum;
  }

  public void setPrintNum(Integer printNum) {
    this.printNum = printNum;
  }

  public Integer getDocMarkNum() {
    return docMarkNum;
  }

  public void setDocMarkNum(Integer docMarkNum) {
    this.docMarkNum = docMarkNum;
  }

  public Integer getDocMarkYear() {
    return docMarkYear;
  }

  public void setDocMarkYear(Integer docMarkYear) {
    this.docMarkYear = docMarkYear;
  }

  public Integer getDocSequenceYear() {
    return docSequenceYear;
  }

  public void setDocSequenceYear(Integer docSequenceYear) {
    this.docSequenceYear = docSequenceYear;
  }

  public ArchiveTypeEnum getArchiveType() {
    return archiveType;
  }

  public void setArchiveType(ArchiveTypeEnum archiveType) {
    this.archiveType = archiveType;
  }

  public String getArchiveFlag() {
    return archiveFlag;
  }

  public void setArchiveFlag(String archiveFlag) {
    this.archiveFlag = archiveFlag;
  }

  public String getAttachDesc() {
    return attachDesc;
  }

  public void setAttachDesc(String attachDesc) {
    this.attachDesc = attachDesc;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getParentClass() {
    return parentClass;
  }

  public void setParentClass(String parentClass) {
    this.parentClass = parentClass;
  }

  public String getChildrenClass() {
    return childrenClass;
  }

  public void setChildrenClass(String childrenClass) {
    this.childrenClass = childrenClass;
  }

  public String getReflectionCategory() {
    return reflectionCategory;
  }

  public void setReflectionCategory(String reflectionCategory) {
    this.reflectionCategory = reflectionCategory;
  }

  public String getMeetingSession() {
    return meetingSession;
  }

  public void setMeetingSession(String meetingSession) {
    this.meetingSession = meetingSession;
  }

  public String getProposalNum() {
    return proposalNum;
  }

  public void setProposalNum(String proposalNum) {
    this.proposalNum = proposalNum;
  }

  public String getLeadingUser() {
    return leadingUser;
  }

  public void setLeadingUser(String leadingUser) {
    this.leadingUser = leadingUser;
  }

  public String getLeadingPhoto() {
    return leadingPhoto;
  }

  public void setLeadingPhoto(String leadingPhoto) {
    this.leadingPhoto = leadingPhoto;
  }

  public String getLeadingPlace() {
    return leadingPlace;
  }

  public void setLeadingPlace(String leadingPlace) {
    this.leadingPlace = leadingPlace;
  }

  public Set<String> getSecondedUsers() {
    return secondedUsers;
  }

  public void setSecondedUsers(Set<String> secondedUsers) {
    this.secondedUsers = secondedUsers;
  }

  public String getSecondedNum() {
    return secondedNum;
  }

  public void setSecondedNum(String secondedNum) {
    this.secondedNum = secondedNum;
  }

  public String getCauseAction() {
    return causeAction;
  }

  public void setCauseAction(String causeAction) {
    this.causeAction = causeAction;
  }

  public Set<String> getMainOrganizer() {
    return mainOrganizer;
  }

  public void setMainOrganizer(Set<String> mainOrganizer) {
    this.mainOrganizer = mainOrganizer;
  }

  public Set<String> getAssistOrganizer() {
    return assistOrganizer;
  }

  public void setAssistOrganizer(Set<String> assistOrganizer) {
    this.assistOrganizer = assistOrganizer;
  }

  public String getAssignmentNum() {
    return assignmentNum;
  }

  public void setAssignmentNum(String assignmentNum) {
    this.assignmentNum = assignmentNum;
  }

  public Date getAssignmentDate() {
    return assignmentDate;
  }

  public void setAssignmentDate(Date assignmentDate) {
    this.assignmentDate = assignmentDate;
  }

  public Date getRequestDate() {
    return requestDate;
  }

  public void setRequestDate(Date requestDate) {
    this.requestDate = requestDate;
  }

  public String getUndertakeDepartment() {
    return undertakeDepartment;
  }

  public void setUndertakeDepartment(String undertakeDepartment) {
    this.undertakeDepartment = undertakeDepartment;
  }

  public String getHandlingMode() {
    return handlingMode;
  }

  public void setHandlingMode(String handlingMode) {
    this.handlingMode = handlingMode;
  }

  public String getUrgentLevel() {
    return urgentLevel;
  }

  public void setUrgentLevel(String urgentLevel) {
    this.urgentLevel = urgentLevel;
  }

  public String getAssignmentUser() {
    return assignmentUser;
  }

  public void setAssignmentUser(String assignmentUser) {
    this.assignmentUser = assignmentUser;
  }

  public String getAssignmentPhoto() {
    return assignmentPhoto;
  }

  public void setAssignmentPhoto(String assignmentPhoto) {
    this.assignmentPhoto = assignmentPhoto;
  }

  public String getImportLevel() {
    return importLevel;
  }

  public void setImportLevel(String importLevel) {
    this.importLevel = importLevel;
  }

  public String getAssignmentRequirements() {
    return assignmentRequirements;
  }

  public void setAssignmentRequirements(String assignmentRequirements) {
    this.assignmentRequirements = assignmentRequirements;
  }

  public Set<String> getMainSend() {
    return mainSend;
  }

  public void setMainSend(Set<String> mainSend) {
    this.mainSend = mainSend;
  }

  public Set<String> getCopySend() {
    return copySend;
  }

  public void setCopySend(Set<String> copySend) {
    this.copySend = copySend;
  }

  public String getHandleUser() {
    return handleUser;
  }

  public void setHandleUser(String handleUser) {
    this.handleUser = handleUser;
  }

  public String getHandlePhoto() {
    return handlePhoto;
  }

  public void setHandlePhoto(String handlePhoto) {
    this.handlePhoto = handlePhoto;
  }

  public String getServiceAttitude() {
    return serviceAttitude;
  }

  public void setServiceAttitude(String serviceAttitude) {
    this.serviceAttitude = serviceAttitude;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getSystemNo() { return systemNo; }

  public void setSystemNo(String systemNo) { this.systemNo = systemNo; }

  public String getDocMark() {
    return docMark;
  }

  public void setDocMark(String docMark) {
    this.docMark = docMark;
  }

  public String getDocSequence() {
    return docSequence;
  }

  public void setDocSequence(String docSequence) {
    this.docSequence = docSequence;
  }

  public String getSecLevel() {
    return secLevel;
  }

  public void setSecLevel(String secLevel) {
    this.secLevel = secLevel;
  }

  public Date getDraftDate() {
    return draftDate;
  }

  public void setDraftDate(Date draftDate) {
    this.draftDate = draftDate;
  }

  public String getDraftUserNo() {
    return draftUserNo;
  }

  public void setDraftUserNo(String draftUserNo) {
    this.draftUserNo = draftUserNo;
  }

  public String getDraftUserName() {
    return draftUserName;
  }

  public void setDraftUserName(String draftUserName) {
    this.draftUserName = draftUserName;
  }

  public String getDraftDept() {
    return draftDept;
  }

  public void setDraftDept(String draftDept) {
    this.draftDept = draftDept;
  }

  public String getDraftDeptNo() {
    return draftDeptNo;
  }

  public void setDraftDeptNo(String draftDeptNo) {
    this.draftDeptNo = draftDeptNo;
  }

  public String getDraftUnit() {
    return draftUnit;
  }

  public void setDraftUnit(String draftUnit) {
    this.draftUnit = draftUnit;
  }

  public String getDraftUnitNo() {
    return draftUnitNo;
  }

  public void setDraftUnitNo(String draftUnitNo) {
    this.draftUnitNo = draftUnitNo;
  }

  public String getSignUserNo() {
    return signUserNo;
  }

  public void setSignUserNo(String signUserNo) {
    this.signUserNo = signUserNo;
  }

  public String getSignUserName() {
    return signUserName;
  }

  public void setSignUserName(String signUserName) {
    this.signUserName = signUserName;
  }

  public String getSignDept() {
    return signDept;
  }

  public void setSignDept(String signDept) {
    this.signDept = signDept;
  }

  public String getSignDeptNo() {
    return signDeptNo;
  }

  public void setSignDeptNo(String signDeptNo) {
    this.signDeptNo = signDeptNo;
  }

  public String getSignUnit() {
    return signUnit;
  }

  public void setSignUnit(String signUnit) {
    this.signUnit = signUnit;
  }

  public String getSignUnitNo() {
    return signUnitNo;
  }

  public void setSignUnitNo(String signUnitNo) {
    this.signUnitNo = signUnitNo;
  }

  public Date getSignDate() {
    return signDate;
  }

  public void setSignDate(Date signDate) {
    this.signDate = signDate;
  }

  public CleanFlagEnum getSignDateCleanFlag() {
    return signDateCleanFlag;
  }

  public void setSignDateCleanFlag(CleanFlagEnum signDateCleanFlag) {
    this.signDateCleanFlag = signDateCleanFlag;
  }

  public String getSignFlag() {
    return signFlag;
  }

  public void setSignFlag(String signFlag) {
    this.signFlag = signFlag;
  }

  public String getLeadingNo() {
    return leadingNo;
  }

  public void setLeadingNo(String leadingNo) {
    this.leadingNo = leadingNo;
  }

  public String getSecondedNo() {
    return secondedNo;
  }

  public void setSecondedNo(String secondedNo) {
    this.secondedNo = secondedNo;
  }

  public String getAssignmentUserNo() {
    return assignmentUserNo;
  }

  public void setAssignmentUserNo(String assignmentUserNo) {
    this.assignmentUserNo = assignmentUserNo;
  }

  public String getHandleUserNo() {
    return handleUserNo;
  }

  public void setHandleUserNo(String handleUserNo) {
    this.handleUserNo = handleUserNo;
  }

  public Set<String> getReaders() {
    return readers;
  }

  public void setReaders(Set<String> readers) {
    this.readers = readers;
  }

  public String getDocWord() {
    return docWord;
  }

  public void setDocWord(String docWord) {
    this.docWord = docWord;
  }

  public String getDocCate() {
    return docCate;
  }

  public void setDocCate(String docCate) {
    this.docCate = docCate;
  }

  public String getRelReceivalMark() {
    return relReceivalMark;
  }

  public void setRelReceivalMark(String relReceivalMark) {
    this.relReceivalMark = relReceivalMark;
  }

  public TransferLibraryTypeEnum getTransferLibraryType() {
    return transferLibraryType;
  }

  public void setTransferLibraryType(TransferLibraryTypeEnum transferLibraryType) {
    this.transferLibraryType = transferLibraryType;
  }

  public String getDealFormId() {
    return dealFormId;
  }

  public void setDealFormId(String dealFormId) {
    this.dealFormId = dealFormId;
  }

  public Integer getPageNum() {
    return pageNum;
  }

  public void setPageNum(Integer pageNum) {
    this.pageNum = pageNum;
  }

  public HashMap<String, Object> toMap() {
    HashMap<String, Object> map = new HashMap<>(15);
    map.put(ModuleFiledConst.DOC_ID, this.id);
    map.put(ModuleFiledConst.SUBJECT, this.subject);
    map.put(ModuleFiledConst.DOC_MARK, this.docMark);
    map.put(ModuleFiledConst.DOC_SEQUENCE, this.docSequence);
    map.put(ModuleFiledConst.DOC_DATE, this.draftDate);
    map.put(ModuleFiledConst.SECRET, this.secLevel);
    map.put(ModuleFiledConst.BUSINESS_NO, "PROPOSALMOTION");
    map.put(ModuleFiledConst.BUSINESS_NAME, "提案议案");
    map.put(ModuleFiledConst.BUSINESS_CATE, "003");
    map.put(ModuleFiledConst.REG_ORG_NAME, this.draftDept);
    map.put(ModuleFiledConst.PRIORITY, this.urgentLevel);

    return map;
  }
  public HashMap<String, Object> toSolrMap() {
    HashMap<String, Object> map = new HashMap<>(15);

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Calendar c = null;

    map.put("S_module", "PROPOSALMOTION");
    map.put("S_moduleDes", "提案议案");
    map.put("S_businessNo", "PROPOSALMOTION");
    map.put("S_businessName", "提案议案");
    map.put("S_rjSearchUrl", "");

    if (StringUtils.isNotBlank(this.id)) {
      map.put("id", this.id);
    }

    if (StringUtils.isNotBlank(this.subject)) {
      map.put("C_subject", this.subject);
      map.put("S_subject2", this.subject);
    }

    if (this.getDraftDate() != null) {
      map.put("T_creattime", this.getDraftDate());
    }

    if (StringUtils.isNotBlank(this.getFlowStatus())) {
      map.put("S_flowStatus", this.getFlowStatus());
    }

    if (this.mainSend != null && this.mainSend.size() > 0) {
      map.put("R_mainSend", this.mainSend.toArray(new String[this.mainSend.size()]));
    }

    if (this.docMark != null) {
      map.put("S_docMark", this.docMark);
    }

    if (StringUtils.isNotBlank(this.docSequence)) {
      map.put("S_docSequence", this.docSequence);
    }
    if (StringUtils.isNotBlank(this.urgentLevel)) {
      map.put("S_urgenLevel", this.urgentLevel);
    }
    if (StringUtils.isNotBlank(this.secLevel)) {
      map.put("S_secLevel", this.secLevel);
    }


    if (StringUtils.isNotBlank(this.signUserName)) {
      map.put("S_signUserName", this.signUserName);
    }

    if (StringUtils.isNotBlank(this.draftDept)) {
      map.put("S_draftDept", this.draftDept);
    }
    if (StringUtils.isNotBlank(this.draftUserName)) {
      map.put("S_draftUserName", this.draftUserName);
    }

    if (StringUtils.isNotBlank(this.signFlag)) {
      map.put("S_signFlag", this.signFlag);
    }


    if (this.draftDate != null) {
      // 写入时间 年月
      String draftDateStr = dateFormat.format(this.draftDate.getTime());
      map.put("S_createTime", draftDateStr);
      map.put("T_createTime", this.draftDate);
      map.put("S_draftTime", draftDateStr);
      map.put("T_draftTime", this.draftDate);

      c = Calendar.getInstance();
      c.setTime(this.draftDate);
      map.put("I_draftYear", c.get(Calendar.YEAR));
      map.put("I_draftMonth", c.get(Calendar.MONTH) + 1);
      map.put("I_draftDay", c.get(Calendar.DAY_OF_MONTH));
    }

    if (null != this.signDate) {
      c = Calendar.getInstance();
      c.setTime(this.signDate);
      String signDateStr = dateFormat.format(this.signDate.getTime());
      map.put("S_signDateDes", signDateStr);
      map.put("T_signDate", this.signDate);
      map.put("I_signYear", c.get(Calendar.YEAR));
      map.put("I_signMonth", c.get(Calendar.MONTH) + 1);
      map.put("I_signDay", c.get(Calendar.DAY_OF_MONTH));
    } else if (this.getSignDateCleanFlag() == CleanFlagEnum.CLEAN_UP) {
      map.put("S_signDateDes", null);
      map.put("T_signDate", null);
      map.put("I_signYear", null);
      map.put("I_signMonth", null);
      map.put("I_signDay", null);
    }

    List<String> body = ToSolrMapUtil.getFileStringByTikaList(this.id, this.getFlowStatus());
    if (body != null && body.size() > 0) {
      map.put("BODY_zw", body);
    }
    return map;
  }


}

