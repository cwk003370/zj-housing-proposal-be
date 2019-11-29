package com.zjhousing.egov.proposal.business.service;

import com.rongji.egov.flowutil.business.model.EgovTemplateFile;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.model.ProposalSequence;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @author luzhangfei
 */
public interface ProSequenceMng {
  /**
   * 添加流水信息
   * 判断办阅件标识是否正确
   *
   * @param sequence
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
   */
  int updateSequence(ProposalSequence sequence);


  /**
   * 根据ID得到流水信息
   *
   * @param id
   * @return
   */
  ProposalSequence getSequenceById(String id);

  /**
   * 根据文件类别及办阅件标识得到配置的流水号
   * 根据流水号得到配置的模板信息
   * 根据模板信息得到模板文件
   *
   * @param docCate
   * @param type         得到的模板类别
   * @return
   */
  List<EgovTemplateFile> getTemplateFileByDocCate(String docCate, String type, String os, String systemNo);

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
                                     @Param("sequence") ProposalSequence sequence,
                                     @Param("word") String word);

  /**
   * 根据“文件类型（办阅件）、是否处室流水”生成流水号
   *
   * @param docCate      文件类别编码
   * @param systemNo 系统编码
   * @return
   */
  HashMap<String, Object> getSequenceNum(String docCate, String systemNo);

}
