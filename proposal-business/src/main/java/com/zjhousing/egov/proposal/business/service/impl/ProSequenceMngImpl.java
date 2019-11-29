package com.zjhousing.egov.proposal.business.service.impl;



import com.rongji.egov.flowutil.business.model.EgovTemplateFile;
import com.rongji.egov.flowutil.business.service.EgovTemplateFileMng;
import com.rongji.egov.maximunno.service.EgovMaximumNoMng;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.business.util.SecurityUtils;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import com.rongji.egov.utils.common.IdUtil;
import com.rongji.egov.utils.exception.BusinessException;
import com.zjhousing.egov.proposal.business.dao.ProSequenceDao;
import com.zjhousing.egov.proposal.business.model.ProposalSequence;
import com.zjhousing.egov.proposal.business.service.ProSequenceMng;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * 流水号配置 mng impl
 *
 * @author luzhangfei
 */
@Service
public class ProSequenceMngImpl implements ProSequenceMng {

  @Resource
  private ProSequenceDao proSequenceDao;
  @Resource
  private EgovMaximumNoMng egovMaximumNoMng;
  @Resource
  private EgovTemplateFileMng egovTemplateFileMng;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int insertSequence(ProposalSequence proSequence) {
    if (StringUtils.isBlank(proSequence.getId())) {
      proSequence.setId(IdUtil.getUID());
    }
    if (StringUtils.isBlank(proSequence.getStatus())) {
      proSequence.setStatus("1");
    }

    // 判断流水号是否重复设置
    boolean isDuplicate = this.isDuplicate(proSequence);
    if (!isDuplicate) {
      return this.proSequenceDao.insertSequence(proSequence);
    } else {
      throw new BusinessException("该文件类型下流水号已配置！");
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int deleteSequence(List<String> list) {
    return this.proSequenceDao.deleteSequence(list);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int updateSequence(ProposalSequence proSequence) {
    // 判断流水号是否重复设置
    boolean isDuplicate = this.isDuplicate(proSequence);
    if (!isDuplicate) {
      return this.proSequenceDao.updateSequence(proSequence);
    } else {
      throw new BusinessException("该文件类型下流水号已配置！");
    }
  }

  @Override
  public ProposalSequence getSequenceById(String id) {
    return this.proSequenceDao.getSequenceById(id);
  }

  @Override
  public List<EgovTemplateFile> getTemplateFileByDocCate(String docCate, String type, String os, String systemNo) {
    SecurityUser user = SecurityUtils.getPrincipal();
    if(StringUtils.isBlank(systemNo)){
      systemNo = user.getSystemNo();
    }
    ProposalSequence sequence = this.proSequenceDao.getSequenceByDocCateAndDealReadCate(docCate, systemNo);
    List<String> templateIds = new ArrayList<>();
    switch (type) {
      // TODO 抽取模板类别常量
      case "DEALFORM":
        templateIds = sequence.getDealFormId();
        if (templateIds.size() < 1) {
          throw new BusinessException("阅办单未配置");
        }
        break;
      case "ERROR":
        templateIds = sequence.getErrorId();
        if (templateIds.size() < 1) {
          throw new BusinessException("错情办理单未配置");
        }
        break;
      case "TRANSPORT":
        templateIds = sequence.getTransportId();
        if (templateIds.size() < 1) {
          throw new BusinessException("运转单未配置");
        }
        break;
      default:
    }
    //根据模板ID得到模板文件
    List<EgovTemplateFile> templateFiles = this.egovTemplateFileMng.getEgovTemplateFileByTemplateIds(templateIds, os);
    return templateFiles;
  }

  @Override
  public List<ProposalSequence> getSequenceList(ProposalSequence proSequence) {
    return this.proSequenceDao.getSequenceList(proSequence);
  }

  @Override
  public Page<ProposalSequence> getSequence4Page(PagingRequest<ProposalSequence> paging, ProposalSequence proSequence, String word) {
    return this.proSequenceDao.getSequence4Page(paging, proSequence, word);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public HashMap<String, Object> getSequenceNum(String docCate,  String systemNo) {
    if (StringUtils.isBlank(systemNo)) {
      throw new BusinessException("系统编码不能为空");
    }

    HashMap<String, Object> map = new HashMap<>(16);

    Integer maxNo;
    ProposalSequence sequence = this.proSequenceDao.getSequenceByDocCateAndDealReadCate(docCate, systemNo);
    if (null == sequence) {
      throw new BusinessException("流水号未配置");
    } else if ("0".equals(sequence.getStatus())) {
      throw new BusinessException("流水号已停用");
    }

    //获取当前年份
    Calendar date = Calendar.getInstance();
    int year = date.get(Calendar.YEAR);
    //得到组织
    SecurityUser securityUser = SecurityUtils.getPrincipal();
    String deptNo = securityUser.getOrgNo();

    //获取最大值依据是否需要单位编号
    //处室流水-最大值数据保存部门编号
    String egovMaximumOrgNo = securityUser.getOrgNo();
    //厅流水时-最大值数据不保存单位编号
    if ("1".equals(sequence.getSequenceCate())) {
      egovMaximumOrgNo = null;
    }

    maxNo = this.egovMaximumNoMng.getMaximumNo(sequence.getId(), year, egovMaximumOrgNo, sequence.getInitValue()).getMaxNo();


    this.egovMaximumNoMng.updateMaxAndUsedNos(maxNo, sequence.getId(), year, egovMaximumOrgNo);

    String modeStr = sequence.getSequenceMode();
    if (StringUtils.isBlank(modeStr)) {
      throw new BusinessException("流水号模式未配置");
    }
    int length = sequence.getSequenceLength();

    String prefixRegex = "(\\{|\\[|【|\\(|（|〔)";
    String suffixRegex = "(\\}|\\]|】|\\)|）|〕)";
    String yearRegex = prefixRegex + "年度" + suffixRegex;
    String noRegex = prefixRegex + "序号" + suffixRegex;
    String orgNoRegex = prefixRegex + "组织编码" + suffixRegex;
    modeStr = modeStr.replaceAll(yearRegex, String.valueOf(year));
    modeStr = modeStr.replaceAll(orgNoRegex, StringUtils.trimToEmpty(deptNo));
    modeStr = modeStr.replaceAll(noRegex, StringUtils.leftPad(maxNo.toString(), length, "0"));

    map.put("docSequence", modeStr);
    map.put("docSequenceYear", year);
    map.put("docSequenceNum", maxNo);
    return map;
  }

  /**
   * 判断当前流水是否重复<br/>
   *
   * @param proSequence
   * @return
   */
  private boolean isDuplicate(ProposalSequence proSequence) {
    boolean dealDuplicate = false;
    boolean readDuplicate = false;
    readDuplicate = this.proSequenceDao.isDuplicateSequenceByType(proSequence.getDocCate(), proSequence.getSystemNo(), proSequence.getId());
    return dealDuplicate || readDuplicate;
  }
}
