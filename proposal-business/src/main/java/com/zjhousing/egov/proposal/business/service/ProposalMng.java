package com.zjhousing.egov.proposal.business.service;

import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.model.Proposal;

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
}
