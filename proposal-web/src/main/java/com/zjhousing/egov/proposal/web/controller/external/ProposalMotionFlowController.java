package com.zjhousing.egov.proposal.web.controller.external;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.business.util.SecurityUtils;
import com.rongji.egov.utils.exception.BusinessException;
import com.rongji.egov.wflow.business.constant.ModuleFiledConst;
import com.rongji.egov.wflow.business.model.dto.transfer.SubmitParam;
import com.rongji.egov.wflow.business.service.engine.manage.ProcessManageMng;
import com.rongji.egov.wflow.business.service.engine.transfer.AtdoTransferMng;
import com.rongji.egov.wflow.business.service.engine.transfer.DoneTransferMng;
import com.rongji.egov.wflow.business.service.engine.transfer.TodoTransferMng;
import com.rongji.egov.wflow.web.flow.FLowRecoverController;
import com.rongji.egov.wflow.web.flow.FlowRevokeController;
import com.rongji.egov.wflow.web.flow.FlowTransferController;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.service.ProSequenceMng;
import com.zjhousing.egov.proposal.business.service.ProposalFlowOperator;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/proposalmotion")
public class ProposalMotionFlowController implements FlowTransferController, FlowRevokeController, FLowRecoverController {
  @Resource
  private TodoTransferMng todoTransferMng;
  @Resource
  private AtdoTransferMng atdoTransferMng;
  @Resource
  private DoneTransferMng doneTransferMng;
  @Resource
  private ProcessManageMng processManageMng;
  @Resource
  private ProposalMng proposalMng;
  @Resource
  private ProposalFlowOperator proposalFlowOperator;
  @Autowired
  private ProSequenceMng proSequenceMng;



  @Override
  public String initProcess(String label, String version, String docId) {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    if (StringUtils.isNotBlank(proposal.getFlowPid())) {
      throw new BusinessException("该文件已启用流程");
    } else {
      //初始化编号
      SecurityUser user = SecurityUtils.getPrincipal();
      String systemNo = user.getSystemNo();
      HashMap<String, Object> map = this.proSequenceMng.getSequenceNum(proposal.getDocCate(), systemNo);
      proposal.setRelReceivalMark(map.get("docSequence").toString().replaceFirst("^0*", ""));
      proposalMng.updateProposalMotion(proposal);
      HashMap<String, Object> proposalMap = proposal.toMap();
      proposalMap.put(ModuleFiledConst.SEQUENCE_MAP, map);
      proposalMap.put(ModuleFiledConst.DOC_SEQUENCE, map.get("docSequence"));

      try {
        String aid = this.todoTransferMng.initProcess(label, version, this.proposalFlowOperator, proposalMap);
        return aid;
      } catch (Exception var10) {
        throw new BusinessException(var10.getMessage());
      }
    }
  }
  @Override
  public boolean setProcessDone(@RequestBody List<String> finishInfo) {
    return  this.processManageMng.setProcessDone(finishInfo, this.proposalFlowOperator);
  }

  @Override
  public boolean revokeTodos(String aid) {
    List<String> aidList = new ArrayList<>();
    aidList.add(aid);
    return this.todoTransferMng.revokeTodos(aidList, this.proposalFlowOperator);
  }

  @Override
  public String getProcessPermission(String aid, String docId) {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    String result = "";
    try {
      result = this.todoTransferMng.getProcessPermission(aid, proposal.toMap()).toJSONString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  @Override
  public JSONObject getProcessTransitions(String aid, String docId) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    String result = this.todoTransferMng.getProcessTransitions(aid, proposal.toMap()).toJSONString();
    return (JSONObject) JSON.parse(result);
  }

  @Override
  public JSONObject getProcessUsers(String aid, String docId, String tranLabel) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    return this.todoTransferMng.getProcessUsers(aid, proposal.toMap(), tranLabel);

  }

  @Override
  public Boolean submitProcessUsers(@RequestBody SubmitParam submitParam) throws Exception {
    return this.proposalMng.submitProcessUsers(submitParam);
  }

  @Override
  public Boolean submitProcessWithoutUsers(@RequestBody SubmitParam submitParam) throws Exception {
    return this.proposalMng.submitProcessWithoutUsers(submitParam);
  }
  @Override
  public JSONObject getProcessTransUsers(String aid, String docId) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    return this.todoTransferMng.getProcessTransUsers(aid, proposal.toMap());
  }

  @Override
  public Boolean submitProcessTransUsers(@RequestBody SubmitParam submitParam) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(submitParam.getDocId());
    return this.todoTransferMng.submitProcessTransUsers(submitParam.getAid(), proposal.toMap(), this.proposalFlowOperator, submitParam.getNextStates(), submitParam.getMsgType());
  }

  @Override
  public JSONObject getProcessReturnUsers(String aid, String docId) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    return this.todoTransferMng.getProcessReturnUsers(aid, proposal.toMap());
  }

  @Override
  public Boolean submitProcessReturnUsers(@RequestBody SubmitParam submitParam) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(submitParam.getDocId());
    return this.todoTransferMng.submitProcessReturnUsers(submitParam.getAid(), proposal.toMap(), this.proposalFlowOperator, submitParam.getNextStates(), submitParam.getMsgType());
  }

  ///////在办
  @Override
  public Boolean submitProcessCancle(@RequestBody SubmitParam submitParam) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(submitParam.getDocId());
    return this.atdoTransferMng.submitProcessCancle(submitParam.getAid(), proposal.toMap(), this.proposalFlowOperator, submitParam.getMsgType());
  }

  @Override
  public JSONObject getProcessResendTransitions(String aid, String docId) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    return this.atdoTransferMng.getProcessResendTransitions(aid, proposal.toMap());
  }

  @Override
  public JSONObject getProcessResendUsers(String aid, String docId, String tranLabel) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    return this.atdoTransferMng.getProcessResendUsers(aid, proposal.toMap(), tranLabel);
  }

  @Override
  public Boolean submitProcessResendUsers(@RequestBody SubmitParam submitParam) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(submitParam.getDocId());
    return this.atdoTransferMng.submitProcessResendUsers(submitParam.getAid(), proposal.toMap(), this.proposalFlowOperator, submitParam.getNextStates(), submitParam.getMsgType());
  }

  @Override
  public JSONObject getProcessTakebackUsers(String aid) throws Exception {
    return this.atdoTransferMng.getProcessTakebackUsers(aid);
  }

  @Override
  public Boolean submitProcessTakebackUsers(@RequestBody SubmitParam submitParam) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(submitParam.getDocId());
    return this.atdoTransferMng.submitProcessTakebackUsers(submitParam.getAid(), proposal.toMap(), this.proposalFlowOperator, submitParam.getNextStates(), submitParam.getMsgType());
  }

  @Override
  public JSONObject getProcessAllTakebackUsers(String aid) throws Exception {
    return this.atdoTransferMng.getProcessAllTakebackUsers(aid);
  }

  @Override
  public Boolean submitProcessAllTakebackUsers(@RequestBody SubmitParam submitParam) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(submitParam.getDocId());
    return this.atdoTransferMng.submitProcessAllTakebackUsers(submitParam.getAid(), proposal.toMap(), this.proposalFlowOperator, submitParam.getNextStates(), submitParam.getMsgType());
  }

  @Override
  public JSONObject getProcessForceUsers(String aid) throws Exception {
    return this.atdoTransferMng.getProcessForceUsers(aid);
  }

  @Override
  public Boolean submitProcessForceUsers(@RequestBody SubmitParam submitParam) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(submitParam.getDocId());
    return this.atdoTransferMng.submitProcessForceUsers(submitParam.getAid(), proposal.toMap(), this.proposalFlowOperator, submitParam.getNextStates(), submitParam.getMsgType());
  }

  @Override
  public JSONObject getProcessSpecialSendStates(String aid) throws Exception {
    return this.atdoTransferMng.getProcessSpecialSendStates(aid);
  }

  @Override
  public Boolean submitProcessSpecialSendUsers(@RequestBody SubmitParam submitParam) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(submitParam.getDocId());
    return this.atdoTransferMng.submitProcessSpecialSendUsers(submitParam.getAid(), proposal.toMap(), this.proposalFlowOperator, submitParam.getNextStates(), submitParam.getMsgType());
  }

  @Override
  public Boolean submitProcessSpecialTransUsers(@RequestBody SubmitParam submitParam) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(submitParam.getDocId());
    return this.atdoTransferMng.submitProcessSpecialTransUsers(submitParam.getAid(), proposal.toMap(), this.proposalFlowOperator, submitParam.getNextStates(), submitParam.getMsgType());
  }

  @Override
  public JSONObject getProcessCancleFinishedUsers(String docId) throws Exception {
    return this.doneTransferMng.getProcessCancleFinishedUsers(docId);
  }

  @Override
  public Boolean submitProcessCancleFinishedUsers(@RequestBody SubmitParam submitParam) throws Exception {
    return this.proposalMng.submitProcessCancelFinishedUsers(submitParam);
  }

  @Override
  public JSONObject getProcessTransitionsAndUsers(String aid, String docId) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    String result = this.todoTransferMng.getProcessTransitionsAndUsers(aid, proposal.toMap()).toJSONString();
    return (JSONObject) JSON.parse(result);
  }

  @Override
  public JSONObject getProcessResendTransitionsAndUsers(String aid, String docId) throws Exception {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    return this.atdoTransferMng.getProcessResendTransitionsAndUsers(aid, proposal.toMap());
  }

  @Override
  public boolean revokeTodosByPid(@RequestBody List<String> pidList) {
    return this.todoTransferMng.revokeTodosByPid(pidList, this.proposalFlowOperator);
  }

  @Override
  public boolean setProcessRecover(@RequestBody List<String> pidList) {
    return this.todoTransferMng.setProcessRecover(pidList,this.proposalFlowOperator);
  }

  @Override
  public boolean setProcessDestory(@RequestBody List<String> docList) {
    List<String> listPid = new ArrayList<>();
    for(String docId: docList){
      Proposal proposal = this.proposalMng.getProposalMotionById(docId);
      listPid.add(proposal.getFlowPid());
    }
    this.proposalMng.delProposalMotion(docList);
    return this.todoTransferMng.destoryProcess(listPid);
  }
}
