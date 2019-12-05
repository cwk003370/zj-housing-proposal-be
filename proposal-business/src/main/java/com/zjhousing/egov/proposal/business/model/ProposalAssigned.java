package com.zjhousing.egov.proposal.business.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rongji.egov.utils.spring.validation.InsertValidate;
import com.rongji.egov.utils.spring.validation.UpdateValidate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author chenwenkang
 * @create 2019/11/15
 * @desc 提案议案交办关系
 **/
public class ProposalAssigned {
  /**
   * id
   */
  @NotEmpty(message = "id不能为空", groups = {UpdateValidate.class})
  @Length(min = 16, max = 16, message = "id{fixedLength}", groups = {UpdateValidate.class})
  private String id;
  /**
   * 主流程文档ID-MAIN_DOC_ID
   */
  @NotEmpty(message = "主文档ID不能为空", groups = {InsertValidate.class})
  @Length(max = 256, message = "主文档ID{fixedLength}", groups = {UpdateValidate.class, InsertValidate.class})
  private String mainDocId;
  /**
   * 子流程文档ID-ASSIST_DOC_ID
   */
  @NotEmpty(message = "主文档ID不能为空", groups = {InsertValidate.class})
  @Length(max = 256, message = "主文档ID{fixedLength}", groups = {UpdateValidate.class, InsertValidate.class})
  private String assistDocId;
  /**
   * 主办协办识别码-HANDLE_TYPE
   * 主办：host
   * 协办：assist
   */
  private String handleType;
  /**
   * 交办时间-CREATE_TIME
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp createTime;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMainDocId() {
    return mainDocId;
  }

  public void setMainDocId(String mainDocId) {
    this.mainDocId = mainDocId;
  }

  public String getAssistDocId() {
    return assistDocId;
  }

  public void setAssistDocId(String assistDocId) {
    this.assistDocId = assistDocId;
  }

  public Timestamp getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Timestamp createTime) {
    this.createTime = createTime;
  }

  public String getHandleType() {
    return handleType;
  }

  public void setHandleType(String handleType) {
    this.handleType = handleType;
  }

  public ProposalAssigned() {
  }

  public ProposalAssigned(String id, String mainDocId, String assistDocId, String handleType, Timestamp createTime) {
    this.id = id;
    this.mainDocId = mainDocId;
    this.assistDocId = assistDocId;
    this.handleType = handleType;
    this.createTime = createTime;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("ProposalAssigned{");
    sb.append("id='").append(id).append('\'');
    sb.append(", mainDocId='").append(mainDocId).append('\'');
    sb.append(", assistDocId='").append(assistDocId).append('\'');
    sb.append(", handleType='").append(handleType).append('\'');
    sb.append(", createTime=").append(createTime);
    sb.append('}');
    return sb.toString();
  }
}
