package com.zjhousing.egov.proposal.web.controller.external;

import com.rongji.egov.flowrelation.web.controller.FlowRelationFlowTodoController;
import com.rongji.egov.wflow.business.service.engine.transfer.AtdoTransferMng;
import com.rongji.egov.wflow.business.service.engine.transfer.TodoTransferMng;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.service.ProposalFlowOperator;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/proposalmotion")
public class ProposalFlowRelation implements FlowRelationFlowTodoController {
  @Resource
  ProposalMng proposalMng;
  @Resource
  TodoTransferMng todoTransferMng;
  @Resource
  ProposalFlowOperator proposalFlowOperator;
  @Resource
  AtdoTransferMng atdoTransferMng;

  @Override
  public  Boolean submitProcessAutoSend(String aid,String docId,String transitionLabel)throws Exception{
    Proposal proposal =this.proposalMng.getProposalMotionById(docId);
    return this.todoTransferMng.autoSend(aid,transitionLabel,proposal.toMap(),this.proposalFlowOperator);
  }
  @Override
  public  Boolean submitProcessCancel(String aid,String docId)throws Exception{
    Proposal proposal =this.proposalMng.getProposalMotionById(docId);
    return this.atdoTransferMng.submitProcessCancle(aid,proposal.toMap(),this.proposalFlowOperator,null);
  }
  @Override
  public boolean revokeTodos(String aid) throws Exception{
    List<String> aidList = new ArrayList<>();
    aidList.add(aid);
    return this.todoTransferMng.revokeTodos(aidList, this.proposalFlowOperator);
  }
  @Override
  public boolean setProcessRestart(List<String> docList) throws Exception {
    return this.proposalMng.setProcessRestart(docList);
  }


}
