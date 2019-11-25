package com.zjhousing.egov.proposal.business.dao.impl;

import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.dao.ReceivalDao;
import com.zjhousing.egov.proposal.business.mapper.ReceivalMapper;
import com.zjhousing.egov.proposal.business.model.Receival;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luzhangfei
 */
@Repository
public class ReceivalDaoImpl implements ReceivalDao {

  @Resource
  private ReceivalMapper receivalMapper;

  @Override
  public List<String> isDuplicateWithModel(Receival receival) {
    return this.receivalMapper.isDuplicateWithModel(receival);
  }

  @Override
  public int insertReceival(Receival receival) {
    return this.receivalMapper.insertReceival(receival);
  }

  @Override
  public int deleteReceival(List<String> list) {
    return this.receivalMapper.deleteReceival(list);
  }

  @Override
  public int updateReceival(Receival receival) {
    return this.receivalMapper.updateReceival(receival);
  }

  @Override
  public int updateReturnFlagByIds(List<String> list) {
    return this.receivalMapper.updateReturnFlagByIds(list);
  }

  @Override
  public int updateFlowStatus(String flowStatus, String id) {
    return this.receivalMapper.updateFlowStatus(flowStatus, id);
  }

  @Override
  public Receival getReceivalById(String id) {
    return this.receivalMapper.getReceivalById(id);
  }

  @Override
  public Receival getReceival4docMark(String docMark) {
    return this.receivalMapper.getReceival4docMark(docMark);
  }

  @Override
  public Page<Receival> getReceival4Page(PagingRequest<Receival> paging, Receival receival, String[] word) {
    return this.receivalMapper.getReceival4Page(paging, receival, word);
  }

  @Override
  public int updateSwapReturnById(String id, String swapReturn) {
    return this.receivalMapper.updateSwapReturnById(id, swapReturn);
  }

  @Override
  public int batchUpdateReceivalRelDocMark(List<Receival> list) {
    return this.receivalMapper.batchUpdateReceivalRelDocMark(list);
  }

  @Override
  public List<Receival> getReceivalByIdList(List<String> list) {
    return this.receivalMapper.getReceivalByIdList(list);
  }
}
