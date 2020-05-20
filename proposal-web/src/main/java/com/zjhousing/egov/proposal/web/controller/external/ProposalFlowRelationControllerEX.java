package com.zjhousing.egov.proposal.web.controller.external;

import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.flowrelation.web.controller.FlowRelationFlowTodoControllerEX;
import com.rongji.egov.wflow.business.service.engine.transfer.TodoTransferMng;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.service.ProposalFlowOperator;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/proposalmotion")
public class ProposalFlowRelationControllerEX implements FlowRelationFlowTodoControllerEX {
  @Resource
  ProposalMng proposalMng;
  @Resource
  TodoTransferMng todoTransferMng;
  @Resource
  ProposalFlowOperator proposalFlowOperator;
  @Override
  public Boolean submitProcessWithoutUsers(String aid, String docId) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    return this.todoTransferMng.submitProcessWithoutUsers(aid, proposal.toMap(), this.proposalFlowOperator);
  }
}
