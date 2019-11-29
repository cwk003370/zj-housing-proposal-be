package com.zjhousing.egov.proposal.business.mapper;

import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.model.ProposalSequence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chenwenkang
 */
@Mapper
public interface ProSequenceMapper {
  /**
   * 添加流水信息
   * @param sequence
   */
  int insertSequence(ProposalSequence sequence);

  /**
   * 根据ID列表删除流水信息
   * @param list
   * @return
   */
  int deleteSequence(List<String> list);

  /**
   * 更新流水信息
   * @param sequence
   */
  int updateSequence(ProposalSequence sequence);

  /**
   * 根据ID得到流水信息
   * @param id
   * @return
   */
  ProposalSequence getSequenceById(@Param("id") String id);

  /**
   * 根据条件得到流水信息
   *
   * @param sequence
   * @return
   */
  List<ProposalSequence> getSequenceList(ProposalSequence sequence);

  /**
   * 根据文件类型及办阅件得到相应流水
   * @param docCate
   * @param systemNo
   * @return
   */
  ProposalSequence getSequenceByDocCateAndDealReadCate(@Param("docCate") String docCate, @Param("systemNo") String systemNo);

  /**
   * 根据条件得到分页流水信息
   * @param paging
   * @param sequence
   * @param word
   * @return
   */
  Page<ProposalSequence> getSequence4Page(PagingRequest<ProposalSequence> paging,
                                     @Param("sequence") ProposalSequence sequence,
                                     @Param("word") String word);

  /**
   * 根据条件得到是否存在重复
   *
   * @param docCate 文件类别
   * @param id      唯一id
   * @return
   */
  int isDuplicateSequenceByType(@Param("docCate") String docCate, @Param("systemNo") String systemNo, @Param("id") String id);
}
