package com.zjhousing.egov.proposal.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.SolrCloudClient.util.ATOMIC;
import com.rongji.egov.attachutil.model.EgovAtt;
import com.rongji.egov.attachutil.service.EgovAttMng;
import com.rongji.egov.doc.business.external.query.DealForm;
import com.rongji.egov.docconfig.business.service.DocOperatorLogMng;
import com.rongji.egov.flowrelation.business.constant.UnitTypeConstant;
import com.rongji.egov.flowrelation.business.constant.FlowTypeConstant;
import com.rongji.egov.flowrelation.business.model.FlowRelation;
import com.rongji.egov.flowrelation.business.model.query.FlowRelationQuery;
import com.rongji.egov.flowrelation.business.service.FlowRelationMng;
import com.rongji.egov.flowutil.business.service.DocOpinionMng;
import com.rongji.egov.flowutil.business.service.DocResourceMng;
import com.rongji.egov.solrData.business.dao.SolrDataDao;
import com.rongji.egov.user.business.dao.UserDao;
import com.rongji.egov.user.business.model.*;
import com.rongji.egov.user.business.model.vo.DraftUser;
import com.rongji.egov.user.business.util.SecurityUtils;
import com.rongji.egov.user.business.util.UserDraftUtils;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.rongji.egov.utils.api.paging.Sort;
import com.rongji.egov.utils.common.IdUtil;
import com.rongji.egov.utils.exception.BusinessException;
import com.rongji.egov.wflow.business.model.dto.transfer.SubmitParam;
import com.rongji.egov.wflow.business.service.engine.transfer.DoneTransferMng;
import com.rongji.egov.wflow.business.service.engine.transfer.TodoTransferMng;
import com.zjhousing.egov.proposal.business.dao.ProposalDao;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.query.ProposalAssistQuery;
import com.zjhousing.egov.proposal.business.service.ProposalFlowOperator;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
  private DocResourceMng docResourceMng;
  @Resource
  private UserDao userDao;
  @Resource
  private FlowRelationMng flowRelationMng;
  @Resource
  private ProposalFlowOperator proposalFlowOperator;
  @Resource
  private DoneTransferMng doneTransferMng;
  @Resource
  private DocOpinionMng docOpinionMng;


  @Override
  public int insertProposalMotion(Proposal proposal) {
    if (StringUtils.isBlank(proposal.getId())) {
      proposal.setId(IdUtil.getUID());
    }
    SecurityUser securityUser = SecurityUtils.getPrincipal();
    // 初始化提案默认数据
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
    List<Proposal> proList = this.proposalDao.getProposalMotionListByIds(list);
    if (proList == null || proList.size() <= 0) {
      return 0;
    }

    int r = this.proposalDao.delProposalMotion(list);
    for (Proposal proposal : proList) {
      String id = proposal.getId();
      if (StringUtils.isNotBlank(proposal.getDocMark())) {
        proposal.setDocMark("");
        this.proposalDao.updateProposalMotion(proposal);
      }

      // 从solr中删除数据
      try {
        this.solrDataDao.delById(id);
      } catch (Exception e) {
        e.printStackTrace();
      }
      //删除附件
      this.egovAttMng.deleteEgovAttByDocId(id);
      //删除提案关联
      this.docResourceMng.deleteDocResourceByDocId(id);
      //删除收文关联
      this.docResourceMng.deleteDocResourceByResourceDocId(id);
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
  public boolean updateProposalOption(String docId, String bureauOpinions, String officeOpinions, String subAssignmentRequirements, String subLeaderOpinions) {
    Proposal proposal =this.proposalDao.getProposalMotionById(docId);
    if(proposal!=null && "1".equals(proposal.getSubJudge())){
      proposal.setSubAssignmentRequirements(subAssignmentRequirements);
      proposal.setSubLeaderOpinions(subLeaderOpinions);
    } else if(proposal!=null&& !"1".equals(proposal.getSubJudge())){
      proposal.setBureauOpinions(bureauOpinions);
      proposal.setOfficeOpinions(officeOpinions);
    }else{
      return false;
    }
    this.proposalDao.updateProposalMotion(proposal);
    return true;
  }


  @Override
  public int batchUpdateProposalRelReceivalMark(List<Proposal> list) {
    return this.proposalDao.batchUpdateProposalRelReceivalMark(list);
  }

  @Override
  public Page<SolrDocument> getProposalMotionBySolr(PagingRequest paging, Proposal proposal, Integer draftYear, Integer draftMonth, Integer draftDay, String showDept, String word) {
    SecurityUser securityUser = SecurityUtils.getPrincipal();

    Page<SolrDocument> page = new Page<>();

    StringBuffer sqStr = new StringBuffer();
    List<RmsRole> roles = userDao.listUserRole(securityUser.getUserNo(), securityUser.getSystemNo());
    Boolean manage = false;
    for (RmsRole role : roles) {
      if ("proposal_manager".equals(role.getRoleNo()) || "sys_manager".equals(role.getRoleNo())) {
        manage = true;
      }
    }
    if (manage) {
      sqStr.append("R_readers:*");
    } else {
      // 默认必须参数
      sqStr.append("(R_readers:" + securityUser.getUserNo());
      List<UmsGroup> groups = this.userDao.listUserGroup(securityUser.getUserNo(), securityUser.getSystemNo());
      for (UmsGroup group : groups) {
        sqStr.append(" OR R_readers:" + group.getGroupNo());
      }
      sqStr.append(" OR R_readers:" + securityUser.getOrgNo() + ")");
    }
    // word关键字模糊查询
    if (StringUtils.isNotBlank(word)) {
      StringBuffer sqStrWord = new StringBuffer();
      String[] strings = word.trim().split("\\s+");
      if (strings != null && strings.length > 0) {
        for (int i = strings.length - 1; i >= 0; i--) {
          sqStrWord.append(" OR S_subject2:*" + strings[i] + "*");
          sqStrWord.append(" OR S_draftUserName:*" + strings[i] + "*");
          sqStrWord.append(" OR S_docMark:*" + strings[i] + "*");
          sqStrWord.append(" OR S_draftDept:*" + strings[i] + "*");
          sqStrWord.append(" OR S_signUserName:*" + strings[i] + "*");
        }
        sqStrWord.replace(0,3," AND (");
        sqStrWord.append(" )");
        sqStr.append(sqStrWord);
      }
    }
    // 提案查询字段
    if(StringUtils.isNotBlank(showDept)){
      sqStr.append("AND S_showDept:" + showDept);
    }
    if (StringUtils.isNotBlank(proposal.getSystemNo())) {
      sqStr.append(" AND S_systemNo:" + proposal.getSystemNo());
    }
    if (StringUtils.isNotBlank(proposal.getSubject())) {
      sqStr.append(" AND S_subject2:*" + proposal.getSubject() + "*");
    }
    if (StringUtils.isNotBlank(proposal.getDocMark())) {
      sqStr.append(" AND S_docMark:*" + proposal.getDocMark() + "*");
    }
    if (StringUtils.isNotBlank(proposal.getDraftDept())) {
      if (StringUtils.equals(proposal.getDraftDept(), "办公室")) {
        sqStr.append(" AND S_draftDept:" + proposal.getDraftDept());
      } else {
        sqStr.append(" AND S_draftDept:*" + proposal.getDraftDept() + "*");
      }
    }
    if (StringUtils.isNotBlank(proposal.getDocSequence())) {
      sqStr.append(" AND S_docSequence:*" + proposal.getDocSequence() + "*");
    }

    if (StringUtils.isNotBlank(proposal.getArchiveFlag())) {
      sqStr.append(" AND S_archiveFlag:*" + proposal.getArchiveFlag() + "*");
    }
    if (null != proposal.getArchiveType()) {
      sqStr.append(" AND S_archiveType:*" + proposal.getArchiveType() + "*");
    }
    if (StringUtils.isNotBlank(proposal.getSignUserName())) {
      sqStr.append(" AND S_signUserName:*" + proposal.getSignUserName() + "*");
    }

    if (StringUtils.isNotBlank(proposal.getSignFlag())) {
      sqStr.append(" AND S_signFlag:" + proposal.getSignFlag());
    }
    if (null != draftYear) {
      sqStr.append(" AND I_draftYear:" + draftYear);
    }
    if (null != draftMonth) {
      sqStr.append(" AND I_draftMonth:" + draftMonth);
    }
    if (null != draftDay) {
      sqStr.append(" AND I_draftDay:" + draftDay);
    }
    if (StringUtils.isNotBlank(proposal.getDraftUserName())) {
      sqStr.append(" AND S_draftUserName:*" + proposal.getDraftUserName() + "*");
    }

    // 流程相关查询字段
    if (StringUtils.isNotBlank(proposal.getFlowStatus())) {
      sqStr.append(" AND S_flowStatus:*" + proposal.getFlowStatus() + "*");
    }
//        sqStr.append(" AND -S_flowStatus:*8*");

    // 协调世界时与东八区时间相差8个小时
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    String beginStr = "*";
    String endStr = "*";

    if (null != proposal.getBeginDate()) {
      beginStr = sdf.format(proposal.getBeginDate());
    }
    if (null != proposal.getEndDate()) {
      endStr = sdf.format(proposal.getEndDate());
    }
    sqStr.append(" AND T_draftTime:[" + beginStr + " TO " + endStr + "]");

    SolrQuery sq = new SolrQuery();
    sq.addFilterQuery("S_module:PROPOSALMOTION");
    sq.setQuery(sqStr.toString());

    if (paging.getSort() != null && "ASC".equals(((Sort.Order) paging.getSort().getOrders().get(0)).getDirection().name())) {
      sq.addSort(((Sort.Order) paging.getSort().getOrders().get(0)).getProperty(), SolrQuery.ORDER.asc);
    } else if (paging.getSort() != null) {
      sq.addSort(((Sort.Order) paging.getSort().getOrders().get(0)).getProperty(), SolrQuery.ORDER.desc);
    }

    QueryResponse queryResult = this.solrDataDao.queryAll(sq, paging.getOffset(), paging.getLimit());
    page.setList(queryResult.getResults());
    page.setTotal(queryResult.getResults().getNumFound());
    return page;
  }


  @Override
  @Transactional
  public boolean insertSubProposalMotions(String mainDocId,String aid,List<String> deptNos,String methodType) throws Exception {

    Proposal proposal = this.proposalDao.getProposalMotionById(mainDocId);
    if(StringUtils.isBlank(mainDocId) ||proposal == null ){
      throw new BusinessException("文档不存在");
    }
    if("1".equals(proposal.getSubJudge())){
      throw new BusinessException("不支持再次交办");
    }
//    //获取阅办单附件ID
//    String dealFormNo = proposal.getDealFormNo();
//    if (dealFormNo == null || "".equals(dealFormNo)) {
//      throw new BusinessException("阅办单附件不存在");
//    }
    SecurityUser securityUser = SecurityUtils.getPrincipal();
    //添加流程关系批次
    String flowVersion = Long.toString(System.currentTimeMillis());
    for (int i= 0; i<deptNos.size(); i++){
      String targetId =IdUtil.getUID();
      // 添加查阅用户 TODO 提案议案受理人
      HashSet<String> readers = new HashSet<>();
      UmsOrg umsOrg = userDao.getUmsOrg(deptNos.get(i));
      UmsUser deptProUser = new UmsUser();
      // 得到提案议案受理，根据群组拿到用户
      UmsGroup umsGroup = userDao.getUmsGroup(deptNos.get(i) + "_" + "PRO", securityUser.getSystemNo());
      UmsGroupCate umsGroupCate = userDao.getUmsGroupCate("PRO", securityUser.getSystemNo());
      if (umsGroup != null && umsGroupCate != null) {
        List<UmsUser> umsUsers = userDao.listGroupUser(umsGroup.getGroupNo(), securityUser.getSystemNo());
        for (UmsUser umsUser : umsUsers) {
          List<UmsUserProxy> umsUserProxies = userDao.listUserProxy(umsUser.getUserNo(), umsUser.getOrgNo());
          long longTime = System.currentTimeMillis();
          for (UmsUserProxy umsUserProxy : umsUserProxies) {
            // 添加等效代理人，存在代理的情况
            if (umsUserProxy.getProxyEndTime().getTime() >= longTime
              && umsUserProxy.getProxyBeginTime().getTime() <= longTime) {
              readers.add(umsUserProxy.getUserNoProxy());
            }
          }
          readers.add(umsUser.getUserNo());
        }
        if (null != umsUsers && umsUsers.size() == 1) {
          deptProUser = umsUsers.get(0);
        } else if (null != umsUsers && umsUsers.size() > 1){
          throw new BusinessException("【" + umsOrg.getOrgName() + "】只能配置一个提案受理人，请及时联系管理员进行配置");
        }
      }

      if (readers.size() == 0) {
        throw new BusinessException("【" + umsOrg.getOrgName() + "】未配置提案受理人，请及时联系管理员进行配置");
      }
      String unitName ="";
      if(umsOrg.getUnitNo()!=null){
        unitName = this.userDao.getUmsOrg(umsOrg.getUnitNo()).getOrgName();
      }
      //初始化流程信息
      proposal.setId(targetId);
      proposal.setFlowDoneUser(null);
      proposal.setFlowLabel(null);
      proposal.setFlowPid(null);
      proposal.setFlowStatus(null);
      proposal.setFlowVersion(null);
      proposal.setFlowStatus("0");
      proposal.setDraftUserNo(deptProUser.getUserNo());
      proposal.setDraftUserName(deptProUser.getUserName());
      proposal.setDraftDeptNo(deptNos.get(i));
      proposal.setDraftDept(umsOrg.getOrgName());
      proposal.setDraftDate(new Timestamp(System.currentTimeMillis()));
      proposal.setDraftUnitNo(umsOrg.getUnitNo());
      proposal.setDraftUnit(unitName);
      proposal.setDocCate(null);
      proposal.setSignFlag("0");
      proposal.setExtension(null);
      //表示该实例是子流程实例
      proposal.setSubJudge("1");
      //将局交办单反馈时间给子流程交办反馈时间
      proposal.setSubRequestDate(proposal.getRequestDate());

      if (StringUtils.isBlank(proposal.getDocMark())) {
        proposal.setDocMark("");
      }
      //过滤头尾空格
      if (StringUtils.isNotBlank(proposal.getSubject())) {
        proposal.setSubject(StringUtils.trim(proposal.getSubject()));
      }
      int result = this.proposalDao.insertProposalMotion(proposal);
      if (result < 1) {
        throw new BusinessException("登记提案议案失败");
      }
      final String MODULE_NO = "PROPOSALMOTION";
      //拷贝正文
      this.egovAttMng.copyEgovAttByDocId(mainDocId, targetId, "main_doc", "main_doc");
      //拷贝普通附件
      this.egovAttMng.copyEgovAttByDocId(mainDocId, targetId, "attach", "attach");
//      //拷贝阅办单附件
//      this.egovAttMng.copyEgovAttByDocId(proposal.getDealFormNo(), targetId, "attach", "attach");
      // 添加数据到solr
      try {
        this.solrDataDao.add(proposal.toSolrMap());
      } catch (Exception e) {
        e.printStackTrace();
        throw new BusinessException("solr数据添加失败");
      }
      //添加流程关系
      FlowRelation flowRelation = new FlowRelation();
      flowRelation.setId(IdUtil.getUID());
      flowRelation.setParentDocId(mainDocId);
      flowRelation.setSonDocId(targetId);
      flowRelation.setParentModuleNo(MODULE_NO);
      flowRelation.setSonModuleNo(MODULE_NO);
      flowRelation.setFlowType(FlowTypeConstant.TO_DO);
      flowRelation.setAId(aid);
      flowRelation.setCreateUserName(securityUser.getUserName());
      flowRelation.setCreateUserNo(securityUser.getUserNo());
      flowRelation.setCreateTime(new Timestamp(System.currentTimeMillis()));
      flowRelation.setSonDept(umsOrg.getOrgName());
      flowRelation.setSonDeptNo(deptNos.get(i));
      flowRelation.setFlowVersion(flowVersion);
      if(i==0 && "0".equals(methodType)){
        flowRelation.setUnitType(UnitTypeConstant.MAIN);
      }else {
        flowRelation.setUnitType(UnitTypeConstant.OTHER);
      }
      if("0".equals(methodType)){
        flowRelationMng.addFlowRelationToFlow(flowRelation);
      }else{
        flowRelationMng.insertFlowRelation(flowRelation);
      }
    }

    return true;
  }



  @Override
  public boolean insertSubDealForm(ProposalAssistQuery proposalAssistQuery) {
    String mainDocId = proposalAssistQuery.getDocId();
    Proposal proposal = this.proposalDao.getProposalMotionById(mainDocId);
    if(proposal == null ){
      throw new BusinessException("文档不存在");
    }
    List<DealForm> dealForm = proposalAssistQuery.getDealForm();
    String targetId = IdUtil.getUID();
    final String MODULE_NO = "PROPOSALMOTION";
    //保存办理单为附件
    if (dealForm != null &&dealForm.size()>0 ) {
      SecurityUser user = SecurityUtils.getPrincipal();
      byte[] fileByte = dealForm.get(0).getFile().getBytes();
      String dealFormTitle = dealForm.get(0).getFileName();
      EgovAtt egovAtt = new EgovAtt();
      String dealFormAttachId = IdUtil.getUID();
      egovAtt.setId(dealFormAttachId);
      egovAtt.setModuleId(MODULE_NO);
      egovAtt.setDocId(targetId);
      egovAtt.setFileSize(fileByte.length);
      egovAtt.setStatus("1");
      egovAtt.setType("attach");
      egovAtt.setFile(fileByte);
      egovAtt.setFileName(dealFormTitle);
      egovAtt.setFileSuffix("html");
      egovAtt.setContentType("text/html");
      egovAtt.setSystemNo(user.getSystemNo());
      this.egovAttMng.insertEgovAtt(egovAtt, user);
    }
    proposal.setDealFormNo(targetId);
    if(this.proposalDao.updateProposalMotion(proposal) ==0){
      throw new BusinessException("阅办单转附件失败");
    }
    return true;
  }

  @Override
  public boolean getFlowStatus(String docId) throws Exception {
    String moduleId = "PROPOSALMOTION";
    return this.flowRelationMng.getFlowStatus(docId,moduleId,FlowTypeConstant.TO_DO);
  }

  @Override
  @Transactional
  public Boolean submitProcessUsers(SubmitParam submitParam) throws Exception {
    Proposal proposal = this.getProposalMotionById(submitParam.getDocId());
    JSONObject permission = this.todoTransferMng.getProcessPermission(submitParam.getAid(), proposal.toMap());
    if(permission!=null && !"".equals(permission)){
      String buttons =permission.getJSONObject("business").getString("buttons");
      //判断当前环节是否存在分发权限
      if(buttons != null && buttons.indexOf("assist") > -1){
        //交办前置判断，如果子流程未作废，阻止交办
        List<String> docIdList = this.flowRelationMng.getDraftSonDocIdList(submitParam.getDocId(), "PROPOSALMOTION", FlowTypeConstant.TO_DO, "1");
        if(docIdList!=null &&!docIdList.isEmpty()){
          //如果子流程未启用流程，删除子流程。
          this.delProposalMotion(docIdList);
        }
        this.insertSubProposalMotions(submitParam.getDocId(),submitParam.getAid(),proposal.getUndertakeDepartment(),"0");
      }
      //判断当前环节是否存在汇合权限
      if(buttons != null && buttons.indexOf("converge") > -1){
        this.getFlowStatus(submitParam.getDocId());
      }
    }
    return this.todoTransferMng.submitProcessUsers(submitParam.getAid(), proposal.toMap(), this.proposalFlowOperator, submitParam.getNextStates(), submitParam.getMsgType());
  }

  @Override
  @Transactional
  public Boolean submitProcessWithoutUsers(SubmitParam submitParam) throws Exception {
    Proposal proposal = this.getProposalMotionById(submitParam.getDocId());
    JSONObject permission = this.todoTransferMng.getProcessPermission(submitParam.getAid(), proposal.toMap());
    if(permission!=null && !"".equals(permission)){
      String buttons =permission.getJSONObject("business").getString("buttons");
      //判断当前环节是否存在汇合权限
      if(buttons != null && buttons.indexOf("converge") > -1){
        this.getFlowStatus(submitParam.getDocId());
      }
    }
    return this.todoTransferMng.submitProcessWithoutUsers(submitParam.getAid(), proposal.toMap(), this.proposalFlowOperator);
  }

  @Override
  public Boolean updateSubOption(String sonDocId) throws Exception {
    FlowRelationQuery flowRelationQuery = new FlowRelationQuery();
    flowRelationQuery.setSonDocId(sonDocId);
    flowRelationQuery.setSonModuleNo("PROPOSALMOTION");
    flowRelationQuery.setFlowType(FlowTypeConstant.TO_DO);
    List<FlowRelation> flowRelationList = this.flowRelationMng.getFlowRelationAll(flowRelationQuery);
    if(flowRelationList!=null && !flowRelationList.isEmpty()){
      FlowRelation flowRelation = flowRelationList.get(0);
      if(flowRelation!=null){
        //同步子流程局交办单领导意见和办公室意见
        Proposal parentProposal =this.proposalDao.getProposalMotionById(flowRelation.getParentDocId());
        Proposal sonProposal = this.proposalDao.getProposalMotionById(flowRelation.getSonDocId());
        sonProposal.setBureauOpinions(parentProposal.getBureauOpinions());
        sonProposal.setOfficeOpinions(parentProposal.getOfficeOpinions());
        this.proposalDao.updateProposalMotion(sonProposal);
        return true;
      }
    }
    return false;
  }

  @Override
  public Boolean submitProcessCancelFinishedUsers(SubmitParam submitParam) throws Exception {
    Proposal proposal = this.getProposalMotionById(submitParam.getDocId());
    //更新流程关系
    //this.flowRelationMng.updateFlowRelationCancel(submitParam.getDocId(),"PROPOSALMOTION", FlowTypeConstant.TO_DO,"1");
    return this.doneTransferMng.submitProcessCancleFinishedUsers(submitParam.getDocId(), proposal.toMap(), this.proposalFlowOperator, submitParam.getNextStates(), submitParam.getMsgType());
  }

  @Override
  @Transactional
  public boolean setProcessRestart(String docId) throws Exception {
    if (StringUtils.isBlank(docId)){
      throw  new BusinessException("重启流程文档ID不存在");
    }
    List<String> listPid = new ArrayList<>();
    Proposal proposal = this.getProposalMotionById(docId);
    listPid.add(proposal.getFlowPid());
    proposal.setFlowDoneUser("");
    proposal.setFlowLabel("");
    proposal.setFlowPid("");
    proposal.setFlowStatus("");
    proposal.setFlowVersion("");
    proposal.setFlowStatus("0");
    proposal.setDocCate("");
    proposal.setExtension("");
    this.updateProposalMotion(proposal);
    //删除子流程部分流程关联信息，并对子流程进行作废或删除处理
    this.flowRelationMng.delAllSonFlowRelation(docId,"PROPOSALMOTION");
    //删除意见
    this.docOpinionMng.deleteDocOpinionByDocId(docId);
    //流程销毁
    if(this.todoTransferMng.destoryProcess(listPid)){
      return true;
    }else{
      throw new BusinessException("流程销毁失败");
    }
  }

}
