package com.zjhousing.egov.proposal.business.mapper;

import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.model.Receival;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author luzhangfei
 */
@Mapper
public interface ReceivalMapper {

  /**
   * 验证字段值是否已存在
   *
   * @param receival 收文实体
   * @return
   */
  List<String> isDuplicateWithModel(Receival receival);

  /**
   * 登记收文
   * @param receival
   * @return
   */
  int insertReceival(Receival receival);

  /**
   * 删除收文拟稿
   * @param list
   * @return
   */
  int deleteReceival(List<String> list);

  /**
   * 更新收文
   * @param receival
   * @return
   */
  int updateReceival(Receival receival);

  /**
   * 更新收文状态
   * @param flowStatus
   * @param id
   * @return
   */
  int updateFlowStatus(@Param("flowStatus") String flowStatus, @Param("id") String id);

  /**
   * 清退收文拟稿
   * @param list
   * @return
   */
  int updateReturnFlagByIds(List<String> list);
  /**
   * 根据文档ID得到收文
   * @param id
   * @return
   */
  Receival getReceivalById(@Param("id") String id);

  /**
   * 根据文件字号得到收文
   * @param docMark
   * @return
   */
  Receival getReceival4docMark(@Param("docMark") String docMark);

  /**
   * 得到收文分页信息
   * @param paging
   * @param receival
   * @param word
   * @return
   */
  Page<Receival> getReceival4Page(PagingRequest<Receival> paging,
                                  @Param("receival") Receival receival,
                                  @Param("word") String[] word);

  /**
   * 更新退文标志
   *
   * @param id
   * @param swapReturn
   * @return
   */
  int updateSwapReturnById(@Param("id") String id, @Param("swapReturn") String swapReturn);

  /**
   * 批量更新关联文件字段值
   *
   * @param list
   * @return
   */
  int batchUpdateReceivalRelDocMark(@Param("list") List<Receival> list);

  /**
   * 批量查询
   *
   * @param list
   * @return
   */
  List<Receival> getReceivalByIdList(@Param("list") List<String> list);
}
