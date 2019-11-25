package com.zjhousing.egov.proposal.business.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rongji.egov.utils.spring.validation.InsertValidate;
import com.rongji.egov.utils.spring.validation.UpdateValidate;
import com.rongji.egov.wflow.business.constant.ModuleFiledConst;
import com.rongji.egov.wflow.business.model.FlowObject;
import com.zjhousing.egov.proposal.business.enums.ArchiveTypeEnum;
import com.zjhousing.egov.proposal.business.enums.ReturnFlagEnum;
import com.zjhousing.egov.proposal.business.util.ToSolrMapUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * {@link Receival}类定义收文主表单信息
 *
 * <p>提供主表单信息
 * <ul>
 *      <li>提供{@link #toMap()}方法供流程流转使用</li>
 *      <li>提供{@link #toSolrMap()}方法供全文检索使用</li>
 * </ul>
 *
 * @author luzhangfei
 */
public class Receival extends FlowObject {
  /**
   * ID
   */
  @NotEmpty(message = "id不能为空", groups = {UpdateValidate.class})
  @Length(min = 16, max = 16, message = "id{fixedLength}", groups = {UpdateValidate.class})
  private String id;

  /**
   * 公文标识
   */
  @Length(min = 16, max = 16, message = "id{fixedLength}", groups = {UpdateValidate.class})
  private String docStdId;

  /**
   * 文件字号
   */
  @NotEmpty(message = "文件字号不能为空", groups = {InsertValidate.class, UpdateValidate.class})
  @Length(min = 4, max = 64, message = "id{fixedLength}", groups = {InsertValidate.class, UpdateValidate.class})
  private String docMark;

  /**
   * 流水号
   */
  private String docSequence;

  /**
   * 流水序号-DOC_SEQUENCE_NUM
   */
  private Integer docSequenceNum;

  /**
   * 流水号年份
   */
  @Range(max = 9999, message = "序号位数{range}", groups = {UpdateValidate.class})
  private Integer docSequenceYear;

  /**
   * 办阅件标识
   */
  @NotEmpty(message = "办阅件标识不能为空", groups = {InsertValidate.class, UpdateValidate.class})
  private String handleOrView;

  /**
   * 收文分类
   */
  @NotEmpty(message = "收文分类不能为空", groups = {InsertValidate.class, UpdateValidate.class})
  private String fileCategory;

  /**
   * 来文单位
   */
  @NotEmpty(message = "来文单位不能为空", groups = {InsertValidate.class, UpdateValidate.class})
  private String sourceUnit;


  private String draftUser;
  private String draftUserNo;
  private String draftUserDept;
  private String draftUserDeptNo;
  private String draftUserUnit;
  private String draftUserUnitNo;
  @NotEmpty(message = "文件标题不能为空", groups = {InsertValidate.class, UpdateValidate.class})
  private String subject;
  /**
   * 紧急程序
   */
  @NotEmpty(message = "紧急程序不能为空", groups = {InsertValidate.class, UpdateValidate.class})
  private String urgenLevel;
  /**
   * 文件密级
   */
  @NotEmpty(message = "文件密级不能为空", groups = {InsertValidate.class, UpdateValidate.class})
  private String secLevel;
  /**
   * 批示内容
   */
  private String dealContent;
  /**
   * 份数
   */
  private Integer copyNum;
  /**
   * 页数
   */
  private Integer pageNum;
  /**
   * 保密期限
   */
  private String secureTerm;
  /**
   * 归档类型
   */

  private ArchiveTypeEnum archiveType;
  /**
   * 归档状态
   */
  @NotEmpty(message = "归档状态不能为空", groups = {InsertValidate.class, UpdateValidate.class})
  private String archiveFlag;

  /**
   * 成文日期
   */
  @NotEmpty(message = "成文日期不能为空", groups = {InsertValidate.class, UpdateValidate.class})
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date signDate;

  /**
   * 收文日期
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date draftDate;
  /**
   * 交办单位
   */
  private String dealUnit;
  private String dealUnitNo;

  /**
   * 交办日期
   */
  @NotEmpty(message = "归档状态不能为空", groups = {InsertValidate.class, UpdateValidate.class})
  @JsonFormat(pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date dealDate;
  /**
   * 文种
   */
  private String docType;
  /**
   * 办理期限
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date deadLine;
  /**
   * 批办领导
   */
  private String handleLeader;
  private String handleLeaderNo;
  private String handleUnit;
  private String handleUnitNo;
  /**
   * 协办单位
   */
  private List<String> assistUnit;
  private List<String> assistUnitNo;
  /**
   * 传阅单位
   */
  private List<String> viewUnit;
  private List<String> viewUnitNo;
  /**
   * 分发单位
   */
  private String dispatchRange;
  private String dispatchRangeNo;
  /**
   * 查询级别
   */
  private String selectLevel;
  /**
   * 清退标志
   */
  private ReturnFlagEnum returnFlag;
  private String remark;
  private String abstracts;
  private Set<String> readers;
  private String dealFormId;

  /**
   * 文件类型
   */
  private String docCate;
  /**
   * 定密依据
   */
  private String secLevelBase;
  /**
   * 保管期限
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date retentionTerm;
  /**
   * 文件去向
   */
  private String direction;
  /**
   * 文件追踪
   */
  private String docTrack;
  /**
   * 关联文件
   */
  private String relDocMark;
  /**
   * 运转单的值集合
   */
  private String transData;

  /**
   * 公文交换签收文的ID SWAP_ID
   */
  private String swapId;
  /**
   * 公文交换退为状态 SWAP_RETURN ( 0 未退文 1 已退文)
   */
  private String swapReturn;

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
  /**
   * 统一部署所属系统编码
   */
  private String systemNo;
  /**
   * 转任务标识,是否已转任务 1是 0否
   */
  private String taskFlag;
  /**
   * 二层页面搜索是否开启“全文检索”
   */
  private boolean fulltext = false;
  /**
   * 转重要文件、转工作提醒次数 json格式的字符串"{ExternalToOthersConstant.TO_WORK_REMINDER: 转工作提醒次数, ExternalToOthersConstant.TO_VITAL_DOCUMENT: 转重要文件次数}"
   */
  private String turnNum;
  /**
   * 内部传阅readers
   */
  private Set<String> inReceivalReaders;

  public Set<String> getInReceivalReaders() {
    return inReceivalReaders;
  }

  public void setInReceivalReaders(Set<String> inReceivalReaders) {
    this.inReceivalReaders = inReceivalReaders;
  }

  public String getTurnNum() {
    return turnNum;
  }

  public void setTurnNum(String turnNum) {
    this.turnNum = turnNum;
  }

  public Receival() {
  }

  public Receival(String id, String relDocMark) {
    this.id = id;
    this.relDocMark = relDocMark;
  }

  public String getSwapId() {
    return this.swapId;
  }

  public void setSwapId(String swapId) {
    this.swapId = swapId;
  }

  public String getSwapReturn() {
    return this.swapReturn;
  }

  public void setSwapReturn(String swapReturn) {
    this.swapReturn = swapReturn;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDocStdId() {
    return this.docStdId;
  }

  public void setDocStdId(String docStdId) {
    this.docStdId = docStdId;
  }

  public String getDocMark() {
    return this.docMark;
  }

  public void setDocMark(String docMark) {
    this.docMark = docMark;
  }

  public String getDocSequence() {
    return this.docSequence;
  }

  public void setDocSequence(String docSequence) {
    this.docSequence = docSequence;
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

  public Integer getDocSequenceYear() {
    return this.docSequenceYear;
  }

  public void setDocSequenceYear(Integer docSequenceYear) {
    this.docSequenceYear = docSequenceYear;
  }

  public String getHandleOrView() {
    return this.handleOrView;
  }

  public void setHandleOrView(String handleOrView) {
    this.handleOrView = handleOrView;
  }

  public String getFileCategory() {
    return this.fileCategory;
  }

  public void setFileCategory(String fileCategory) {
    this.fileCategory = fileCategory;
  }

  public String getSourceUnit() {
    return this.sourceUnit;
  }

  public void setSourceUnit(String sourceUnit) {
    this.sourceUnit = sourceUnit;
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

  public String getDealContent() {
    return this.dealContent;
  }

  public void setDealContent(String dealContent) {
    this.dealContent = dealContent;
  }

  public Integer getCopyNum() {
    return this.copyNum;
  }

  public void setCopyNum(Integer copyNum) {
    this.copyNum = copyNum;
  }

  public Integer getPageNum() {
    return this.pageNum;
  }

  public void setPageNum(Integer pageNum) {
    this.pageNum = pageNum;
  }

  public String getSecureTerm() {
    return this.secureTerm;
  }

  public void setSecureTerm(String secureTerm) {
    this.secureTerm = secureTerm;
  }

  public String getArchiveFlag() {
    return this.archiveFlag;
  }

  public void setArchiveFlag(String archiveFlag) {
    this.archiveFlag = archiveFlag;
  }

  public Date getSignDate() {
    return this.signDate;
  }

  public void setSignDate(Date signDate) {
    this.signDate = signDate;
  }

  public Date getDraftDate() {
    return this.draftDate;
  }

  public void setDraftDate(Date draftDate) {
    this.draftDate = draftDate;
  }

  public String getDealUnit() {
    return this.dealUnit;
  }

  public void setDealUnit(String dealUnit) {
    this.dealUnit = dealUnit;
  }

  public String getDealUnitNo() {
    return this.dealUnitNo;
  }

  public void setDealUnitNo(String dealUnitNo) {
    this.dealUnitNo = dealUnitNo;
  }

  public Date getDealDate() {
    return this.dealDate;
  }

  public void setDealDate(Date dealDate) {
    this.dealDate = dealDate;
  }

  public String getDocType() {
    return this.docType;
  }

  public void setDocType(String docType) {
    this.docType = docType;
  }

  public Date getDeadLine() {
    return this.deadLine;
  }

  public void setDeadLine(Date deadLine) {
    this.deadLine = deadLine;
  }

  public String getHandleLeader() {
    return this.handleLeader;
  }

  public void setHandleLeader(String handleLeader) {
    this.handleLeader = handleLeader;
  }

  public String getHandleLeaderNo() {
    return this.handleLeaderNo;
  }

  public void setHandleLeaderNo(String handleLeaderNo) {
    this.handleLeaderNo = handleLeaderNo;
  }

  public String getHandleUnit() {
    return this.handleUnit;
  }

  public void setHandleUnit(String handleUnit) {
    this.handleUnit = handleUnit;
  }

  public String getHandleUnitNo() {
    return this.handleUnitNo;
  }

  public void setHandleUnitNo(String handleUnitNo) {
    this.handleUnitNo = handleUnitNo;
  }

  public List<String> getAssistUnit() {
    return this.assistUnit;
  }

  public void setAssistUnit(List<String> assistUnit) {
    this.assistUnit = assistUnit;
  }

  public List<String> getAssistUnitNo() {
    return this.assistUnitNo;
  }

  public void setAssistUnitNo(List<String> assistUnitNo) {
    this.assistUnitNo = assistUnitNo;
  }

  public List<String> getViewUnit() {
    return this.viewUnit;
  }

  public void setViewUnit(List<String> viewUnit) {
    this.viewUnit = viewUnit;
  }

  public List<String> getViewUnitNo() {
    return this.viewUnitNo;
  }

  public void setViewUnitNo(List<String> viewUnitNo) {
    this.viewUnitNo = viewUnitNo;
  }

  public String getDispatchRange() {
    return this.dispatchRange;
  }

  public void setDispatchRange(String dispatchRange) {
    this.dispatchRange = dispatchRange;
  }

  public String getDispatchRangeNo() {
    return this.dispatchRangeNo;
  }

  public void setDispatchRangeNo(String dispatchRangeNo) {
    this.dispatchRangeNo = dispatchRangeNo;
  }

  public String getSelectLevel() {
    return this.selectLevel;
  }

  public void setSelectLevel(String selectLevel) {
    this.selectLevel = selectLevel;
  }

  public String getRemark() {
    return this.remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getAbstracts() {
    return this.abstracts;
  }

  public void setAbstracts(String abstracts) {
    this.abstracts = abstracts;
  }

  public Set<String> getReaders() {
    return this.readers;
  }

  public void setReaders(Set<String> readers) {
    this.readers = readers;
  }

  public String getDealFormId() {
    return this.dealFormId;
  }

  public void setDealFormId(String dealFormId) {
    this.dealFormId = dealFormId;
  }

  public String getDocCate() {
    return this.docCate;
  }

  public void setDocCate(String docCate) {
    this.docCate = docCate;
  }

  public String getSecLevelBase() {
    return this.secLevelBase;
  }

  public void setSecLevelBase(String secLevelBase) {
    this.secLevelBase = secLevelBase;
  }

  public Date getRetentionTerm() {
    return this.retentionTerm;
  }

  public void setRetentionTerm(Date retentionTerm) {
    this.retentionTerm = retentionTerm;
  }

  public String getDirection() {
    return this.direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String getDocTrack() {
    return this.docTrack;
  }

  public void setDocTrack(String docTrack) {
    this.docTrack = docTrack;
  }

  public String getRelDocMark() {
    return this.relDocMark;
  }

  public void setRelDocMark(String relDocMark) {
    this.relDocMark = relDocMark;
  }

  public String getTransData() {
    return this.transData;
  }

  public void setTransData(String transData) {
    this.transData = transData;
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

  public ArchiveTypeEnum getArchiveType() {
    return this.archiveType;
  }

  public void setArchiveType(ArchiveTypeEnum archiveType) {
    this.archiveType = archiveType;
  }

  public ReturnFlagEnum getReturnFlag() {
    return this.returnFlag;
  }

  public void setReturnFlag(ReturnFlagEnum returnFlag) {
    this.returnFlag = returnFlag;
  }

  public String getTaskFlag() {
    return taskFlag;
  }

  public void setTaskFlag(String taskFlag) {
    this.taskFlag = taskFlag;
  }

  public boolean isFulltext() {
    return fulltext;
  }

  public void setFulltext(boolean fulltext) {
    this.fulltext = fulltext;
  }

  public Integer getDocSequenceNum() {
    return docSequenceNum;
  }

  public void setDocSequenceNum(Integer docSequenceNum) {
    this.docSequenceNum = docSequenceNum;
  }

  /**
   * 生成Map供流程流转中使用
   * <p>
   * <p>在流程流转过程中，由于要得到业务中信息。提供当前方法提供信息。
   * <p>Map定义的key值以流程中{@link ModuleFiledConst} 提供
   *
   * @return {@code HashMap}
   */
  public HashMap<String, Object> toMap() {
    HashMap<String, Object> map = new HashMap<>(15);
    map.put(ModuleFiledConst.DOC_ID, this.id);
    map.put(ModuleFiledConst.SUBJECT, this.subject);
    map.put(ModuleFiledConst.DOC_MARK, this.docMark);
    map.put(ModuleFiledConst.DOC_SEQUENCE, this.docSequence);
    map.put(ModuleFiledConst.DOC_DATE, this.draftDate);
    map.put(ModuleFiledConst.SECRET, this.secLevel);
    map.put(ModuleFiledConst.BUSINESS_NO, "RECEIVAL");
    map.put(ModuleFiledConst.BUSINESS_NAME, "收文");
    map.put(ModuleFiledConst.BUSINESS_CATE, "001");
    map.put(ModuleFiledConst.REG_ORG_NAME, this.draftUserDept);
    map.put(ModuleFiledConst.PRIORITY, this.urgenLevel);
    return map;
  }

  /**
   * 添加到Solr中的字段转化
   * <p>
   * <p>公文信息转Solr信息，提供手动转化。各字段定义。
   *
   * @return {@code HashMap}
   */
  public HashMap<String, Object> toSolrMap() {
    HashMap<String, Object> map = new HashMap<>(15);

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Calendar c;

    map.put("S_module", "RECEIVAL");
    map.put("S_moduleDes", "收文");
    map.put("S_businessNo", "RECEIVAL");
    map.put("S_businessName", "收文");
    map.put("S_rjSearchUrl", "");

    if(StringUtils.isNotBlank(this.id)){
      map.put("id", this.id);
    }
    if(StringUtils.isNotBlank(this.subject)){
      map.put("C_subject", this.subject);
      map.put("S_subject2", this.subject);
    }

    if(StringUtils.isNotBlank(this.getFlowStatus())){
      map.put("S_flowStatus", this.getFlowStatus());
    }

    if (!StringUtils.equals(this.getFlowStatus(), "8")) {
      Set<String> strings = new HashSet<>();
      strings.add("sys_manager");
      strings.add("receival_manager");
      if (null != this.readers && this.readers.size() > 0) {
        strings.addAll(this.readers);
      }
      if (this.inReceivalReaders != null && this.inReceivalReaders.size() > 0) {
        strings.addAll(this.inReceivalReaders);
        map.put("R_inReceivalReaders", this.inReceivalReaders.toArray(new String[this.inReceivalReaders.size()]));
      }
      map.put("R_readers", strings.toArray(new String[strings.size()]));
    } else {
      map.put("R_readers", null);
    }

    if(StringUtils.isNotBlank(this.docMark)){
      map.put("S_docMark", this.docMark);
    }

    if(this.docSequence != null){
      map.put("S_docSequence", this.docSequence);
    }

    if(StringUtils.isNotBlank(this.sourceUnit)){
      map.put("S_sourceUnit", this.sourceUnit);
    }

    if(StringUtils.isNotBlank(this.urgenLevel)){
      map.put("S_urgenLevel", this.urgenLevel);
    }

    if(StringUtils.isNotBlank(this.secLevel)){
      map.put("S_secLevel", this.secLevel);
    }
    if (null != this.archiveType) {
      map.put("S_archiveType", this.archiveType.name());
    }
    if(StringUtils.isNotBlank(this.archiveFlag)){
      map.put("S_archiveFlag", this.archiveFlag);
    }
    if(StringUtils.isNotBlank(this.draftUserDept)){
      map.put("S_draftUserDept", this.draftUserDept);
    }
    if(StringUtils.isNotBlank(this.draftUser)){
      map.put("S_draftUser", this.draftUser);
    }
    if (StringUtils.isNotBlank(this.handleUnit)) {
      map.put("S_handleUnit", this.handleUnit);
    }
    if (StringUtils.isNotBlank(this.fileCategory)) {
      map.put("S_fileCategory", this.fileCategory);
    }

    if (StringUtils.isNotBlank(this.systemNo)) {
      map.put("S_systemNo", this.systemNo);
    }

    if(null!=this.draftDate){
      // 写入时间 年月
      String dateStr = dateFormat.format(this.draftDate.getTime());
      map.put("S_createTime", dateStr);
      map.put("T_createTime", this.draftDate);
      map.put("S_draftTimeDes", dateStr);
      map.put("T_draftTime", this.draftDate);
      c = Calendar.getInstance();
      c.setTime(this.draftDate);
      map.put("I_draftYear", c.get(Calendar.YEAR));
      map.put("I_draftMonth", c.get(Calendar.MONTH) + 1);
      map.put("I_draftDay", c.get(Calendar.DAY_OF_MONTH));
    }

    List<String> body = ToSolrMapUtil.getFileStringByTikaList(this.id, this.getFlowStatus());
    if (body != null && body.size() > 0) {
      map.put("BODY_zw", body);
    }
    return map;
  }

  @Override
  public String toString() {
    return "Receival{" +
      "id='" + this.id + '\'' +
      ", docStdId='" + this.docStdId + '\'' +
      ", docMark='" + this.docMark + '\'' +
      ", docSequence='" + this.docSequence + '\'' +
      ", docSequenceYear=" + this.docSequenceYear +
      ", handleOrView='" + this.handleOrView + '\'' +
      ", fileCategory='" + this.fileCategory + '\'' +
      ", sourceUnit='" + this.sourceUnit + '\'' +
      ", draftUser='" + this.draftUser + '\'' +
      ", draftUserNo='" + this.draftUserNo + '\'' +
      ", draftUserDept='" + this.draftUserDept + '\'' +
      ", draftUserDeptNo='" + this.draftUserDeptNo + '\'' +
      ", draftUserUnit='" + this.draftUserUnit + '\'' +
      ", draftUserUnitNo='" + this.draftUserUnitNo + '\'' +
      ", subject='" + this.subject + '\'' +
      ", urgenLevel='" + this.urgenLevel + '\'' +
      ", secLevel='" + this.secLevel + '\'' +
      ", dealContent='" + this.dealContent + '\'' +
      ", copyNum=" + this.copyNum +
      ", pageNum=" + this.pageNum +
      ", secureTerm='" + this.secureTerm + '\'' +
      ", archiveType='" + this.archiveType + '\'' +
      ", archiveFlag='" + this.archiveFlag + '\'' +
      ", signDate=" + this.signDate +
      ", draftDate=" + this.draftDate +
      ", dealUnit='" + this.dealUnit + '\'' +
      ", dealUnitNo='" + this.dealUnitNo + '\'' +
      ", dealDate=" + this.dealDate +
      ", docType='" + this.docType + '\'' +
      ", deadLine=" + this.deadLine +
      ", handleLeader='" + this.handleLeader + '\'' +
      ", handleLeaderNo='" + this.handleLeaderNo + '\'' +
      ", handleUnit='" + this.handleUnit + '\'' +
      ", handleUnitNo='" + this.handleUnitNo + '\'' +
      ", assistUnit=" + this.assistUnit +
      ", assistUnitNo=" + this.assistUnitNo +
      ", viewUnit=" + this.viewUnit +
      ", viewUnitNo=" + this.viewUnitNo +
      ", dispatchRange='" + this.dispatchRange + '\'' +
      ", dispatchRangeNo='" + this.dispatchRangeNo + '\'' +
      ", selectLevel='" + this.selectLevel + '\'' +
      ", returnFlag='" + this.returnFlag + '\'' +
      ", remark='" + this.remark + '\'' +
      ", abstracts='" + this.abstracts + '\'' +
      ", readers=" + this.readers +
      ", dealFormId='" + this.dealFormId + '\'' +
      ", docCate='" + this.docCate + '\'' +
      ", secLevelBase='" + this.secLevelBase + '\'' +
      ", retentionTerm=" + this.retentionTerm +
      ", direction='" + this.direction + '\'' +
      ", docTrack='" + this.docTrack + '\'' +
      ", relDocMark='" + this.relDocMark + '\'' +
      ", transData='" + this.transData + '\'' +
      ", swapId='" + this.swapId + '\'' +
      ", swapReturn='" + this.swapReturn + '\'' +
      ", front_RangeStartDate=" + this.front_RangeStartDate +
      ", front_RangeEndDate=" + this.front_RangeEndDate +
      ", beginDate=" + this.beginDate +
      ", endDate=" + this.endDate +
      ", systemNo='" + this.systemNo + '\'' +
      ", taskFlag='" + this.taskFlag + '\'' +
      '}';
  }
}
