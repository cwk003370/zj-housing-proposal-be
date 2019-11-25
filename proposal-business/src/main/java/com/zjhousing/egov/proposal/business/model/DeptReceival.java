package com.zjhousing.egov.proposal.business.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

/**
 * 部门文库 model
 *
 * @author lindongmei
 * @date 2018/11/14
 **/
public class DeptReceival {

  private String id;
  /**
   * 如果是收发文来的， 记录收发文文件id，以便收发文查询转部门收文记录，以及进行的撤回操作。登记时为空
   */
  private String sourceId;
  /**
   * 必填，来文单位。发文则为福建海事局（各分支局），收文为实际来文单位
   */
  private String sourceUnit;
  /**
   * 必填 文件标题
   */
  private String subject;
  /**
   * 紧急程度
   */
  private String urgenLevel;
  /**
   * 文件密级
   */
  private String secLevel;
  /**
   * 来文的 文件字号
   */
  private String docMark;

  /**
   * 收文日期 自动生成当前时间  收文和发文转过来的 签收日期
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date dealDate;
  /**
   * 登记人、收文人
   */
  private String draftUser;
  private String draftUserNo;
  /**
   * 登记人、收文人部门
   */
  private String draftUserDept;
  private String draftUserDeptNo;
  /**
   * 登记人、收文人单位
   */
  private String draftUserUnit;
  private String draftUserUnitNo;
  /**
   * 登记时间、签收时的签收时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date draftDate;
  /**
   * 流水号 流水号格式为：[年度]BM[序号]，新年度以登记时间先后为顺序从第1号开始顺序编号，
   * ： 2018BM0001、2018BM0002…。 [1：分开编+部门编码]
   */
  private String docSequence;

  /**
   * 流水号年份
   */
  private Integer docSequenceYear;
  /**
   * 备注-REMARK
   */
  private String remark;
  /**
   * 接收部门 系统自动生成，当前的接收部门
   */
  private String receivalDept;
  private String receivalDeptNo;
  private String receivalUserNo;
  private String receivalUser;
  /**
   * 收文或发文转部门文库后，部门正职领导将在首页待办文件中收到该文件，
   * 打开该文件后即算签收，而后由部门领导进行文件的分送
   * 0 代表未签收状态， 1代表已签收状态
   */
  private String receivalStatus;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp receivalTime;


  private String sourceUserNo;
  private String sourceUser;
  /**
   * 来文类型 单选：收文；发文；手动登记
   */
  private String sourceCategory;
  /**
   * 文件链接
   */
  private String fileLink;
  /**
   * 查阅权限
   */
  private Set<String> readers;
  private String unitNo;
  /**
   * 流程实例ID
   */
  private String flowPid;
  /**
   * 流程模板编码
   */
  private String flowLabel;
  /**
   * 流程模板版本
   */
  private String flowVersion;
  /**
   * 当前流程状态
   */
  private String flowStatus;
  /**
   * 流程办结人员
   */
  private String flowDoneUser;
  /**
   * 统一部署所属系统编码
   */
  private String systemNo;
  /**
   * 办阅件标识
   */
  private String handleOrView;
  /**
   * 文件分类
   */
  private String docCate;

  /**
   * 办理期限
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date deadLine;

  /**
   * solr作业使用 开始时间
   */
  private Date front_RangeStartDate;
  /**
   * solr作业使用 结束时间
   */
  private Date front_RangeEndDate;
  /**
   * 高级搜索起始日期
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date beginDate;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date endDate;

  public Timestamp getReceivalTime() {
    return this.receivalTime;
  }

  public void setReceivalTime(Timestamp receivalTime) {
    this.receivalTime = receivalTime;
  }

  public String getReceivalUserNo() {
    return this.receivalUserNo;
  }

  public void setReceivalUserNo(String receivalUserNo) {
    this.receivalUserNo = receivalUserNo;
  }

  public String getReceivalUser() {
    return this.receivalUser;
  }

  public void setReceivalUser(String receivalUser) {
    this.receivalUser = receivalUser;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSourceId() {
    return this.sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getSourceUnit() {
    return this.sourceUnit;
  }

  public void setSourceUnit(String sourceUnit) {
    this.sourceUnit = sourceUnit;
  }

  public String getSubject() {
    return this.subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getUrgenLevel() {
    return this.urgenLevel;
  }

  public void setUrgenLevel(String urgenLevel) {
    this.urgenLevel = urgenLevel;
  }

  public String getSecLevel() {
    return this.secLevel;
  }

  public void setSecLevel(String secLevel) {
    this.secLevel = secLevel;
  }

  public String getDocMark() {
    return this.docMark;
  }

  public void setDocMark(String docMark) {
    this.docMark = docMark;
  }

  public Date getDealDate() {
    return this.dealDate;
  }

  public void setDealDate(Date dealDate) {
    this.dealDate = dealDate;
  }

  public String getDraftUser() {
    return this.draftUser;
  }

  public void setDraftUser(String draftUser) {
    this.draftUser = draftUser;
  }

  public String getDraftUserNo() {
    return this.draftUserNo;
  }

  public void setDraftUserNo(String draftUserNo) {
    this.draftUserNo = draftUserNo;
  }

  public String getDraftUserDept() {
    return this.draftUserDept;
  }

  public void setDraftUserDept(String draftUserDept) {
    this.draftUserDept = draftUserDept;
  }

  public String getDraftUserDeptNo() {
    return this.draftUserDeptNo;
  }

  public void setDraftUserDeptNo(String draftUserDeptNo) {
    this.draftUserDeptNo = draftUserDeptNo;
  }

  public String getDraftUserUnit() {
    return this.draftUserUnit;
  }

  public void setDraftUserUnit(String draftUserUnit) {
    this.draftUserUnit = draftUserUnit;
  }

  public String getDraftUserUnitNo() {
    return this.draftUserUnitNo;
  }

  public void setDraftUserUnitNo(String draftUserUnitNo) {
    this.draftUserUnitNo = draftUserUnitNo;
  }

  public Date getDraftDate() {
    return this.draftDate;
  }

  public void setDraftDate(Date draftDate) {
    this.draftDate = draftDate;
  }

  public String getDocSequence() {
    return this.docSequence;
  }

  public void setDocSequence(String docSequence) {
    this.docSequence = docSequence;
  }

  public Integer getDocSequenceYear() {
    return this.docSequenceYear;
  }

  public void setDocSequenceYear(Integer docSequenceYear) {
    this.docSequenceYear = docSequenceYear;
  }

  public String getRemark() {
    return this.remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getReceivalDept() {
    return this.receivalDept;
  }

  public void setReceivalDept(String receivalDept) {
    this.receivalDept = receivalDept;
  }

  public String getReceivalDeptNo() {
    return this.receivalDeptNo;
  }

  public void setReceivalDeptNo(String receivalDeptNo) {
    this.receivalDeptNo = receivalDeptNo;
  }

  public String getSourceCategory() {
    return this.sourceCategory;
  }

  public void setSourceCategory(String sourceCategory) {
    this.sourceCategory = sourceCategory;
  }

  public String getFileLink() {
    return this.fileLink;
  }

  public void setFileLink(String fileLink) {
    this.fileLink = fileLink;
  }

  public Set<String> getReaders() {
    return this.readers;
  }

  public void setReaders(Set<String> readers) {
    this.readers = readers;
  }

  public String getReceivalStatus() {
    return this.receivalStatus;
  }

  public void setReceivalStatus(String receivalStatus) {
    this.receivalStatus = receivalStatus;
  }

  public String getUnitNo() {
    return this.unitNo;
  }

  public void setUnitNo(String unitNo) {
    this.unitNo = unitNo;
  }

  public String getFlowPid() {
    return this.flowPid;
  }

  public void setFlowPid(String flowPid) {
    this.flowPid = flowPid;
  }

  public String getFlowLabel() {
    return this.flowLabel;
  }

  public void setFlowLabel(String flowLabel) {
    this.flowLabel = flowLabel;
  }

  public String getFlowVersion() {
    return this.flowVersion;
  }

  public void setFlowVersion(String flowVersion) {
    this.flowVersion = flowVersion;
  }

  public String getFlowStatus() {
    return this.flowStatus;
  }

  public void setFlowStatus(String flowStatus) {
    this.flowStatus = flowStatus;
  }

  public String getFlowDoneUser() {
    return this.flowDoneUser;
  }

  public void setFlowDoneUser(String flowDoneUser) {
    this.flowDoneUser = flowDoneUser;
  }

  public String getHandleOrView() {
    return this.handleOrView;
  }

  public void setHandleOrView(String handleOrView) {
    this.handleOrView = handleOrView;
  }

  public String getDocCate() {
    return this.docCate;
  }

  public void setDocCate(String docCate) {
    this.docCate = docCate;
  }

  public Date getDeadLine() {
    return this.deadLine;
  }

  public void setDeadLine(Date deadLine) {
    this.deadLine = deadLine;
  }

  public Date getBeginDate() {
    return this.beginDate;
  }

  public void setBeginDate(Date beginDate) {
    this.beginDate = beginDate;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String getSystemNo() {
    return this.systemNo;
  }

  public void setSystemNo(String systemNo) {
    this.systemNo = systemNo;
  }

  public Date getFront_RangeStartDate() {
    return this.front_RangeStartDate;
  }

  public void setFront_RangeStartDate(Date front_RangeStartDate) {
    this.front_RangeStartDate = front_RangeStartDate;
  }

  public Date getFront_RangeEndDate() {
    return this.front_RangeEndDate;
  }

  public void setFront_RangeEndDate(Date front_RangeEndDate) {
    this.front_RangeEndDate = front_RangeEndDate;
  }

  public String getSourceUserNo() {
    return this.sourceUserNo;
  }

  public void setSourceUserNo(String sourceUserNo) {
    this.sourceUserNo = sourceUserNo;
  }

  public String getSourceUser() {
    return this.sourceUser;
  }

  public void setSourceUser(String sourceUser) {
    this.sourceUser = sourceUser;
  }

  @Override
  public String toString() {
    return "DeptReceival{" +
      "id='" + this.id + '\'' +
      ", sourceId='" + this.sourceId + '\'' +
      ", sourceUnit='" + this.sourceUnit + '\'' +
      ", subject='" + this.subject + '\'' +
      ", urgenLevel='" + this.urgenLevel + '\'' +
      ", secLevel='" + this.secLevel + '\'' +
      ", docMark='" + this.docMark + '\'' +
      ", dealDate=" + this.dealDate +
      ", draftUser='" + this.draftUser + '\'' +
      ", draftUserNo='" + this.draftUserNo + '\'' +
      ", draftUserDept='" + this.draftUserDept + '\'' +
      ", draftUserDeptNo='" + this.draftUserDeptNo + '\'' +
      ", draftUserUnit='" + this.draftUserUnit + '\'' +
      ", draftUserUnitNo='" + this.draftUserUnitNo + '\'' +
      ", draftDate=" + this.draftDate +
      ", docSequence='" + this.docSequence + '\'' +
      ", docSequenceYear=" + this.docSequenceYear +
      ", remark='" + this.remark + '\'' +
      ", receivalDept='" + this.receivalDept + '\'' +
      ", receivalDeptNo='" + this.receivalDeptNo + '\'' +
      ", receivalUserNo='" + this.receivalUserNo + '\'' +
      ", receivalUser='" + this.receivalUser + '\'' +
      ", sourceUserNo='" + this.sourceUserNo + '\'' +
      ", sourceUser='" + this.sourceUser + '\'' +
      ", sourceCategory='" + this.sourceCategory + '\'' +
      ", fileLink='" + this.fileLink + '\'' +
      ", readers=" + this.readers +
      ", receivalStatus='" + this.receivalStatus + '\'' +
      ", unitNo='" + this.unitNo + '\'' +
      ", flowPid='" + this.flowPid + '\'' +
      ", flowLabel='" + this.flowLabel + '\'' +
      ", flowVersion='" + this.flowVersion + '\'' +
      ", flowStatus='" + this.flowStatus + '\'' +
      ", flowDoneUser='" + this.flowDoneUser + '\'' +
      ", systemNo='" + this.systemNo + '\'' +
      ", handleOrView='" + this.handleOrView + '\'' +
      ", docCate='" + this.docCate + '\'' +
      ", deadLine=" + this.deadLine +
      ", front_RangeStartDate=" + this.front_RangeStartDate +
      ", front_RangeEndDate=" + this.front_RangeEndDate +
      ", beginDate=" + this.beginDate +
      ", endDate=" + this.endDate +
      '}';
  }
}
