package com.zjhousing.egov.proposal.web.controller.external;

import com.rongji.egov.wflow.business.service.engine.transfer.AtdoTransferMng;
import com.rongji.egov.wflow.business.service.engine.transfer.TodoTransferMng;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.service.ProposalFlowOperator;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提案议案-流水号配置
 *
 * @author chenwenkang
 * @date 2020/04/25
 **/
@RestController
@RequestMapping("/proposalmotion")
public class ProposalFlowRelation {
  @Autowired
  ProposalMng proposalMng;
  @Autowired
  TodoTransferMng todoTransferMng;
  @Autowired
  ProposalFlowOperator proposalFlowOperator;
  @Autowired
  AtdoTransferMng atdoTransferMng;
  @GetMapping({"/wflow/submitProcessAutoSend"})
  public  Boolean submitProcessAutoSend(String aid,String docId,String transitionLabel)throws Exception{
    Proposal proposal =this.proposalMng.getProposalMotionById(docId);
    Boolean flag = this.todoTransferMng.autoSend(aid,transitionLabel,proposal.toMap(),this.proposalFlowOperator);
    return flag;
  }
  @GetMapping({"/wflow/submitProcessCancel"})
  public  Boolean submitProcessCancel(String aid,String docId)throws Exception{
    Proposal proposal =this.proposalMng.getProposalMotionById(docId);
    Boolean flag = this.atdoTransferMng.submitProcessCancle(aid,proposal.toMap(),this.proposalFlowOperator,null);
    return flag;
  }

}
