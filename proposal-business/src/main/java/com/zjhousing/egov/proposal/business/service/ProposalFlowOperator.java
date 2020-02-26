package com.zjhousing.egov.proposal.business.service;

import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.attachutil.model.EgovAtt;
import com.rongji.egov.attachutil.service.EgovAttMng;
import com.rongji.egov.doc.business.constant.ExternalToOthersConstant;
import com.rongji.egov.doc.business.enums.TransferLibraryTypeEnum;
import com.rongji.egov.docconfig.business.annotation.DocReadLogAn;
import com.rongji.egov.flowrelation.business.constant.FlowTypeConstant;
import com.rongji.egov.flowrelation.business.model.FlowRelation;
import com.rongji.egov.flowrelation.business.model.query.FlowRelationQuery;
import com.rongji.egov.flowrelation.business.service.FlowRelationMng;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.business.util.SecurityUtils;
import com.rongji.egov.utils.exception.BusinessException;
import com.rongji.egov.wflow.business.service.ModuleOperator;
import com.rongji.egov.wflow.business.service.engine.manage.ProcessManageMng;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.query.ProToOthersQuery;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ProposalFlowOperator implements ModuleOperator {

  @Resource
  private ProposalMng proposalMng;

  @Resource
  private ProToOthersMng proToOthersMng;

  @Resource
  private ProcessManageMng processManageMng;

  @Resource
  private FlowRelationMng flowRelationMng;

  @Resource
  private EgovAttMng egovAttMng;
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

    this.proToOthers(pro);
    // 归历史公文库（转历史公文库类型：办结转）
    if (TransferLibraryTypeEnum.FILE_DONE_TRANSFER.equals(proposal.getTransferLibraryType())) {
      proposal.setArchiveFlag("1");
    }
    //更新流程关系
    if("1".equals(pro.getSubJudge())){
      updateFlowRelation(docId,"PROPOSALMOTION", FlowTypeConstant.TO_DO);
    }
    this.proposalMng.updateProposalMotion(proposal);


  }

  /**
   * 流程办结自动归档（转公文库类型：办结转）
   *
   * @param proposal
   */
  public void proToOthers(Proposal proposal) {
    if (TransferLibraryTypeEnum.FILE_DONE_TRANSFER.equals(proposal.getTransferLibraryType())) {
      ProToOthersQuery proToOthersQuery = new ProToOthersQuery();
      proToOthersQuery.setType(ExternalToOthersConstant.TO_ARCHIVE);
      proToOthersQuery.setDocId(proposal.getId());
      proToOthersQuery.setPublicFlag("PUBLIC");
      Set<String> readers = new HashSet<>();
      proToOthersQuery.setReaders(readers);
      this.proToOthersMng.proToOthers(proToOthersQuery);
    }
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

    if (StringUtils.isNotBlank((String) returnInfo.get("isDone")) && returnInfo.get("isDone").equals("1")) {
      SecurityUser securityUser = SecurityUtils.getPrincipal();
      proposal.setFlowStatus("9");
      proposal.setFlowDoneUser(securityUser.getUserNo());

      // 归历史公文库（转历史公文库类型：办结转）
      this.proToOthers(proposal);
      if (TransferLibraryTypeEnum.FILE_DONE_TRANSFER.equals(proposal.getTransferLibraryType())) {
        proposal.setArchiveFlag("1");
      }
    }

    this.proposalMng.updateProposalMotion(proposal);
  }
  /**
   * 更新流程关系
   *
   * @param docId
   * @param moduleId
   * @param
   */
  public int updateFlowRelation(String docId,String moduleId,String flowType){
    try {
      //添加反馈文件
      List<String> typeList = new ArrayList<>();
      typeList.add("feedback_file");
      List<EgovAtt> egovAttList =this.egovAttMng.getEgovAttsByDocIdAndType(docId,typeList,false);
      if(egovAttList!=null && !egovAttList.isEmpty()){
        FlowRelation model = new FlowRelation();
        model.setSonModuleNo(egovAttList.get(0).getModuleId());
        model.setSonDocId(egovAttList.get(0).getDocId());
        model.setFeedbackFile(egovAttList.get(0).getId());
        model.setFlowType(FlowTypeConstant.TO_DO);
        this.flowRelationMng.addFlowRelationToFeedback(model);
      }
      // 更改流程关系子文档办理时间
      FlowRelation flowRelation = new FlowRelation();
      flowRelation.setSonDocId(docId);
      flowRelation.setSonModuleNo(moduleId);
      flowRelation.setFlowType(flowType);
      flowRelation.setSonEndTime(new Timestamp(System.currentTimeMillis()));
      return this.flowRelationMng.updateFlowRelationByDocId(flowRelation);
    } catch (Exception e) {
      throw new BusinessException(e.getMessage());
    }

  }
}
