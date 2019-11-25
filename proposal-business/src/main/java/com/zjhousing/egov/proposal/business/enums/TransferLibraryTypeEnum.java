package com.zjhousing.egov.proposal.business.enums;

/**
 * 转历史文库类型 enum
 *
 * @author lindongmei
 * @date 2018/10/25
 */
public enum TransferLibraryTypeEnum {
  /**
   * 办结转
   */
  FILE_DONE_TRANSFER("办结转", true),
  /**
   * 手动转
   */
  MANUAL_TRANSFER("手动转", false);
  /**
   * 指定权限转
   */
//    PERMISSION_TRANSFER("指定权限转", false);
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

  TransferLibraryTypeEnum(String label, Boolean defaults) {
    this.label = label;
    this.defaults = defaults;
  }
}
