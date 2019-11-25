package com.zjhousing.egov.proposal.business.service.impl;

import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.rongji.egov.utils.common.IdUtil;
import com.zjhousing.egov.proposal.business.dao.EgovDocUpdateItemLogDao;
import com.zjhousing.egov.proposal.business.model.EgovDocUpdateItemLog;
import com.zjhousing.egov.proposal.business.service.EgovDocUpdateItemLogMng;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 公文修改日志
 *
 * @author lindongmei
 * @date 2019/3/22
 */
@Service
public class EgovDocUpdateItemLogMngImpl implements EgovDocUpdateItemLogMng {

  @Resource
  private EgovDocUpdateItemLogDao dao;

  @Override
  public Page<EgovDocUpdateItemLog> getEgovDocUpdateItemLog4Page(PagingRequest<EgovDocUpdateItemLog> paging, EgovDocUpdateItemLog egovDocUpdateItemLog, String word) {
    return this.dao.getEgovDocUpdateItemLog4Page(paging, egovDocUpdateItemLog, word);
  }

  @Override
  public boolean insertEgovDocUpdateItemLog(EgovDocUpdateItemLog egovDocUpdateItemLog, SecurityUser user) {
    egovDocUpdateItemLog.setId(IdUtil.getUID());
    egovDocUpdateItemLog.setUpdateTime(new Date());
    //操作用户信息
    egovDocUpdateItemLog.setOperUserName(user.getUserName());
    egovDocUpdateItemLog.setOperUserNo(user.getUserNo());
    egovDocUpdateItemLog.setSystemNo(user.getSystemNo());
    return this.dao.insertEgovDocUpdateItemLog(egovDocUpdateItemLog) > 0;
  }

  @Override
  public boolean batchInsertEgovDocUpdateItemLog(List<EgovDocUpdateItemLog> list, SecurityUser user) {
    //frank 放弃使用batchInsertEgovDocUpdateItemLog的方法新增，这种语法不兼容，就直接每次insert一条记录；
    int result = 0;
    for (EgovDocUpdateItemLog tmp : list) {
      tmp.setId(IdUtil.getUID());
      tmp.setUpdateTime(new Date());
      tmp.setOperUserName(user.getUserName());
      tmp.setOperUserNo(user.getUserNo());
      tmp.setSystemNo(user.getSystemNo());
      int r = dao.insertEgovDocUpdateItemLog(tmp);
      result += r;
    }
    return result == list.size() ? true : false;
  }

  @Override
  public int updateEgovDocUpdateItemLog(EgovDocUpdateItemLog egovDocUpdateItemLog) {
    return this.dao.updateEgovDocUpdateItemLog(egovDocUpdateItemLog);
  }

  @Override
  public int deleteEgovDocUpdateItemById(List<String> list) {
    return this.dao.deleteEgovDocUpdateItemById(list);
  }

  @Override
  public EgovDocUpdateItemLog getEgovDocUpdateItemById(String id) {
    return this.dao.getEgovDocUpdateItemById(id);
  }

  @Override
  public int deleteEgovDocUpdateItemByDocId(List<String> list) {
    return this.dao.deleteEgovDocUpdateItemByDocId(list);
  }
}
