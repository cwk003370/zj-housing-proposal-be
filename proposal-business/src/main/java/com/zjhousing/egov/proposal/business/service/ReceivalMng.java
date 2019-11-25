package com.zjhousing.egov.proposal.business.service;

import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.model.Receival;
import org.apache.solr.common.SolrDocument;

import java.util.List;

/**
 * @author luzhangfei
 */
public interface ReceivalMng {

  /**
   * 登记收文
   *
   * @param receival
   * @return
   */
  int insertReceival(Receival receival);

  /**
   * 删除收文拟稿
   *
   * @param list
   * @return
   */
  int deleteReceival(List<String> list);

  /**
   * 更新收文
   *
   * @param receival
   * @return
   */
  int updateReceival(Receival receival);

  /**
   * 根据ID清退稿件
   *
   * @param list 稿件ID集合
   */
  int updateReturnFlagByIds(List<String> list);

  /**
   * 更新绑定的阅办单(弃用)
   *
   * @param oldArr
   * @param name
   * @param id
   * @param startTime
   * @return
   */
  JSONObject updateDealForm(String oldArr, String name, String id, String startTime);

  /**
   * 根据文档ID得到收文
   * 如果不为拟稿文件、写入查阅记录
   *
   * @param id
   * @return
   */
  Receival getReceivalById(String id);

  /**
   * 根据文档ID及待办实例ID得到收文详细信息
   * - 主表单信息
   * - 附件信息 attach main_doc main_trace main_pdf
   * - 权限信息
   *
   * 如果不为拟稿文件、写入查阅记录
   *
   * @param docId
   * @param aid
   * @return
   */
  JSONObject getReceivalDetailById(String docId, String aid);

  /**
   * 根据文件字号得到收文
   *
   * @param docMark
   * @return
   */
  Receival getReceival4docMark(String docMark);

  /**
   * 得到收文分页信息
   *
   * @param paging
   * @param receival
   * @param word
   * @return
   */
  Page<Receival> getReceival4Page(PagingRequest<Receival> paging,
                                  Receival receival,
                                  String[] word);

  /**
   * 得到收文solr分页信息
   *
   * @param paging
   * @param receival
   * @param word
   * @param draftYear
   * @param draftMonth
   * @param draftDay
   * @return
   */
  Page<SolrDocument> getReceivalBySolr(PagingRequest paging, Receival receival, String word, Integer draftYear, Integer draftMonth, Integer draftDay);

  /**
   * 收文转发文
   *
   * @param id
   * @return
   */
  Proposal transferToProposalMotion(String id);

  /**
   * 更新退文标志
   *
   * @param id
   * @param swapReturn
   * @return
   */
  int updateSwapReturnById(String id, String swapReturn);

  /**
   * 验证字段值是否存在
   *
   * @param receival 收文实体
   * @return 重复记录的id集合
   */
  List<String> isDuplicateWithModel(Receival receival);

  /**
   * 批量更新关联文件字段值
   *
   * @param list
   * @return
   */
  int batchUpdateReceivalRelDocMark(List<Receival> list);

  void cleanUpDocSequence(Receival receival);
  /**
   * 获取收文流水号页面展示数据
   */
  JSONObject getDocSequencePageJson (String docCate, String handleOrView, String systemNo, Integer docSequenceYear, Integer docSequenceNum);
  /**
   * 获取收文流水号页面编号
   */
  void serialDocSequenceNum (String docCate, String handleOrView, String systemNo, String docSequence, Integer docSequenceYear, Integer docSequenceNum, String docId);
}
