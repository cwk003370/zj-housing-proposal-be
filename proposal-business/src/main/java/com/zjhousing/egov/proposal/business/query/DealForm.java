package com.zjhousing.egov.proposal.business.query;

/**
 * 阅办单附件：转部门收文、归档案系统使用
 *
 * @author lindongmei
 * @date 2018/11/22
 */
public class DealForm {
  /**
   * 阅办单html
   */
  private String file;
  /**
   * 文件名称（文件标题+'-阅办单'）
   */
  private String fileName;
  /**
   * 文件后缀名称
   */
  private String fileSuffix;

  public DealForm() {
  }

  public DealForm(String file, String fileName) {
    this.file = file;
    this.fileName = fileName;
  }

  public String getFile() {
    return this.file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileSuffix() {
    return this.fileSuffix;
  }

  public void setFileSuffix(String fileSuffix) {
    this.fileSuffix = fileSuffix;
  }
}
