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
import com.rongji.egov.wflow.business.service.engine.manage.ProcessManageMng;
import com.zjhousing.egov.proposal.business.model.Proposal;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 提案议案
 *
 * @author chenwenkang
 * @date 2019/11/15
 **/
@RestController
@RequestMapping("/com/zjhousing/egov/proposal/business/mapper/proposal")
public class ProposalController {
  @Resource
  private ProposalMng proposalMng;
  @Resource
  private UserDao userDao;
  @Resource
  private ProcessManageMng processManageMng;

  /**
   * 根据ID得到发文信息
   * 注：参数docId必须放在第一个位置，否则日志注解利用反射机制无法取到文档id
   *
   * @param docId 发文ID
   * @return
   */
  @GetMapping("/getProposalMotionById")
  @DocReadLogAn(moduleId = "PROPOSALMOTION", operator = "insert")
  public Proposal getProposalMotionById(@RequestParam("docId") String docId) {
    return this.proposalMng.getProposalMotionById(docId);
  }

  /**
   * 根据ID得到发文详细信息
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
   * 登记发文<br/>
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
      throw new BusinessException("文件登记出错");
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
  @DocReadLogAn(moduleId = "DISPATCH", operator = "delete")
  public void deleteProposal(@RequestBody List<String> list) {
    if (list == null || list.size() == 0) {
      throw new BusinessException("参数list为空");
    }
    int deleteResult = this.proposalMng.delProposalMotion(list);
    if (deleteResult < 1) {
      throw new BusinessException("删除发文稿件出错");
    }
  }

  /**
   * 更新发文
   *
   * @param dispatch
   * @param bindingResult 验证对象
   * @return
   */
  @PostMapping("/updateProposalMotion")
  public Proposal updateRegProposal(@RequestBody @Validated({UpdateValidate.class}) Proposal dispatch, BindingResult bindingResult) {
    //验证表单数据
    if (bindingResult.hasErrors()) {
      throw new BusinessException(bindingResult.getFieldError().getDefaultMessage());
    }
    Proposal old = this.proposalMng.getProposalMotionById(dispatch.getId());
    if (!old.getFlowStatus().equals(dispatch.getFlowStatus())) {
      throw new BusinessException("流程状态已被修改！");
    }
    final String FLOW_STATUS = "0";
    if (!FLOW_STATUS.equals(dispatch.getFlowStatus())) {
      this.processManageMng.updateTodo(dispatch.toMap());
    }
    int updateResult = this.proposalMng.updateProposalMotion(dispatch);
    if (updateResult != 1) {
      throw new BusinessException("文件更新出错");
    } else {
      return this.proposalMng.getProposalMotionById(dispatch.getId());
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
  public Page<Proposal> dispatchPageJson(@CurrentUser SecurityUser user, PagingRequest<Proposal> paging, Proposal proposal, String word) {
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
}
