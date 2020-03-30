package com.zjhousing.egov.proposal.business.service;

import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.rongji.egov.wflow.business.model.dto.transfer.SubmitParam;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.query.ProposalAssistQuery;
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
   * 修改提案
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
   * 修改提案交办单意见
   *
   * @param docId 文档ID
   * @param bureauOpinions 局领导意见
   * @param officeOpinions 办公室领导意见
   * @param subAssignmentRequirements 交办要求
   * @param subLeaderOpinions 领导意见
   * @return true 更新成功，false更新失败
   */
   boolean updateProposalOption(String docId,String bureauOpinions,String officeOpinions,String subAssignmentRequirements ,String subLeaderOpinions);


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
   * 子流程-批量新增子流程文档/补充协办子流程文档
   *
   * @param mainDocId 主流程文档ID
   * @param aid 主流程环节ID
   * @param deptNos 补交协办部门ID
   * @param methodType  0-流程交办 1-追加协办
   * @return
   */
  boolean insertSubProposalMotions(String mainDocId,String aid,List<String> deptNos,String methodType);


  /**
   * 子流程-将阅办单保存为附件（隐藏）
   *
   * @param proposalAssistQuery
   * @return
   */
  boolean insertSubDealForm(ProposalAssistQuery proposalAssistQuery);

  /**
   * 子流程-判断是否可以继续流转
   * @param docId 主流程文档ID
   * @return true 表示子流程都办结 or 作废 or 暂停 ，否则抛出异常
   * @throws Exception
   */
  boolean getFlowStatus(String docId) throws Exception;

  /**
   * 子流程-正常流转人员提交
   * @param submitParam
   * @throws Exception
   */
  Boolean submitProcessUsers(SubmitParam submitParam) throws Exception;

  /**
   * 子流程-同步子流程局交办单意见
   * @param  sonDocId
   * @return true 表示更新成功，false表示未更新
   * @throws Exception
   */
  Boolean updateSubOption(String sonDocId) throws Exception;



}
