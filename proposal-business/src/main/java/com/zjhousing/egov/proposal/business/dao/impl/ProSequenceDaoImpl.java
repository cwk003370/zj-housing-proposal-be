package com.zjhousing.egov.proposal.business.dao.impl;

import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.dao.ProSequenceDao;
import com.zjhousing.egov.proposal.business.mapper.ProSequenceMapper;
import com.zjhousing.egov.proposal.business.model.ProposalSequence;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chenwenkang
 */
@Repository
public class ProSequenceDaoImpl implements ProSequenceDao {

  @Resource
  private ProSequenceMapper sequenceMapper;

  @Override
  public int insertSequence(ProposalSequence sequence) {
    return this.sequenceMapper.insertSequence(sequence);
  }

  @Override
  public int deleteSequence(List<String> list) {
    return this.sequenceMapper.deleteSequence(list);
  }

  @Override
  public int updateSequence(ProposalSequence sequence) {
    return this.sequenceMapper.updateSequence(sequence);
  }

  @Override
  public ProposalSequence getSequenceById(String id) {
    return this.sequenceMapper.getSequenceById(id);
  }

  @Override
  public ProposalSequence getSequenceByDocCateAndDealReadCate(String docCate, String systemNo) {
    return this.sequenceMapper.getSequenceByDocCateAndDealReadCate(docCate, systemNo);
  }

  @Override
  public List<ProposalSequence> getSequenceList(ProposalSequence sequence) {
    return this.sequenceMapper.getSequenceList(sequence);
  }

  @Override
  public Page<ProposalSequence> getSequence4Page(PagingRequest<ProposalSequence> paging, ProposalSequence sequence, String word) {
    return this.sequenceMapper.getSequence4Page(paging, sequence, word);
  }

  @Override
  public boolean isDuplicateSequenceByType(String docCate, String systemNo, String id) {
    return this.sequenceMapper.isDuplicateSequenceByType(docCate, systemNo, id) > 0;
  }

}
