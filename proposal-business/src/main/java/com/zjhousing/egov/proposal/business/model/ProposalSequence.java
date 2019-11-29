package com.zjhousing.egov.proposal.business.model;

import com.rongji.egov.utils.spring.validation.InsertValidate;
import com.rongji.egov.utils.spring.validation.UpdateValidate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.List;
/**
 * 提案流水号
 *
 * @author chenwenkang
 */
public class ProposalSequence {
  /**
   * ID
   */
  @NotEmpty(message = "id不能为空", groups = {UpdateValidate.class})
  @Length(min = 16, max = 16, message = "id{fixedLength}", groups = {UpdateValidate.class})
  private String id;

  /**
   * 流水号名称
   */
  @NotEmpty(message = "流水号名称{notEmpty}", groups = {UpdateValidate.class, InsertValidate.class})
  @Length(max = 16, message = "流水号名称{fixedLength}", groups = {UpdateValidate.class, InsertValidate.class})
  private String sequenceName;

  /**
   * 序号位数
   */
  @NotNull(message = "序号位数{notEmpty}", groups = {UpdateValidate.class, InsertValidate.class})
  @Range(max = 9999, message = "序号位数{range}", groups = {UpdateValidate.class, InsertValidate.class})
  private Integer sequenceLength;

  /**
   * 序号初始值
   */
  @NotNull(message = "序号初始值{notEmpty}", groups = {UpdateValidate.class, InsertValidate.class})
  @Range(max = 9999, message = "序号初始值{range}", groups = {UpdateValidate.class, InsertValidate.class})
  private Integer initValue;

  /**
   * 流水号模式
   */
  @NotEmpty(message = "流水号模式{notEmpty}", groups = {UpdateValidate.class, InsertValidate.class})
  @Length(max = 56, message = "流水号模式{fixedLength}", groups = {UpdateValidate.class, InsertValidate.class})
  private String sequenceMode;

  /**
   * 文件分类
   */
  @NotBlank(message = "文件分类不能为空", groups = {UpdateValidate.class, InsertValidate.class})
  private String docCate;

  /**
   * 状态(1正常,0停用)
   */
  @NotEmpty(message = "状态{notEmpty}", groups = {UpdateValidate.class, InsertValidate.class})
  @Length(max = 1, message = "状态{fixedLength}", groups = {UpdateValidate.class, InsertValidate.class})
  private String status;

  /**
   * 流水号分类（厅流水、处室流水）
   */
  @NotEmpty(message = "流水号分类{notEmpty}", groups = {UpdateValidate.class, InsertValidate.class})
  @Length(max = 16, message = "流水号分类{fixedLength}", groups = {UpdateValidate.class, InsertValidate.class})
  private String sequenceCate;



  /**
   * 阅办单ID集合
   */
  private List<String> dealFormId;

  /**
   * 运转单ID集合
   */
  private List<String> transportId;

  /**
   * 错情办理单ID集合
   */
  private List<String> errorId;

  private Integer sortNo;

  /**
   * 统一部署所属系统编码
   */
  private String systemNo;

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSequenceName() {
    return this.sequenceName;
  }

  public void setSequenceName(String sequenceName) {
    this.sequenceName = sequenceName;
  }

  public Integer getSequenceLength() {
    return this.sequenceLength;
  }

  public void setSequenceLength(Integer sequenceLength) {
    this.sequenceLength = sequenceLength;
  }

  public Integer getInitValue() {
    return this.initValue;
  }

  public void setInitValue(Integer initValue) {
    this.initValue = initValue;
  }

  public String getSequenceMode() {
    return this.sequenceMode;
  }

  public void setSequenceMode(String sequenceMode) {
    this.sequenceMode = sequenceMode;
  }

  public String getDocCate() {
    return this.docCate;
  }

  public void setDocCate(String docCate) {
    this.docCate = docCate;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSequenceCate() {
    return this.sequenceCate;
  }

  public void setSequenceCate(String sequenceCate) {
    this.sequenceCate = sequenceCate;
  }


  public List<String> getDealFormId() {
    return this.dealFormId;
  }

  public void setDealFormId(List<String> dealFormId) {
    this.dealFormId = dealFormId;
  }

  public List<String> getTransportId() {
    return this.transportId;
  }

  public void setTransportId(List<String> transportId) {
    this.transportId = transportId;
  }

  public List<String> getErrorId() {
    return this.errorId;
  }

  public void setErrorId(List<String> errorId) {
    this.errorId = errorId;
  }

  public Integer getSortNo() {
    return this.sortNo;
  }

  public void setSortNo(Integer sortNo) {
    this.sortNo = sortNo;
  }

  public String getSystemNo() {
    return this.systemNo;
  }

  public void setSystemNo(String systemNo) {
    this.systemNo = systemNo;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("ProposalSequence{");
    sb.append("id='").append(id).append('\'');
    sb.append(", sequenceName='").append(sequenceName).append('\'');
    sb.append(", sequenceLength=").append(sequenceLength);
    sb.append(", initValue=").append(initValue);
    sb.append(", sequenceMode='").append(sequenceMode).append('\'');
    sb.append(", docCate='").append(docCate).append('\'');
    sb.append(", status='").append(status).append('\'');
    sb.append(", sequenceCate='").append(sequenceCate).append('\'');
    sb.append(", dealFormId=").append(dealFormId);
    sb.append(", transportId=").append(transportId);
    sb.append(", errorId=").append(errorId);
    sb.append(", sortNo=").append(sortNo);
    sb.append(", systemNo='").append(systemNo).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
