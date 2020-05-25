package com.zjhousing.egov.proposal.business.constant;

/**
 * 收文 constant
 *
 * @author chenwenkang
 * @date 2020/04/13
 */
public class ReceivalConstant {
  /**
   * @param receival (map格式)
   *                 id(必填): 收文id
   *                 docMark: 文件字号
   *                 docSequence: 流水号
   *                 docSequenceNum: (默认传-1)
   *                 docSequenceYear: (默认传-1)
   *                 sourceUnit: 来文单位
   *                 draftUser(必填): 登记用户名称
   *                 draftUserNo(必填): 登记用户编码
   *                 draftUserDept(必填): 登记部门名称
   *                 draftUserDeptNo(必填): 登记部门编码
   *                 draftUserUnit: 登记单位名称
   *                 draftUserUnitNo: 登记单位编码
   *                 subject(必填): 文件标题
   *                 secLevel: 文件密级
   *                 draftDate: 收文日期
   *                 docType: 文种
   *                 docCate: 文件类型
   *                 readers: 读者权限(Set<String>)
   *                 mainSend: 主送(Set<String>)
   *                 copySend: 抄送(Set<String>)
   *                 resourceId(必填): 源文id
   *                 flowStatus(默认0)
   *                 returnFlag(默认NOT_RETURN)
   *                 receivalFlowType(收文流程类型 固定值1)
   *                 receivalAssistType(Integer):(3 表示 提案议案收文； 4 表示 发文转收文）
   * @return
   */
  public static final String ID = "id";
  public static final String DOC_MARK = "docMark";
  public static final String DOC_SEQUENCE = "docSequence";
  public static final String DOC_SEQUENCE_NUM = "docSequenceNum";
  public static final String DOC_SEQUENCE_YEAR = "docSequenceYear";
  public static final String SOURCE_UNIT = "sourceUnit";
  public static final String DRAFT_USER = "draftUser";
  public static final String DRAFT_USER_NO = "draftUserNo";
  public static final String DRAFT_USER_DEPT = "draftUserDept";
  public static final String DRAFT_USER_DEPT_NO = "draftUserDeptNo";
  public static final String DRAFT_USER_UNIT = "draftUserUnit";
  public static final String DRAFT_USER_UNIT_NO = "draftUserUnitNo";
  public static final String SUBJECT = "subject";
  public static final String DRAFT_DATE = "draftDate";
  public static final String DOC_TYPE = "docType";
  public static final String DOC_CATE = "docCate";
  public static final String READERS = "readers";
  public static final String MAIN_SEND = "mainSend";
  public static final String COPY_SEND = "copySend";
  public static final String RESOURCE_ID = "resourceId";
  public static final String SYSTEM_NO = "systemNo";
  public static final String FLOW_STATUS = "flowStatus";
  public static final String RETURN_FLAG = "returnFlag";
  public static final String RECEIVAL_FLOW_TYPE = "receivalFlowType";
  public static final String RECEIVAL_ASSIST_TYPE = "receivalAssistType";
}
