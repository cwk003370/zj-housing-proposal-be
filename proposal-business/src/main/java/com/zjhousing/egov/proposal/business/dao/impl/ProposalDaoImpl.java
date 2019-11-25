package com.zjhousing.egov.proposal.business.dao.impl;

import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.dao.ProposalDao;
import com.zjhousing.egov.proposal.business.mapper.ProposalMapper;
import com.zjhousing.egov.proposal.business.model.Proposal;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class ProposalDaoImpl implements ProposalDao {
  @Resource
  private ProposalMapper mapper;


  @Override
  public int insertProposalMotion(Proposal proposal) {
    return mapper.insertProposalMotion(proposal);
  }

  @Override
  public int updateProposalMotion(Proposal proposal) {
    return this.mapper.updateProposalMotion(proposal);
  }

  @Override
  public int delProposalMotion(List<String> list) {
    return this.mapper.delProposalMotion(list);
  }

  @Override
  public Proposal getProposalMotionById(String id) {
    return this.mapper.getProposalMotionById(id);
  }

  @Override
  public Page<Proposal> getProposalMotion4Page(PagingRequest<Proposal> paging, Proposal proposal, String[] word) {
    return this.mapper.getProposalMotion4Page(paging, proposal, word);
  }

  @Override
  public List<Proposal> getProposalMotionListByIds(List<String> list) {
    return this.mapper.getProposalMotionListByIds(list);
  }

  @Override
  public int batchUpdateProposalRelReceivalMark(List<Proposal> list) {
    return this.mapper.batchUpdateProposalRelReceivalMark(list);
  }

}
