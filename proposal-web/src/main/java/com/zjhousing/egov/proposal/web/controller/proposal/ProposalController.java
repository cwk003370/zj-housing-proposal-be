package com.zjhousing.egov.proposal.web.controller.proposal;


import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.docconfig.business.annotation.DocReadLogAn;
import com.rongji.egov.user.business.dao.UserDao;
import com.rongji.egov.user.business.model.RmsRole;
import com.rongji.egov.user.business.model.SecurityUser;
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
import com.zjhousing.egov.proposal.business.service.ProSequenceMng;
import com.zjhousing.egov.proposal.business.service.ProposalFlowOperator;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
  @Autowired
  private ProSequenceMng proSequenceMng;
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
  public Proposal updateRegProposal(@RequestBody @Validated({UpdateValidate.class}) Proposal proposal, BindingResult bindingResult) {
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
   * 界面：登记、办结
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
   * 子流程-提案登记
   *
   * @param
   * @return
   * @throws Exception
   */
  @GetMapping("/subprocess/insertProposalMotion")
  public Proposal insertProposal(@RequestParam("docId") String docId, @RequestParam("userNo") String userNo, @RequestParam("userOrgNo") String userOrgNo, @RequestParam("docCate") String docCate, @RequestParam("userName") String userName) {
    Proposal proposal = this.proposalMng.getProposalMotionById(docId);
    int insertResult = this.proposalMng.insertSubProposalMotion(proposal,userNo,userOrgNo,docCate,userName);
    if (insertResult != 1) {
      throw new BusinessException("子流程提案登记失败");
    } else {
      return this.proposalMng.getProposalMotionById(proposal.getId());
    }
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
}
