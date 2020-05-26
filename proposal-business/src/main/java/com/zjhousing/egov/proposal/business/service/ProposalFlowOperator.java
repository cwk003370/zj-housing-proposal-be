package com.zjhousing.egov.proposal.business.service;

import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.docconfig.business.annotation.DocReadLogAn;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.business.util.SecurityUtils;
import com.rongji.egov.wflow.business.service.ModuleOperator;
import com.rongji.egov.wflow.business.service.engine.manage.ProcessManageMng;
import com.zjhousing.egov.proposal.business.model.Proposal;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ProposalFlowOperator implements ModuleOperator {

  @Resource
  private ProposalMng proposalMng;

  @Resource
  private ProcessManageMng processManageMng;

  /**
   * 初始化时更新主表单
   * 1.变更flowStauts
   * 2.是否添加到Solr
   *
   * @param docId
   * @param readers
   */
  @Override
  public void UpdateDocWhenInit(String docId, HashMap<String, Object> sequenceMap, String label, String version, String pid, String readers) {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    proposal.setFlowPid(pid);
    proposal.setFlowLabel(label);
    proposal.setFlowVersion(version);
    //初始化流水号
//    proposal.setDocSequence(sequenceMap.get("docSequence").toString());
//    proposal.setDocSequenceYear(Integer.parseInt(sequenceMap.get("docSequenceYear").toString()));

    //将流程状态设置为启用
    proposal.setFlowStatus("1");
    // 添加查阅用户
    HashSet<String> readersSet = (HashSet<String>) proposal.getReaders();

    String read = readers.split("/")[0];
    readersSet.add(read);
    SecurityUser user = SecurityUtils.getPrincipal();
    readersSet.add(user.getUserNo());
    proposal.setReaders(readersSet);
    //TODO 阅办单初始化

    this.proposalMng.updateProposalMotion(proposal);
  }

  /**
   * 作废时删除主表单
   * 1.删除主表单
   *
   * @param docId
   */
  @Override
  @DocReadLogAn(moduleId = "PROPOSALMOTION", operator = "delete")
  public void DeleteDocWhenDestory(String docId) {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    // 变更状态
    if("8".equals(proposal.getFlowStatus())){
      proposal.setFlowStatus("1");

    }else{
      proposal.setFlowStatus("8");
    }
    this.processManageMng.updateTodo(proposal.toMap());
    proposalMng.updateProposalMotion(proposal);
  }

  /**
   * 办结时更新主表单
   *
   * @param docId
   * @param pDoneUser 办结人编码
   */
  @Override
  public void UpdateDocWhenDone(String docId, String pDoneUser) {
    Proposal proposal = new Proposal();
    proposal.setFlowDoneUser(pDoneUser);
    proposal.setId(docId);
    proposal.setFlowStatus("9");
    Proposal pro = this.proposalMng.getProposalMotionById(docId);
    // 添加查阅用户
    Set<String> readersSet = pro.getReaders();
    SecurityUser user = SecurityUtils.getPrincipal();
    readersSet.add(user.getUserNo());
    proposal.setReaders(readersSet);
    this.proposalMng.updateProposalMotion(proposal);
  }


  /**
   * 取消办结时更新主表单
   *
   * @param docId
   */
  @Override
  public void UpdateDocWhenDoneCancle(String docId) {

    Proposal proposal = new Proposal();
    proposal.setId(docId);
    proposal.setFlowStatus("1");
    proposal.setFlowDoneUser("");
    this.proposalMng.updateProposalMotion(proposal);
  }

  /**
   * 更新主表单状态及查阅人
   *
   * @param docId
   * @param returnInfo doUser、readUser、readOrg、readGroup、businessAction、isDone
   */
  @Override
  public void UpdateDocByFlowStatusAndReader(String docId, JSONObject returnInfo) {

    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    // 添加查阅用户
    HashSet<String> readersSet = (HashSet<String>) proposal.getReaders();

    if (returnInfo.containsKey("readUser")) {
      String[] readUsers = ((String) returnInfo.get("readUser")).split(";");
      for (String read : readUsers) {
        if (StringUtils.isNotBlank(read)) {
          readersSet.add(read.split("/")[0]);
        }
      }
    }
    if (returnInfo.containsKey("doUser")) {
      String[] doUser = ((String) returnInfo.get("doUser")).split(";");
      for (String read : doUser) {
        if (StringUtils.isNotBlank(read)) {
          readersSet.add(read.split("/")[0]);
        }
      }
    }
    SecurityUser user = SecurityUtils.getPrincipal();
    readersSet.add(user.getUserNo());

    proposal.setReaders(readersSet);

    if (StringUtils.isNotBlank((String) returnInfo.get("isDone")) && "1".equals(returnInfo.get("isDone"))) {
      SecurityUser securityUser = SecurityUtils.getPrincipal();
      proposal.setFlowStatus("9");
      proposal.setFlowDoneUser(securityUser.getUserNo());
    }

    this.proposalMng.updateProposalMotion(proposal);
  }
}
