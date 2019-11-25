package com.zjhousing.egov.proposal.business.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 公文修改日志
 *
 * @author lindongmei
 * @date 2019/3/22
 */
public class EgovDocUpdateItemLog {
  /**
   * id
   */
  private String id;
  /**
   * 文档ID
   */
  private String docId;
  /**
   * 变更时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  /**
   * 执行用户编号
   */
  private String operUserNo;
  /**
   * 执行用户名称
   */
  private String operUserName;
  /**
   * 变更项
   */
  private String updateItem;
  /**
   * 旧值
   */
  private String oldValue;
  /**
   * 新值
   */
  private String newValue;
  /**
   * 开始时间
   */
  private String beginTime;
  /**
   * 结束时间
   */
  private String endTime;
  /**
   * 统一部署所属系统编码
   */
  private String systemNo;
  /**
   * 模块编码
   */
  private String moduleNo;

  public EgovDocUpdateItemLog() {
  }

  public EgovDocUpdateItemLog(String docId, String updateItem, String oldValue, String newValue, String moduleNo) {
    this.docId = docId;
    this.updateItem = updateItem;
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.moduleNo = moduleNo;
  }

  public String getModuleNo() {
    return moduleNo;
  }

  public void setModuleNo(String moduleNo) {
    this.moduleNo = moduleNo;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDocId() {
    return docId;
  }

  public void setDocId(String docId) {
    this.docId = docId;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public String getOperUserNo() {
    return operUserNo;
  }

  public void setOperUserNo(String operUserNo) {
    this.operUserNo = operUserNo;
  }

  public String getOperUserName() {
    return operUserName;
  }

  public void setOperUserName(String operUserName) {
    this.operUserName = operUserName;
  }

  public String getUpdateItem() {
    return updateItem;
  }

  public void setUpdateItem(String updateItem) {
    this.updateItem = updateItem;
  }

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }

  public String getBeginTime() {
    return beginTime;
  }

  public void setBeginTime(String beginTime) {
    this.beginTime = beginTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String getSystemNo() {
    return systemNo;
  }

  public void setSystemNo(String systemNo) {
    this.systemNo = systemNo;
  }

  @Override
  public String toString() {
    return "EgovDocUpdateItemLog{" +
      "id='" + id + '\'' +
      ", docId='" + docId + '\'' +
      ", updateTime=" + updateTime +
      ", operUserNo='" + operUserNo + '\'' +
      ", operUserName='" + operUserName + '\'' +
      ", updateItem='" + updateItem + '\'' +
      ", oldValue='" + oldValue + '\'' +
      ", newValue='" + newValue + '\'' +
      ", beginTime='" + beginTime + '\'' +
      ", endTime='" + endTime + '\'' +
      ", systemNo='" + systemNo + '\'' +
      ", moduleNo='" + moduleNo + '\'' +
      '}';
  }
}
