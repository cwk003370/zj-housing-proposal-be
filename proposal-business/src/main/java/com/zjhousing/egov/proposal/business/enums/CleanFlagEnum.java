package com.zjhousing.egov.proposal.business.enums;

public enum CleanFlagEnum {
  /**
   * 清空
   */
  CLEAN_UP("清空", true),

  /**
   * 不清空
   */
  NOT_CLEAN_UP("不清空", false);

  private final String label;
  private final boolean defaults;

  public String getLabel() {
    return label;
  }

  public boolean isDefaults() {
    return defaults;
  }

  CleanFlagEnum(String label, Boolean defaults) {
    this.label = label;
    this.defaults = defaults;
  }
}
