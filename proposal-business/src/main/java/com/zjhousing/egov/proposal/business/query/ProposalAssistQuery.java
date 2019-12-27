package com.zjhousing.egov.proposal.business.query;

import com.rongji.egov.doc.business.external.query.DealForm;

import java.util.List;

/**
 * 子流程主文档创建  query
 *
 * @author chenwenkang
 * @date 2019/12/17
 **/
public class ProposalAssistQuery {

  /**
   * 主流程-文档id(批量使用)
   */
  private String docId;
  /**
   * 阅办单（批量使用）
   */
  private List<DealForm> dealForm;

  public String getDocId() {
    return docId;
  }

  public void setDocId(String docId) {
    this.docId = docId;
  }

  public List<DealForm> getDealForm() {
    return dealForm;
  }

  public void setDealForm(List<DealForm> dealForm) {
    this.dealForm = dealForm;
  }
}
