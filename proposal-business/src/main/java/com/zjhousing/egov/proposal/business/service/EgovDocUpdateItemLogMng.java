package com.zjhousing.egov.proposal.business.service;

import com.rongji.egov.user.business.model.SecurityUser;
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
public interface EgovDocUpdateItemLogMng {
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
   * 新增,必填字段如下：
   * <p>
   *
   * @param docId                文档id
   * @param updateItem           变更项
   * @param oldValue             旧值
   * @param newValue             新增
   * @param moduleNo             模块编码
   *                             </p>
   * @param egovDocUpdateItemLog
   * @param user                 当前用户
   * @return
   */
  boolean insertEgovDocUpdateItemLog(EgovDocUpdateItemLog egovDocUpdateItemLog, SecurityUser user);

  /**
   * 批量新增
   *
   * @param list
   * @param user
   * @return
   */
  boolean batchInsertEgovDocUpdateItemLog(List<EgovDocUpdateItemLog> list, SecurityUser user);

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
   * @param list
   * @return
   */
  int deleteEgovDocUpdateItemById(List<String> list);

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
}

