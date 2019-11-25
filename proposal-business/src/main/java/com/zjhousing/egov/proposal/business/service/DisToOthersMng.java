package com.zjhousing.egov.proposal.business.service;

import com.zjhousing.egov.proposal.business.query.DisToOthersQuery;

/**
 * 发文转其他文 mng
 *
 * @author lindongmei
 * @date 2018/10/31
 */
public interface DisToOthersMng {

  /**
   * 发文转其他文
   *
   * @param disToOthersQuery
   * @return
   */
  boolean disToOthers(DisToOthersQuery disToOthersQuery);
}
