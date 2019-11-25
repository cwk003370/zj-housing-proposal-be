package com.zjhousing.egov.proposal.business.constant;

/**
 * @author huangzp
 * @create 2018/6/5 16:02
 * @desc 公文日志使用的常量
 **/
public class DocLogConstant {
  private DocLogConstant() {
    throw new IllegalStateException("DocLogConstant class");
  }

  /**
   * 添加
   */
  public static final String LOG_ADD = "01";
  /**
   * 更新
   */
  public static final String LOG_UPDATE = "02";
  /**
   * 删除
   */
  public static final String LOG_DEL = "03";
  /**
   * 作废
   */
  public static final String LOG_REVOKE = "04";
  /**
   * 发文
   */
  public static final String DISPATCH_MODULE_NO = "DISPATCH";
  /**
   * 收文
   */
  public static final String RECEIVAL_MODULE_NO = "RECEIVAL";
}
