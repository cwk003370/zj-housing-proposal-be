package com.zjhousing.egov.proposal.business.mapper;

import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.model.Proposal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProposalMapper {

  /**
   * 新增提案议案
   * @param proposal
   * @return
   */
  int insertProposalMotion(Proposal proposal);

  /**
   * 修改
   * @param proposal
   * @return
   */
  int updateProposalMotion(Proposal proposal);
  /**
   * 通过多个id批量删除
   * @param list
   * @return
   */
  int delProposalMotion(List<String> list) ;

  /**
   * 根据ID查询某条记录
   * @param id
   * @return
   * @throws Exception
   */
  Proposal getProposalMotionById(@Param("id")String id);
  /**
   * 分页-获取发文数据
   * @param paging
   * @param proposal
   * @param word
   * @return
   */
  Page<Proposal> getProposalMotion4Page(PagingRequest<Proposal>paging,
                                        @Param("proposal")Proposal proposal, @Param("word") String[] word );
  /**
   * 根据ID集合获取数据
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
  int batchUpdateProposalRelReceivalMark(@Param("list") List<Proposal> list);
}
