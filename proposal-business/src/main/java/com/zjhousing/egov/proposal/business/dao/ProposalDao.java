package com.zjhousing.egov.proposal.business.dao;

import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.model.ProposalAssigned;
import org.apache.ibatis.annotations.Param;


import java.util.List;

public interface ProposalDao {
  /**
   * 新增提案议案
   *
   * @param proposal
   * @return
   */
  int insertProposalMotion(Proposal proposal);
  /**
   * 修改
   *
   * @param proposal
   * @return
   */
  int updateProposalMotion(Proposal proposal);

  /**
   * 通过多个id批量删除
   *
   * @param list
   * @return
   */
  int delProposalMotion(List<String> list);
  /**
   * 根据ID查询某条记录
   *
   * @param id
   * @return
   * @throws Exception
   */
  Proposal getProposalMotionById(String id);
  /**
   * 分页-获取发文数据
   *
   * @param paging
   * @param proposal
   * @param word
   * @return
   */
  Page<Proposal> getProposalMotion4Page(PagingRequest<Proposal> paging, Proposal proposal, String[] word);
  /**
   * 根据ID集合获取数据
   *
   * @param list
   * @return
   */
  List<Proposal> getProposalMotionListByIds(List<String> list);
  /**
   * 批量更新发文-关联文件字段值
   *
   * @param list
   * @return
   */
  int batchUpdateProposalRelReceivalMark(List<Proposal> list);
  /**
   * 子流程-新增交办关系
   *
   * @param proposalAssigned
   * @return
   */
  int insertProposalMotionAssigned(ProposalAssigned proposalAssigned);
  /**
   * 子流程-通过子文档ID查询主文档ID
   *
   * @param assistDocId 文档Id
   * @return
   */
  String selectProposalDocIdByAssistDocId(@Param("assistDocId") String assistDocId);

  /**
   * 子流程-通过文档ID查询所有子文档数据
   *
   * @param id 文档Id
   * @return
   */
  List<Proposal> getSubProposalById(@Param("id") String id);


}
