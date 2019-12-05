package com.zjhousing.egov.proposal.business.dao.impl;



import com.rongji.egov.solrData.business.dao.BaseSolrDeal;
import com.rongji.egov.solrData.business.dao.SolrDataDao;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.zjhousing.egov.proposal.business.dao.ProposalDao;
import com.zjhousing.egov.proposal.business.model.Proposal;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ZhengGuoJun
 * @date 2018/7/9
 */
@Repository("PROPOSALMOTION")
public class ProposalMotionSolrDealImpl extends BaseSolrDeal<Proposal> {
  @Resource
  private ProposalDao proposalDao;
  @Resource
  private SolrDataDao solrDataDao;
  @Override
  public boolean productionAddList(List<Proposal> list) throws Exception {
    List<Map<String, Object>> solrList = new ArrayList<>();
    for(Proposal pro : list){
      Map<String,Object> map = pro.toSolrMap();
      solrList.add(map);
    }
    return solrDataDao.addList(solrList);
  }

  @Override
  public Page<Proposal> productionQuery(int offset, int limit, boolean isAll, Date startDate, Date endDate, String otherParam) throws Exception {
    PagingRequest<Proposal> paging = new PagingRequest<Proposal>();
    paging.setLimit(limit);
    paging.setOffset(offset);
    Proposal pro = new Proposal();
    if(!isAll){
      pro.setFront_RangeStartDate(startDate);
      pro.setFront_RangeEndDate(endDate);
    }

    return proposalDao.getProposalMotion4Page(paging,pro,null);
  }
}
