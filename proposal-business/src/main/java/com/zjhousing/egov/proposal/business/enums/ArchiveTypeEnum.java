package com.zjhousing.egov.proposal.business.enums;

/**
 * 归档类型 enum
 *
 * @author lindongmei
 * @date 2018/10/25
 */
public enum ArchiveTypeEnum {
  /**
   * 不归档
   */
  NOT_ARCHIVE("不归档", true),
  /**
   * 待归档
   */
  TO_DO_ARCHIVE("待归档", false),
  /**
   * 直接归档
   */
  DIRECT_ARCHIVE("直接归档", false);
  /**
   * 标签，前台显示值
   */
  private final String label;
  /**
   * 前台是否显示默认值
   */
  private final Boolean defaults;

  public String getLabel() {
    return this.label;
  }

  public Boolean getDefaults() {
    return this.defaults;
  }

  ArchiveTypeEnum(String label, Boolean defaults) {
    this.label = label;
    this.defaults = defaults;
  }
}
