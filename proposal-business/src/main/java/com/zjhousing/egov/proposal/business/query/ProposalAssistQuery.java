package com.zjhousing.egov.proposal.business.query;

import com.rongji.egov.doc.business.external.query.DealForm;

import java.util.List;
import java.util.Set;

/**
 * 子流程主文档创建  query
 *
 * @author chenwenkang
 * @date 2019/12/17
 **/
public class ProposalAssistQuery {

  /**
   * 主流程-文档id
   */
  private String docId;
  /**
   * 子流程-拟稿用户ID
   */
  private String userNo;
  /**
   * 子流程-拟稿人姓名
   */
  private String userName;
  /**
   * 子流程-拟稿部门ID
   */
  private String userOrgNo;
  /**
   * 子流程-文件类型
   */
  private String docCate;
  /**
   * 子流程-主办/协办
   */
  private String handleType;
  /**
   * 阅办单
   */
  private List<DealForm> dealForm;

  public String getDocId() {
    return docId;
  }

  public void setDocId(String docId) {
    this.docId = docId;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserOrgNo() {
    return userOrgNo;
  }

  public void setUserOrgNo(String userOrgNo) {
    this.userOrgNo = userOrgNo;
  }

  public String getDocCate() {
    return docCate;
  }

  public void setDocCate(String docCate) {
    this.docCate = docCate;
  }

  public String getHandleType() {
    return handleType;
  }

  public void setHandleType(String handleType) {
    this.handleType = handleType;
  }

  public List<DealForm> getDealForm() {
    return dealForm;
  }

  public void setDealForm(List<DealForm> dealForm) {
    this.dealForm = dealForm;
  }
}
