package com.zjhousing.egov.proposal.business;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 公文请求其他模块地址配置
 *
 * @author lindongmei
 * @date 2018/12/6
 */
@Component
@ConfigurationProperties(prefix = "proposal.business")
  public class ProposalBusinessProperties {
  /**
   * 收发文转其他文请求ip
   */
  private String requestPrefix;
  /**
   * 归档案wsdl地址
   */
  private String toFileWsdl;
  /**
   * 公文库管理员
   */
  private String archiveAdmin;
  /**
   * 向指定栏目发布文章的wsdl地址
   */
  private String publishArticlesWsdl;
  // 归档案  全宗号
  private String writSect;

  public String getWritSect() {
    return writSect;
  }

  public void setWritSect(String writSect) {
    this.writSect = writSect;
  }

  public String getPublishArticlesWsdl() {
    return publishArticlesWsdl;
  }

  public void setPublishArticlesWsdl(String publishArticlesWsdl) {
    this.publishArticlesWsdl = publishArticlesWsdl;
  }

  public String getArchiveAdmin() {
    return this.archiveAdmin;
  }

  public void setArchiveAdmin(String archiveAdmin) {
    this.archiveAdmin = archiveAdmin;
  }

  public String getRequestPrefix() {
    return this.requestPrefix;
  }

  public void setRequestPrefix(String requestPrefix) {
    this.requestPrefix = requestPrefix;
  }

  public String getToFileWsdl() {
    return this.toFileWsdl;
  }

  public void setToFileWsdl(String toFileWsdl) {
    this.toFileWsdl = toFileWsdl;
  }
}
