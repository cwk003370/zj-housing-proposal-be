package com.zjhousing.egov.proposal.business.dao;

import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.model.EgovDocUpdateItemLog;

import java.util.List;

/**
 * 公文修改日志
 *
 * @author lindongmei
 * @date 2019/3/22
 */
public interface EgovDocUpdateItemLogDao {
  /**
   * 变更记录分页
   *
   * @param paging
   * @param egovDocUpdateItemLog
   * @param word
   * @return
   */
  Page<EgovDocUpdateItemLog> getEgovDocUpdateItemLog4Page(PagingRequest<EgovDocUpdateItemLog> paging, EgovDocUpdateItemLog egovDocUpdateItemLog, String word);

  /**
   * 新增变更记录
   *
   * @param egovDocUpdateItemLog
   * @return
   */
  int insertEgovDocUpdateItemLog(EgovDocUpdateItemLog egovDocUpdateItemLog);

  /**
   * 更新变更记录
   *
   * @param egovDocUpdateItemLog
   * @return
   */
  int updateEgovDocUpdateItemLog(EgovDocUpdateItemLog egovDocUpdateItemLog);

  /**
   * 删除变更记录
   *
   * @param ids
   * @return
   */
  int deleteEgovDocUpdateItemById(List<String> ids);

  /**
   * 根据id查询
   *
   * @param id
   * @return
   */
  EgovDocUpdateItemLog getEgovDocUpdateItemById(String id);

  /**
   * 根据文档id删除变更记录
   *
   * @param list 文档id
   * @return
   */
  int deleteEgovDocUpdateItemByDocId(List<String> list);

  /**
   * 批量新增
   *
   * @param list
   * @return
   */
  int batchInsertEgovDocUpdateItemLog(List<EgovDocUpdateItemLog> list);
}
