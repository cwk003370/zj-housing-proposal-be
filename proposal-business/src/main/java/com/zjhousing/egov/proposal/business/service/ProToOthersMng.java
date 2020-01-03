package com.zjhousing.egov.proposal.business.service;

import com.zjhousing.egov.proposal.business.query.ProToOthersQuery;

/**
 * 提案转其他文 mng
 *
 * @author lindongmei
 * @date 2018/10/31
 */
public interface ProToOthersMng {

  /**
   * 提案转其他文<br/>
   * <p>type: 操作类型 必填</p>
   * <p>      取值："归历史公文库";"转部门阅办";"转依申请公开库";</p>
   * <p>docId: 文档id 必填</p>
   *
   * @param proToOthersQuery
   * @return
   */
  boolean proToOthers(ProToOthersQuery proToOthersQuery);
}
