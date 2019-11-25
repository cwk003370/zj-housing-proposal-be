package com.zjhousing.egov.proposal.business.mapper;

import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.model.EgovDocUpdateItemLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公文修改日志
 *
 * @author lindongmei
 * @date 2019/3/22
 */
@Mapper
public interface EgovDocUpdateItemLogMapper {
  /**
   * 变更记录分页
   *
   * @param paging
   * @param egovDocUpdateItemLog
   * @param word
   * @return
   */
  Page<EgovDocUpdateItemLog> getEgovDocUpdateItemLog4Page(@Param("paging") PagingRequest<EgovDocUpdateItemLog> paging, @Param("model") EgovDocUpdateItemLog egovDocUpdateItemLog, @Param("word") String word);

  /**
   * 新增变更记录
   *
   * @param egovDocUpdateItemLog
   * @return
   */
  int insertEgovDocUpdateItemLog(@Param("model") EgovDocUpdateItemLog egovDocUpdateItemLog);

  /**
   * 更新变更记录
   *
   * @param egovDocUpdateItemLog
   * @return
   */
  int updateEgovDocUpdateItemLog(@Param("model") EgovDocUpdateItemLog egovDocUpdateItemLog);

  /**
   * 删除变更记录
   *
   * @param ids
   * @return
   */
  int deleteEgovDocUpdateItemById(@Param("list") List<String> ids);

  /**
   * 根据id查询
   *
   * @param id
   * @return
   */
  EgovDocUpdateItemLog getEgovDocUpdateItemById(@Param("id") String id);

  /**
   * 根据文档id删除变更记录
   *
   * @param list 文档id
   * @return
   */
  int deleteEgovDocUpdateItemByDocId(@Param("list") List<String> list);

  /**
   * 批量新增
   *
   * @param list
   * @return
   */
  int batchInsertEgovDocUpdateItemLog(@Param("list") List<EgovDocUpdateItemLog> list);
}
