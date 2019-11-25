package com.zjhousing.egov.proposal.business.service;

import com.rongji.egov.utils.api.paging.Page;
import com.zjhousing.egov.proposal.business.model.DeptReceival;
import com.zjhousing.egov.proposal.business.query.CommonToOthersQuery;

/**
 * @author lindongmei
 * @date 2018/11/14
 */
public interface ComToOthersMng {


  /**
   * 转部门阅办签收记录
   *
   * @param docId 文件id
   * @param word
   * @param offset
   * @param limit
   * @return
   */
  Page<DeptReceival> getDeptReceival4Page(String docId, String word, int offset, int limit);

  /**
   * 保存办理单信息
   * @param ctq
   * @return
   */
  boolean insertDealFormToAtt(CommonToOthersQuery ctq);
}
