package com.zjhousing.egov.proposal.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.SolrCloudClient.util.ATOMIC;
import com.rongji.egov.attachutil.model.EgovAtt;
import com.rongji.egov.attachutil.service.EgovAttMng;
import com.rongji.egov.doc.business.constant.DocLogConstant;
import com.rongji.egov.doc.business.external.model.EgovDocUpdateItemLog;
import com.rongji.egov.doc.business.external.service.EgovDocUpdateItemLogMng;
import com.rongji.egov.doc.business.receival.model.Receival;
import com.rongji.egov.doc.business.receival.service.ReceivalMng;
import com.rongji.egov.docconfig.business.service.DocOperatorLogMng;
import com.rongji.egov.flowutil.business.service.DocResourceMng;
import com.rongji.egov.solrData.business.dao.SolrDataDao;
import com.rongji.egov.user.business.dao.UmsUserOrgRelateDao;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.business.model.UmsUserOrgRelate;
import com.rongji.egov.user.business.model.vo.DraftUser;
import com.rongji.egov.user.business.util.SecurityUtils;
import com.rongji.egov.user.business.util.UserDraftUtils;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.rongji.egov.utils.common.IdUtil;
import com.rongji.egov.utils.exception.BusinessException;
import com.rongji.egov.wflow.business.service.engine.manage.ProcessManageMng;
import com.rongji.egov.wflow.business.service.engine.transfer.TodoTransferMng;

import com.zjhousing.egov.proposal.business.dao.ProposalDao;

import com.zjhousing.egov.proposal.business.model.Proposal;

import com.zjhousing.egov.proposal.business.model.ProposalAssigned;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLOutput;
import java.util.*;


@Service
public class ProposalMngImpl implements ProposalMng {

  @Resource
  private EgovAttMng egovAttMng;
  @Resource
  private ProposalDao proposalDao;
  @Resource
  private TodoTransferMng todoTransferMng;
  @Resource
  private DocOperatorLogMng operatorLogMng;
  @Resource
  private SolrDataDao solrDataDao;
  @Resource
  private EgovDocUpdateItemLogMng egovDocUpdateItemLogMng;
  @Resource
  private DocResourceMng docResourceMng;
  @Resource
  private ReceivalMng receivalMng;
  @Resource
  private ProcessManageMng processManageMng;
  @Resource
  private UmsUserOrgRelateDao umsUserOrgRelateDao;


  @Override
  public int insertProposalMotion(Proposal proposal) {
    if (StringUtils.isBlank(proposal.getId())) {
      proposal.setId(IdUtil.getUID());
    }
    SecurityUser securityUser = SecurityUtils.getPrincipal();

    // 初始化发文默认数据
    proposal.setDraftDate(new Date());
    proposal.setDraftUserNo(securityUser.getUserNo());
    proposal.setDraftUserName(securityUser.getUserName());
    DraftUser draftUser = UserDraftUtils.getDraftUser(securityUser.getUmsUser());
    proposal.setDraftDeptNo(draftUser.getDraftDeptNo());
    proposal.setDraftDept(draftUser.getDraftDeptName());

    proposal.setDraftUnitNo(securityUser.getUnitNo());
    proposal.setDraftUnit(securityUser.getUnitName());
    proposal.setFlowStatus("0");
    proposal.setSignFlag("0");

    if (StringUtils.isBlank(proposal.getDocMark())) {
      proposal.setDocMark("");
    }

    // 添加查阅用户
    HashSet<String> readers = new HashSet<>();
    readers.add(proposal.getDraftUserNo());
    proposal.setReaders(readers);

    //过滤头尾空格
    if (StringUtils.isNotBlank(proposal.getSubject())) {
      proposal.setSubject(StringUtils.trim(proposal.getSubject()));
    }
    int result = this.proposalDao.insertProposalMotion(proposal);
    if (result < 1) {
      throw new BusinessException("登记提案议案失败");
    }
    //新增记录日志
    this.operatorLogMng.insertOperatorLog("PROPOSALMOTION", "提案议案", proposal.getSubject(), DocLogConstant.LOG_ADD, proposal.getId());

    // 添加数据到solr
    try {
      this.solrDataDao.add(proposal.toSolrMap());
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessException("solr数据添加失败");
    }
    return result;
  }

  @Override
  public int updateProposalMotion(Proposal proposal) {
    Proposal oldProposal = this.proposalDao.getProposalMotionById(proposal.getId());

    //过滤头尾空格
    if (StringUtils.isNotBlank(oldProposal.getSubject())) {
      oldProposal.setSubject(StringUtils.trim(oldProposal.getSubject()));
    }

    if (!"0".equals(proposal.getFlowStatus())) {
      //  操作日志-更新
      this.operatorLogMng.insertOperatorLog("PROPOSALMOTION", "提案议案", oldProposal.getSubject(), DocLogConstant.LOG_UPDATE, oldProposal.getId());
      //添加-重要信息修改（异步）
      this.addProposalUpdateLog(proposal, oldProposal);
    }

    // 添加数据到solr
    try {
      this.solrDataDao.update(ATOMIC.SET, proposal.toSolrMap());
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessException("solr数据添加失败");
    }
    return this.proposalDao.updateProposalMotion(proposal);
  }

  @Override
  public int delProposalMotion(List<String> list) {
    List<Proposal> disList = this.proposalDao.getProposalMotionListByIds(list);
    if (disList == null || disList.size() <= 0) {
      return 0;
    }

    int r = this.proposalDao.delProposalMotion(list);
    List<Receival> receivalList = new ArrayList<>();
    for (Proposal dispatch : disList) {
      String id = dispatch.getId();
      if (StringUtils.isNotBlank(dispatch.getDocMark())) {
        this.cleanUpNum(dispatch.getDocWord(), dispatch.getDocMarkNum(), dispatch.getDocMarkYear(), dispatch.getSystemNo());
        dispatch.setDocMark("");
        dispatch.setDocMarkNum(-1);
        dispatch.setDocMarkYear(-1);
        this.proposalDao.updateProposalMotion(dispatch);
      }
      //记录发文删除操作
      this.operatorLogMng.insertOperatorLog("PROPOSALMOTION", "提案议案", dispatch.getSubject(), DocLogConstant.LOG_DEL, dispatch.getId());

      // 从solr中删除数据
      try {
        this.solrDataDao.delById(id);
      } catch (Exception e) {
        e.printStackTrace();
      }
      //删除附件
      this.egovAttMng.deleteEgovAttByDocId(id);
      //删除发文关联
      this.docResourceMng.deleteDocResourceByDocId(id);
      //删除收文关联
      this.docResourceMng.deleteDocResourceByResourceDocId(id);
      //删除收文-关联文件字段值
      String relRecFile = dispatch.getRelReceivalMark();
      if (StringUtils.isNotBlank(relRecFile)) {
        String[] relRecFileArr = relRecFile.split(";");
        if (relRecFileArr.length > 1) {
          String docId = relRecFileArr[1];
          Receival receival = new Receival(docId, null);
          receivalList.add(receival);
        }
      }
    }
    //更新收文-关联文件字段值
    if (receivalList.size() > 0) {
      this.receivalMng.batchUpdateReceivalRelDocMark(receivalList);
    }
    return r;
  }

  @Override
  public Proposal getProposalMotionById(String id) {
    return this.proposalDao.getProposalMotionById(id);
  }
  @Override
  public JSONObject getProposalMotionDetailById(String docId, String aid) {
    JSONObject result = new JSONObject();
    ObjectMapper mapper = new ObjectMapper();
    mapper.setTimeZone(TimeZone.getDefault());
    Proposal proposal = this.proposalDao.getProposalMotionById(docId);
    if (null == proposal) {
      throw new BusinessException("文件可能被异常删除");
    }
    List<EgovAtt> egovAtts = this.egovAttMng.getEgovAttsByDocId(docId, false);
    JSONObject permission = null;
    if (!StringUtils.equals("0", proposal.getFlowStatus())) {
      try {
        permission = this.todoTransferMng.getProcessPermission(aid, proposal.toMap());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    try {
      result.put("form", JSON.parse(mapper.writeValueAsString(proposal)));
      result.put("atts", JSON.parse(mapper.writeValueAsString(egovAtts)));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    result.put("permission", permission);
      result.put("test", proposal);
    return result;
  }

  @Override
  public Page<Proposal> getProposalMotion4Page(PagingRequest<Proposal> paging, Proposal proposal, String[] word) {
    return this.proposalDao.getProposalMotion4Page(paging, proposal, word);
  }

  @Override
  public void cleanUpNum(String docWord, Integer docMarkNum, Integer docMarkYear, String systemNo) {

  }

  @Override
  public int batchUpdateProposalRelReceivalMark(List<Proposal> list) {
    return this.proposalDao.batchUpdateProposalRelReceivalMark(list);
  }

  @Override
  public int insertSubProposalMotion(Proposal proposal, String userNo, String userOrgNo,String docCate,String userName) {
    List<UmsUserOrgRelate> userList = umsUserOrgRelateDao.listUmsOrgByUserNo(userNo);
    UmsUserOrgRelate umsUserOrgRelate =null;
    for(UmsUserOrgRelate u : userList){
      if(userOrgNo!=null&&userOrgNo.equals(u.getOrgNo())){
        umsUserOrgRelate=u;
      }
    }
    if (umsUserOrgRelate == null) {
      throw new BusinessException("指定人员不存在");
    }

    String mainDocId = proposal.getId();
    //初始化流程信息
    proposal.setId(IdUtil.getUID());
    proposal.setFlowDoneUser(null);
    proposal.setFlowLabel(null);
    proposal.setFlowPid(null);
    proposal.setFlowStatus(null);
    proposal.setFlowVersion(null);
    proposal.setDraftUserNo(umsUserOrgRelate.getUserNo());
    proposal.setDraftUserName(userName);
    proposal.setDraftDeptNo(umsUserOrgRelate.getOrgNo());
    proposal.setDraftDept(umsUserOrgRelate.getOrgName());
    proposal.setDocCate(docCate);
    proposal.setFlowStatus("0");
    proposal.setSignFlag("0");

    if (StringUtils.isBlank(proposal.getDocMark())) {
      proposal.setDocMark("");
    }

    // 添加查阅用户
    HashSet<String> readers = new HashSet<>();
    readers.add(proposal.getDraftUserNo());
    proposal.setReaders(readers);

    //过滤头尾空格
    if (StringUtils.isNotBlank(proposal.getSubject())) {
      proposal.setSubject(StringUtils.trim(proposal.getSubject()));
    }
    int result = this.proposalDao.insertProposalMotion(proposal);
    if (result < 1) {
      throw new BusinessException("登记提案议案失败");
    }
    //添加交办关系
    ProposalAssigned proposalAssigned = new ProposalAssigned();
    proposalAssigned.setId(IdUtil.getUID());
    proposalAssigned.setMainDocId(mainDocId);
    proposalAssigned.setAssistDocId(proposal.getId());
    this.proposalDao.insertProposalMotionAssigned(proposalAssigned);
    //新增记录日志
    this.operatorLogMng.insertOperatorLog("PROPOSALMOTION", "提案议案", proposal.getSubject(), DocLogConstant.LOG_ADD, proposal.getId());

    // 添加数据到solr
    try {
      this.solrDataDao.add(proposal.toSolrMap());
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessException("solr数据添加失败");
    }
    return result;
  }

  @Override
  public boolean setProcessRestart(String assistDocId) {
    String id = proposalDao.selectProposalDocIdByAssistDocId(assistDocId);

    List<Proposal> proposalList = proposalDao.getSubProposalById(id);
    if(proposalList == null || proposalList.size() == 0){
      return false;
    }
    for(Proposal p : proposalList){
      if(!"9".equals(p.getFlowStatus())){
        return false;
      }
    }

    Proposal proposal = proposalDao.getProposalMotionById(id);
    List<String> pidList = new ArrayList<>();
    pidList.add(proposal.getFlowPid());
    System.out.println(pidList.toString());
    return processManageMng.setProcessRestart(pidList);
  }

  /**
   * 添加重要信息修改日志
   *
   * @param newProposal
   * @param oldProposal
   */
  private void addProposalUpdateLog(Proposal newProposal, Proposal oldProposal) {
    if (null != newProposal && null != oldProposal) {
      SecurityUser user = SecurityUtils.getPrincipal();
      String docId = newProposal.getId();
      if (!oldProposal.getId().equals(docId)) {
        throw new BusinessException("此文件不存在");
      }
      List<EgovDocUpdateItemLog> list = new ArrayList<>();
      String oldSubject = oldProposal.getSubject();
      String newSubject = newProposal.getSubject();
      if (!StringUtils.equals(oldSubject, newSubject)) {
        EgovDocUpdateItemLog egovDocUpdateItemLog = new EgovDocUpdateItemLog(docId, "标题", oldSubject, newSubject, "PROPOSALMOTION");
        list.add(egovDocUpdateItemLog);
      }
      String oldSecLevel = oldProposal.getSecLevel();
      String newSecLevel = newProposal.getSecLevel();
      if (!StringUtils.equals(oldSecLevel, newSecLevel)) {
        EgovDocUpdateItemLog egovDocUpdateItemLog = new EgovDocUpdateItemLog(docId, "密级", oldSecLevel, newSecLevel, "PROPOSALMOTION");
        list.add(egovDocUpdateItemLog);
      }
      String oldDocWord = oldProposal.getDocWord();
      String newDocWord = newProposal.getDocWord();
      if (!StringUtils.equals(oldDocWord, newDocWord)) {
        EgovDocUpdateItemLog egovDocUpdateItemLog = new EgovDocUpdateItemLog(docId, "机关代字", oldDocWord, newDocWord, "PROPOSALMOTION");
        list.add(egovDocUpdateItemLog);
      }


      if (list.size() > 0) {
        this.egovDocUpdateItemLogMng.batchInsertEgovDocUpdateItemLog(list, user);
      }
    }
  }
}
