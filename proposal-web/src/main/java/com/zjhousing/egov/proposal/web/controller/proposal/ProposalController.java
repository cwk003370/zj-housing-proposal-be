package com.zjhousing.egov.proposal.web.controller.proposal;


import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.docconfig.business.annotation.DocReadLogAn;
import com.rongji.egov.user.business.dao.UserDao;
import com.rongji.egov.user.business.model.RmsRole;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.business.model.vo.DraftUser;
import com.rongji.egov.user.business.util.UserDraftUtils;
import com.rongji.egov.user.web.annotation.CurrentUser;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.rongji.egov.utils.exception.BusinessException;
import com.rongji.egov.utils.spring.validation.InsertValidate;
import com.rongji.egov.utils.spring.validation.UpdateValidate;
import com.rongji.egov.wflow.business.model.dto.temp.ReadSend;
import com.rongji.egov.wflow.business.service.engine.manage.ProcessManageMng;
import com.rongji.egov.wflow.business.service.engine.transfer.TodoTransferMng;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.query.ProposalAssistQuery;
import com.zjhousing.egov.proposal.business.service.ProposalFlowOperator;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 提案议案
 *
 * @author chenwenkang
 * @date 2019/11/15
 **/
@RestController
@RequestMapping("/proposal")
public class ProposalController {
  @Resource
  private ProposalMng proposalMng;
  @Resource
  private UserDao userDao;
  @Resource
  private ProcessManageMng processManageMng;
  @Resource
  private TodoTransferMng todoTransferMng;
  @Resource
  private ProposalFlowOperator proposalFlowOperator;

  /**
   * 根据ID得到提案议案信息
   * 注：参数docId必须放在第一个位置，否则日志注解利用反射机制无法取到文档id
   *
   * @param docId 提案议案ID
   * @return
   */
  @GetMapping("/getProposalMotionById")
  @DocReadLogAn(moduleId = "PROPOSALMOTION", operator = "insert")
  public Proposal getProposalMotionById(@RequestParam("docId") String docId) {
    return this.proposalMng.getProposalMotionById(docId);
  }

  /**
   * 根据ID得到提案议案详细信息
   * 注：参数docId必须放在第一个位置，否则日志注解利用反射机制无法取到文档id
   *
   * @return
   */
  @GetMapping("/getProposalMotionDetailById")
  @DocReadLogAn(moduleId = "proposalMotion", operator = "insert")
  public JSONObject getProposalDetailById(@RequestParam("docId") String docId, @RequestParam(name = "aid", required = false) String aid) {
    return this.proposalMng.getProposalMotionDetailById(docId, aid);
  }

  /**
   * 登记提案议案<br/>
   * 注1：字段“文件标题（subject）”不能为空
   *
   * @param proposal
   * @param bindingResult
   * @return
   */
  @PostMapping("/insertProposalMotion")
  public Proposal insertProposal(@CurrentUser SecurityUser user, @RequestBody @Validated({InsertValidate.class}) Proposal proposal, BindingResult bindingResult) {
    //验证表单数据
    if (bindingResult.hasErrors()) {
      throw new BusinessException(bindingResult.getFieldError().getDefaultMessage());
    }
    proposal.setSystemNo(user.getSystemNo());
    int insertResult = this.proposalMng.insertProposalMotion(proposal);
    if (insertResult != 1) {
      throw new BusinessException("提案登记出错");
    } else {
      return this.proposalMng.getProposalMotionById(proposal.getId());
    }
  }
  /**
   * 删除登记文件
   *
   * @param list 稿件ID集合
   */
  @PostMapping("/deleteProposalMotion")
  @DocReadLogAn(moduleId = "PROPOSALMOTIO", operator = "delete")
  public void deleteProposal(@RequestBody List<String> list) {
    if (list == null || list.size() == 0) {
      throw new BusinessException("参数list为空");
    }
    int deleteResult = this.proposalMng.delProposalMotion(list);
    if (deleteResult < 1) {
      throw new BusinessException("删除提案议案稿件出错");
    }
  }

  /**
   * 更新提案议案
   *
   * @param proposal
   * @param bindingResult 验证对象
   * @return
   */
  @PostMapping("/updateProposalMotion")
  public Proposal updateRegProposal(@CurrentUser SecurityUser securityUser,@RequestBody @Validated({UpdateValidate.class}) Proposal proposal, BindingResult bindingResult) {
    //验证表单数据
    if (bindingResult.hasErrors()) {
      throw new BusinessException(bindingResult.getFieldError().getDefaultMessage());
    }
    Proposal old = this.proposalMng.getProposalMotionById(proposal.getId());

    if (!old.getFlowStatus().equals(proposal.getFlowStatus())) {
      throw new BusinessException("流程状态已被修改！");
    }
    final String FLOW_STATUS = "0";
    if (!FLOW_STATUS.equals(proposal.getFlowStatus())) {
      this.processManageMng.updateTodo(proposal.toMap());
    }
    int updateResult = this.proposalMng.updateProposalMotion(proposal);
    if (updateResult != 1) {
      throw new BusinessException("文件更新出错");
    } else {
      return this.proposalMng.getProposalMotionById(proposal.getId());
    }
  }
  /**
   * 提案议案分页
   * 界面：登记
   *
   * @param paging
   * @param proposal
   * @param word
   * @return
   */
  @GetMapping("/proposalMotionPageJson")
  public Page<Proposal> proposalPageJson(@CurrentUser SecurityUser user, PagingRequest<Proposal> paging, Proposal proposal, String word) {
    List<RmsRole> roles = userDao.listUserRole(user.getUserNo(), user.getSystemNo());
    Boolean manage = false;
    for (RmsRole role : roles) {
      if ("proposalMotion_manager".equals(role.getRoleNo()) || "sys_manager".equals(role.getRoleNo())) {
        manage = true;
      }
    }
    if (!manage) {
      //拟稿人
      proposal.setDraftUserNo(user.getUserNo());
    }
    proposal.setSystemNo(user.getSystemNo());
    String[] strings = null;
    if (StringUtils.isNotBlank(word)) {
      strings = word.trim().split("\\s+");
    }
    return this.proposalMng.getProposalMotion4Page(paging, proposal, strings);
  }
  /**
   * 提案议案分页
   * 界面：回收站
   *
   * @param paging
   * @param proposal
   * @param word
   * @return
   */
  @GetMapping("/proposalMotionPageJsonRecycle")
  public Page<Proposal> proposalPageJsonRecycle(@CurrentUser SecurityUser user, PagingRequest<Proposal> paging, Proposal proposal, String word) {
    proposal.setSystemNo(user.getSystemNo());
    String[] strings = null;
    if (StringUtils.isNotBlank(word)) {
      strings = word.trim().split("\\s+");
    }
    return this.proposalMng.getProposalMotion4Page(paging, proposal, strings);
  }
  /**
   * 内部分发（内部传阅）
   *
   * @param readSend
   * @return
   * @throws Exception
   */
  @PostMapping("/innerDistribute")
  boolean innerDistribute(@RequestBody ReadSend readSend) throws Exception {
    Boolean noUsers = readSend.getUsers() == null || readSend.getUsers().isEmpty();
    Boolean noGroupNos = readSend.getGroupNos() == null || readSend.getGroupNos().isEmpty();
    Boolean noOrgNos = readSend.getOrgNos() == null || readSend.getOrgNos().isEmpty();
    if (StringUtils.isBlank(readSend.getDocId())) {
      throw new BusinessException("文档ID不能为空");
    } else if (noUsers && noGroupNos && noOrgNos) {
      throw new BusinessException("新增传阅对象不能为空");
    } else {
      boolean innerDistributeFlag = this.processManageMng.innerDistribute(readSend);
      if (innerDistributeFlag) {
        Proposal proposal = this.proposalMng.getProposalMotionById(readSend.getDocId());
        HashSet<String> strings = new HashSet<>(readSend.getOrgNos());
        if (readSend.getGroupNos() != null && readSend.getGroupNos().size() > 0) {
          strings.addAll(readSend.getGroupNos());
        }
        for (ReadSend.User user : readSend.getUsers()) {
          strings.add(user.getUserNo());
        }
        proposal.setInReaders(strings);
        this.proposalMng.updateProposalMotion(proposal);
      }
      return innerDistributeFlag;
    }
  }
  /**
   * 发文solr分页查询
   *
   * @param paging     分页对象
   * @param proposal   提案对象
   * @param draftYear  起草年份
   * @param draftMonth 起草月份
   * @param draftDay   起草日
   * @param word       关键字模糊查询
   * @return
   */
  @GetMapping("/proposalMotionSolrPageJson")
  public Page<SolrDocument> dispatchSolrPageJson(@CurrentUser SecurityUser user, PagingRequest<Proposal> paging, Proposal proposal,
                                                 @RequestParam(name = "draftYear", required = false) Integer draftYear,
                                                 @RequestParam(name = "draftMonth", required = false) Integer draftMonth,
                                                 @RequestParam(name = "draftDay", required = false) Integer draftDay,
                                                 String word) {
    proposal.setSystemNo(user.getSystemNo());
    return this.proposalMng.getProposalMotionBySolr(paging, proposal, draftYear, draftMonth, draftDay, word);
  }

  /**
   * 子流程-阅办单另存为附件
   *
   * @param
   * @return
   * @throws Exception
   */
  @PostMapping("/subprocess/insertSubDealForm")
  public boolean insertSubDealForm(@RequestBody ProposalAssistQuery proposalAssistQuery) {
        return this.proposalMng.insertSubDealForm(proposalAssistQuery);
  }
  /**
   * 子流程-流程初始化
   * @param label 流程标识
   * @param version 流程版本
   * @param docId   文档ID
   * @param userNo 启用用户的编码
   * @param userOrgNo 启用用户的组织编码
   * @throws Exception
   */
  @GetMapping("/subprocess/initProcess")
  public String initProcess(String label, String version, String docId,String userNo,String userOrgNo) {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    if (StringUtils.isNotBlank(proposal.getFlowPid())) {
      throw new BusinessException("该文件已启用流程");
    } else {
      HashMap<String, Object> proposalMap = proposal.toMap();
      try {
        String aid = this.todoTransferMng.initProcess(label, version, this.proposalFlowOperator, proposalMap,userNo,userOrgNo);
        return aid;
      } catch (Exception var10) {
        throw new BusinessException(var10.getMessage());
      }
    }
  }
  /**
   * 子流程-补充协办子流程
   * @param docId   文档ID
   * @param deptNos 交办部门ID集合
   * @param aid   流程环节ID
   * @throws Exception
   */
  @GetMapping("/subprocess/addSubProposalMotions")
  public boolean addSubProposalMotions(@RequestParam(name = "docId") String docId,
                                       @RequestParam(name = "aId") String aid,
                                       @RequestParam(name = "deptNos[]") List<String> deptNos) {
    return this.proposalMng.insertSubProposalMotions(docId,aid,deptNos,"1");

  }
}
