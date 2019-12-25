package com.zjhousing.egov.proposal.business.service;

import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.attachutil.model.EgovAtt;
import com.rongji.egov.doc.business.external.query.DealForm;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.query.ProposalAssistQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.solr.common.SolrDocument;

import java.util.List;
/**
 * 提案议案
 *
 * @author chenwenkang
 * @date 2019/11/15
 **/
public interface ProposalMng {
  /**
   * 新增提案议案
   *
   * @param proposal
   * @return
   */
  int insertProposalMotion(Proposal proposal);
  /**
   * 修改发文
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
   * 根据ID查询提案详细信息
   *
   * @param docId
   * @param aid
   * @return
   */
  JSONObject getProposalMotionDetailById(String docId, String aid);
  /**
   * 分页-获取提案议案数据
   *
   * @param paging
   * @param proposal
   * @param word
   * @return
   */
  Page<Proposal> getProposalMotion4Page(PagingRequest<Proposal> paging, Proposal proposal, String[] word);


  /**
   * 清号
   *
   * @param docWord
   * @param docMarkNum
   * @param docMarkYear
   */
  void cleanUpNum(String docWord, Integer docMarkNum, Integer docMarkYear, String systemNo);

  /**
   * 批量更新提案-关联文件字段值
   *
   * @param list
   * @return
   */
  int batchUpdateProposalRelReceivalMark(List<Proposal> list);

  /**
   * 根据条件 从Solr中查询数据
   *
   * @param paging
   * @param proposal
   * @param draftYear
   * @param draftMonth
   * @param draftDay
   * @param word
   * @return
   */
  Page<SolrDocument> getProposalMotionBySolr(PagingRequest paging, Proposal proposal,
                                       Integer draftYear, Integer draftMonth, Integer draftDay, String word);
  /**
   * 子流程-批量新增子流程文档
   *
   * @param mainDocId 主流程文档ID
   * @param aid 主流程环节ID
   * @return
   */
  boolean insertSubProposalMotions(String mainDocId,String aid) ;

  /**
   * 子流程-将阅办单保存为附件（隐藏）
   *
   * @param proposalAssistQuery
   * @return
   */
  boolean insertSubDealForm(ProposalAssistQuery proposalAssistQuery);
  /**
   * 子流程-新增子流程文档(废弃)
   *
   * @param proposal
   * @return
   */
  int insertSubProposalMotion(Proposal proposal, String userNo,String userOrgNo,String docCate,String userName,String handleType, List<DealForm> dealForm);
  /**
   * 反馈文件记录新增
   * @param egovAtt
   * @return
   * @throws Exception
   */
  int addFlowRelationToFeedback(EgovAtt egovAtt) throws Exception;

  /**
   * 子流程-文件反馈
   *
   * @param assistDocId 子文档ID
   * @return
   */
  boolean setProcessRestart(@Param("assistDocId")String assistDocId);
}
