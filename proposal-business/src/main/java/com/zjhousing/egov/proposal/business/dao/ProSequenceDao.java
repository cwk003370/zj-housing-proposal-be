package com.zjhousing.egov.proposal.business.dao;

import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.model.ProposalSequence;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流水号配置Dao
 *
 * @author chenwenkang
 */
public interface ProSequenceDao {

  /**
   * 添加流水配置项
   *
   * @param sequence
   * @return
   */
  int insertSequence(ProposalSequence sequence);

  /**
   * 根据ID列表删除流水信息
   *
   * @param list
   * @return
   */
  int deleteSequence(List<String> list);

  /**
   * 更新流水信息
   *
   * @param sequence
   * @return
   */
  int updateSequence(ProposalSequence sequence);


  /**
   * 根据ID得到流水信息
   *
   * @param id
   * @return
   */
  ProposalSequence getSequenceById(@Param("id") String id);

  /**
   * 根据文件类型及办阅件得到相应流水
   *
   * @param docCate
   * @param systemNo
   * @return
   */
  ProposalSequence getSequenceByDocCateAndDealReadCate(String docCate,  String systemNo);

  /**
   * 根据条件得到流水信息
   *
   * @param sequence
   * @return
   */
  List<ProposalSequence> getSequenceList(ProposalSequence sequence);

  /**
   * 根据条件得到分页流水信息
   *
   * @param paging
   * @param sequence
   * @param word
   * @return
   */
  Page<ProposalSequence> getSequence4Page(PagingRequest<ProposalSequence> paging,
                                     ProposalSequence sequence,
                                     String word);

  /**
   * 判断条件下是否存在办件、阅件
   *
   * @param docCate
   * @param id
   * @return
   */
  boolean isDuplicateSequenceByType(String docCate, String systemNo, String id);
}
