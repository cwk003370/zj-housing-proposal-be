package com.zjhousing.egov.proposal.business.query;

import com.rongji.egov.user.business.model.SecurityUser;

/**
 * @author lindongmei
 * @date 2019/4/17
 */
public class CommonToOthersQuery {
  /**
   * 文档id
   */
  private String docId;
  /**
   * 标题
   */
  private String title;
  /**
   * 模块编码
   */
  private String moduleNo;
  /**
   * 流程id
   */
  private String flowPid;
  /**
   * 当前用户
   */
  private SecurityUser user;
  /**
   * 阅办单
   */
  private DealForm dealForm;

  public CommonToOthersQuery() {
  }

  public CommonToOthersQuery(String docId, String moduleNo, SecurityUser user, DealForm dealForm) {
    this.docId = docId;
    this.moduleNo = moduleNo;
    this.user = user;
    this.dealForm = dealForm;
  }

  public CommonToOthersQuery(String docId, String title, String moduleNo, String flowPid, SecurityUser user) {
    this.docId = docId;
    this.title = title;
    this.moduleNo = moduleNo;
    this.flowPid = flowPid;
    this.user = user;
  }

  public DealForm getDealForm() {
    return dealForm;
  }

  public void setDealForm(DealForm dealForm) {
    this.dealForm = dealForm;
  }

  public String getFlowPid() {
    return flowPid;
  }

  public void setFlowPid(String flowPid) {
    this.flowPid = flowPid;
  }

  public SecurityUser getUser() {
    return user;
  }

  public void setUser(SecurityUser user) {
    this.user = user;
  }

  public String getDocId() {
    return docId;
  }

  public void setDocId(String docId) {
    this.docId = docId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getModuleNo() {
    return moduleNo;
  }

  public void setModuleNo(String moduleNo) {
    this.moduleNo = moduleNo;
  }
}
