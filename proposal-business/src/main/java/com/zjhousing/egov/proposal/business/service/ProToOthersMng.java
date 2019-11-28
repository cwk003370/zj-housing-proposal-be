package com.zjhousing.egov.proposal.business.service;

import com.zjhousing.egov.proposal.business.query.ProToOthersQuery;

/**
 * 发文转其他文 mng
 *
 * @author lindongmei
 * @date 2018/10/31
 */
public interface ProToOthersMng {

  /**
   * 发文转其他文
   *
   * @param proToOthersQuery
   * @return
   */
  boolean proToOthers(ProToOthersQuery proToOthersQuery);
}
