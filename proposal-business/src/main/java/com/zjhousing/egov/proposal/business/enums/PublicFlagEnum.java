package com.zjhousing.egov.proposal.business.enums;

/**
 * 公开属性 enum
 *
 * @author lindongmei
 * @date 2018/10/25
 */
public enum PublicFlagEnum {
  /**
   * 不予公开
   */
  NOT_PUBLIC("不予公开", true),
  /**
   * 主动公开
   */
  PUBLIC("主动公开", false),
  /**
   * 依申请公开
   */
  APPLY_PUBLIC("依申请公开", false);
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

  PublicFlagEnum(String label, Boolean defaults) {
    this.label = label;
    this.defaults = defaults;
  }
}
