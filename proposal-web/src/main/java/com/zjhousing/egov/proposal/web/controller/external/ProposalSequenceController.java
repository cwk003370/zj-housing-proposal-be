package com.zjhousing.egov.proposal.web.controller.external;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rongji.egov.flowutil.business.model.EgovTemplateFile;
import com.rongji.egov.flowutil.business.service.EgovTemplateFileMng;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.business.util.SecurityUtils;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.rongji.egov.utils.exception.BusinessException;
import com.zjhousing.egov.proposal.business.model.ProposalSequence;
import com.zjhousing.egov.proposal.business.service.ProSequenceMng;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 提案议案-流水号配置
 *
 * @author chenwenkang
 * @date 2019/11/15
 **/
@RestController
@RequestMapping("/proposalmotion")
public class ProposalSequenceController {
  @Autowired
  private ProSequenceMng proSequenceMng;
  @Autowired
  private EgovTemplateFileMng templateFileMng;

  /**
   * @apiDefine group 收文-流水号配置
   */



  /**
   * 流水号配置分页
   *
   * @param word        关键字模糊查询
   * @param proposalSequence 流水号对象
   * @param paging      分页对象
   * @return
   */
  @GetMapping("/sequence/sequencePageJson")
  public Page<ProposalSequence> sequencePageJson(String word, ProposalSequence proposalSequence, PagingRequest<ProposalSequence> paging) {
    SecurityUser user = SecurityUtils.getPrincipal();
    proposalSequence.setSystemNo(user.getSystemNo());
    return this.proSequenceMng.getSequence4Page(paging, proposalSequence, word);
  }

  /**
   * 根据ID得到流水号配置项
   *
   * @param id 流水号配置项ID
   * @return
   */
  @GetMapping("/sequence/getSequenceById")
  public ProposalSequence getSequenceById(@RequestParam("id") String id) {
    return this.proSequenceMng.getSequenceById(id);
  }

  /**
   * 添加流水配置项<br/>
   * 注1： 字段“流水号名称（sequenceName）、序号位数（sequenceLength）、序号初始值（initValue）、流水号模式（sequenceMode）、文件分类（docCate）
   * 、状态（status）、流水号分类（sequenceCate）不能为空”
   *
   * @param proposalSequence   流水配置对象
   * @param bindingResult 验证参数绑定返回
   * @return 流水配置对象
   */
  @PostMapping("/sequence/insertSequence")
  public ProposalSequence insertSequence(@RequestBody @Validated ProposalSequence proposalSequence, BindingResult bindingResult) {
    //验证表单数据
    if (bindingResult.hasErrors()) {
      throw new BusinessException(bindingResult.getFieldError().getDefaultMessage());
    }

    SecurityUser user = SecurityUtils.getPrincipal();
    proposalSequence.setSystemNo(user.getSystemNo());
    int insertResult = this.proSequenceMng.insertSequence(proposalSequence);
    if (insertResult != 1) {
      throw new BusinessException("添加流水配置出错");
    } else {
      return this.proSequenceMng.getSequenceById(proposalSequence.getId());
    }
  }

  /**
   * 删除流水配置项
   *
   * @param list
   */
  @PostMapping("/sequence/deleteSequence")
  public void deleteSequence(@RequestBody List<String> list) {
    if (list == null || list.size() == 0) {
      throw new BusinessException("参数list为空");
    }
    int deleteResult = this.proSequenceMng.deleteSequence(list);
    if (deleteResult < 1) {
      throw new BusinessException("删除流水配置出错");
    }
  }

  /**
   * 编辑流水号配置项
   *
   * @param proposalSequence
   * @param bindingResult
   */
  @PostMapping("/sequence/updateSequence")
  public ProposalSequence updateSequence(@RequestBody @Validated ProposalSequence proposalSequence, BindingResult bindingResult) {
    //验证表单数据
    if (bindingResult.hasErrors()) {
      throw new BusinessException(bindingResult.getFieldError().getDefaultMessage());
    }
    int updateResult = this.proSequenceMng.updateSequence(proposalSequence);
    if (updateResult < 1) {
      throw new BusinessException("更新流水配置出错");
    } else {
      return this.proSequenceMng.getSequenceById(proposalSequence.getId());
    }
  }

  /**
   * 根据文件类别及办阅件标识得到配置的流水号<br/>
   * 根据流水号得到配置的模板信息<br/>
   * 根据模板信息得到模板文件<br/>
   *
   * @param docCate
   * @param type         得到的模板类别
   * @return
   */
  @GetMapping("/sequence/getEgovTemplateFilesByDocCate")
  public List<EgovTemplateFile> getEgovTemplateFilesByDocCate(String docCate, String type, String os, String systemNo) {
    return this.proSequenceMng.getTemplateFileByDocCate(docCate, type, os, systemNo);
  }

//  /**
//   * 获取文件、办阅件级联数据(浙江)
//   *
//   * @return
//   */
//  @GetMapping("/receival/sequence/getCascadDataToDealForm")
//  public JSONArray getCascadDataToDealForm() {
//    JSONArray jsonArray = new JSONArray();
//    List<ProposalSequence> sequences = this.proSequenceMng.getSequenceList(null);
//
//    JSONObject json;
//    for (ProposalSequence sequence : sequences) {
//      json = new JSONObject();
//      json.put("label", sequence.getSequenceName());
//      json.put("value", sequence.getDocCate());
//
//      //子级
//      JSONArray childArray = new JSONArray();
//      JSONObject childJson;
//      for (String cate : sequence.getDealReadCate()) {
//        childJson = new JSONObject();
//        if ("1".equals(cate)) {
//          childJson.put("label", "办件");
//        }
//        if ("2".equals(cate)) {
//          childJson.put("label", "阅件");
//        }
//        childJson.put("value", cate);
//        childArray.add(childJson);
//      }
//      json.put("children", childArray);
//      jsonArray.add(json);
//    }
//    return jsonArray;
//  }

  /**
   * 根据文件类型、办阅件获取阅办单集合
   *
   * @return
   */
  @GetMapping("/sequence/getealFormByCascadData")
  public List<EgovTemplateFile> getealFormByCascadData(@RequestParam("docCate") String docCate,  String os, String systemNo) {
    //阅办单ID集合
    List<String> dealFormIds = null;
    ProposalSequence proposalSequence = new ProposalSequence();
    proposalSequence.setDocCate(docCate);

    if(StringUtils.isBlank(systemNo)){
      SecurityUser user = SecurityUtils.getPrincipal();
      systemNo = user.getSystemNo();
    }
    proposalSequence.setSystemNo(systemNo);

    //流水号
    List<ProposalSequence> list = this.proSequenceMng.getSequenceList(proposalSequence);
    if (null == list || list.size() == 0) {
      throw new BusinessException("请为相应的文件类型配置流水号");
    }
    for (ProposalSequence rs : list) {
      if (rs.getDealFormId() != null) {
        dealFormIds = rs.getDealFormId();
        break;
      }
    }
    if (null == dealFormIds || 0 == dealFormIds.size()) {
      throw new BusinessException("请配置相应的阅办单");
    }
    //获取阅办单数据集合
    List<EgovTemplateFile> egovTemplateFiles = this.templateFileMng.getEgovTemplateFileByTemplateIds(dealFormIds, os);

    List<EgovTemplateFile> templateFiles = new ArrayList<>();
    for (EgovTemplateFile egovTemplateFile : egovTemplateFiles) {
      if (egovTemplateFile.getStatus().equals("1")) {
        templateFiles.add(egovTemplateFile);
      }
    }
    return templateFiles;
  }
}
