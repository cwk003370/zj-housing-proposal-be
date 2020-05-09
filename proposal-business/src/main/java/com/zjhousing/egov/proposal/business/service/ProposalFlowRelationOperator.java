package com.zjhousing.egov.proposal.business.service;

import com.rongji.egov.flowrelation.business.service.FlowRelationModuleMng;
import com.zjhousing.egov.proposal.business.model.Proposal;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class ProposalFlowRelationOperator implements FlowRelationModuleMng {
  @Resource
  private ProposalMng proposalMng;
  @Override
  public boolean addNewFlowRelations(String parentDocId, String parentModuleNo,String aid,List<String> deptNos,List<String> optDeptNos) throws Exception {
    Proposal proposal = new Proposal();
    proposal.setId(parentDocId);
    proposal.setUndertakeDepartment(optDeptNos);
    this.proposalMng.updateProposalMotion(proposal);
    return this.proposalMng.insertSubProposalMotions(parentDocId,aid,deptNos,"0");
  }

  @Override
  public boolean addOtherFlowRelations(String parentDocId, String parentModuleNo,String aid,List<String> deptNos,List<String> optDeptNos) throws Exception {
    Proposal proposal = new Proposal();
    proposal.setId(parentDocId);
    proposal.setUndertakeDepartment(optDeptNos);
    this.proposalMng.updateProposalMotion(proposal);
    return this.proposalMng.insertSubProposalMotions(parentDocId,aid,deptNos,"1");
  }

  @Override
  public boolean DeleteDoc(List<String> docIdList) throws Exception {
    this.proposalMng.delProposalMotion(docIdList);
    return true;
  }
}
