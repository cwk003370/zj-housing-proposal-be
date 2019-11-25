package com.zjhousing.egov.proposal.business.dao.impl;

import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.dao.EgovDocUpdateItemLogDao;
import com.zjhousing.egov.proposal.business.mapper.EgovDocUpdateItemLogMapper;
import com.zjhousing.egov.proposal.business.model.EgovDocUpdateItemLog;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公文修改日志
 *
 * @author lindongmei
 * @date 2019/3/22
 */
@Repository
public class EgovDocUpdateItemLogDaoImpl implements EgovDocUpdateItemLogDao {

  @Resource
  private EgovDocUpdateItemLogMapper mapper;

  @Override
  public Page<EgovDocUpdateItemLog> getEgovDocUpdateItemLog4Page(PagingRequest<EgovDocUpdateItemLog> paging, EgovDocUpdateItemLog egovDocUpdateItemLog, String word) {
    return this.mapper.getEgovDocUpdateItemLog4Page(paging, egovDocUpdateItemLog, word);
  }

  @Override
  public int insertEgovDocUpdateItemLog(EgovDocUpdateItemLog egovDocUpdateItemLog) {
    return this.mapper.insertEgovDocUpdateItemLog(egovDocUpdateItemLog);
  }

  @Override
  public int updateEgovDocUpdateItemLog(EgovDocUpdateItemLog egovDocUpdateItemLog) {
    return this.mapper.updateEgovDocUpdateItemLog(egovDocUpdateItemLog);
  }

  @Override
  public int deleteEgovDocUpdateItemById(List<String> list) {
    return this.mapper.deleteEgovDocUpdateItemById(list);
  }

  @Override
  public EgovDocUpdateItemLog getEgovDocUpdateItemById(String id) {
    return this.mapper.getEgovDocUpdateItemById(id);
  }

  @Override
  public int deleteEgovDocUpdateItemByDocId(List<String> list) {
    return this.mapper.deleteEgovDocUpdateItemByDocId(list);
  }

  @Override
  public int batchInsertEgovDocUpdateItemLog(List<EgovDocUpdateItemLog> list) {
    return this.mapper.batchInsertEgovDocUpdateItemLog(list);
  }
}
