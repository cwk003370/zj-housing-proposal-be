package com.zjhousing.egov.proposal.business.constant;


/**
 * 发文转其他文 constant
 *
 * @author lindongmei
 */
public class ExternalToOthersConstant {

  public static final int READ_TIMEOUT = 5000;
  public static final int CONNECT_TIMEOUT = 5000;

  public static final String TO_ARCHIVE = "归历史公文库";

  public static final String TO_DEPARTMENT = "转部门阅办";
  public static final String CANCLE_TO_DEPARTMENT = "取消转部门阅办";

  public static final String TO_OFFICE = "发部局";

  public static final String TO_PUBLIC = "转依申请公开库";

  public static final String TO_FILE = "归档案";
  public static final String TO_FILE_METHOD_NAME = "sendData";
  public static final String TABLE_NAME = "FLOW_WORKITEM_INSTANCE";

  public static final String TO_TASK_LIBRARY = "转任务库";
  public static final String TO_WORK_REMINDER = "转工作提醒";
  public static final String TO_VITAL_DOCUMENT = "转重要文件";
}
