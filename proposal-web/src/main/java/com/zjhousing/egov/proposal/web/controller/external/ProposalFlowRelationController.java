package com.zjhousing.egov.proposal.web.controller.external;

import com.rongji.egov.flowrelation.web.controller.FlowRelationFlowTodoController;
import com.rongji.egov.utils.exception.BusinessException;
import com.rongji.egov.wflow.business.service.engine.transfer.AtdoTransferMng;
import com.rongji.egov.wflow.business.service.engine.transfer.TodoTransferMng;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.service.ProposalFlowOperator;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/proposalmotion")
public class ProposalFlowRelationController implements FlowRelationFlowTodoController {
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
  public boolean delDoc(@RequestBody Map<String, Object> map) throws Exception {
    List<String> docIdList = (List<String>) map.get("docIdList");
    if(docIdList!=null&& !docIdList.isEmpty()){
      int res =this.proposalMng.delProposalMotion(docIdList);
      if(res>0){
        return true;
      }
    }
    return false;
  }

  @Override
  public Boolean submitProcessWithoutUsers(String aid, String docId) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    return this.todoTransferMng.submitProcessWithoutUsers(aid, proposal.toMap(), this.proposalFlowOperator);
  }


  @Override
  public boolean setProcessRestart(@RequestParam(name = "docId") String docId) throws Exception {
    return this.proposalMng.setProcessRestart(docId);
  }


}
