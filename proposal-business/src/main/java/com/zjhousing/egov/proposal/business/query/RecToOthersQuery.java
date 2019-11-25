package com.zjhousing.egov.proposal.business.query;

import java.util.List;
import java.util.Set;

/**
 * 收文转其他文 query
 *
 * @author lindongmei
 * @date 2018/11/9
 */
public class RecToOthersQuery {

  /**
   * 操作类型
   */
  private String type;
  /**
   * 文档id
   */
  private String docId;
  /**
   * 转部门阅办：部门编码
   */
  private String deptNo;
  /**
   * 公开属性
   */
  private String publicFlag;
  /**
   * 可读人员
   */
  private Set<String> readers;
  /**
   * 阅办单
   */
  private List<DealForm> dealForm;
  private List<String> idList;

  public List<String> getIdList() {
    return idList;
  }

  public void setIdList(List<String> idList) {
    this.idList = idList;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDocId() {
    return this.docId;
  }

  public void setDocId(String docId) {
    this.docId = docId;
  }

  public String getDeptNo() {
    return this.deptNo;
  }

  public void setDeptNo(String deptNo) {
    this.deptNo = deptNo;
  }

  public String getPublicFlag() {
    return this.publicFlag;
  }

  public void setPublicFlag(String publicFlag) {
    this.publicFlag = publicFlag;
  }

  public Set<String> getReaders() {
    return this.readers;
  }

  public void setReaders(Set<String> readers) {
    this.readers = readers;
  }

  public List<DealForm> getDealForm() {
    return this.dealForm;
  }

  public void setDealForm(List<DealForm> dealForm) {
    this.dealForm = dealForm;
  }
}
