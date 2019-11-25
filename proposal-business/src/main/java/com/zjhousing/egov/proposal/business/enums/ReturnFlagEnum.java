package com.zjhousing.egov.proposal.business.enums;

/**
 * 清退类型 enum
 *
 * @author lindongmei
 * @date 2018/10/25
 */
public enum ReturnFlagEnum {
  /**
   * 不清退
   */
  NOT_RETURN("不清退", true),
  /**
   * 待清退
   */
  TO_DO_RETURN("待清退", false);
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

  ReturnFlagEnum(String label, Boolean defaults) {
    this.label = label;
    this.defaults = defaults;
  }
}
