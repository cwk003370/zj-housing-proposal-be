package com.zjhousing.egov.proposal.business.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.rongji.egov.attachutil.model.EgovAtt;
import com.rongji.egov.attachutil.service.EgovAttMng;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.common.IdUtil;
import com.zjhousing.egov.proposal.business.model.DeptReceival;
import com.zjhousing.egov.proposal.business.query.CommonToOthersQuery;
import com.zjhousing.egov.proposal.business.query.DealForm;
import com.zjhousing.egov.proposal.business.service.ComToOthersMng;
import com.zjhousing.egov.proposal.business.util.DocBusinessProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lindongmei
 * @date 2018/11/14
 */
@Service
public class ComToOthersMngImpl implements ComToOthersMng {


  @Resource
  private RestTemplate withTokenRestTemplate;

  @Resource
  private DocBusinessProperties docBusinessProperties;

  @Resource
  private EgovAttMng egovAttMng;

  @Override
  public Page<DeptReceival> getDeptReceival4Page(String docId, String word, int offset, int limit) {
    Map map = new HashMap(16);
    map.put("sourceId", docId);
    map.put("word", word);
    JSONArray jsonArray = this.withTokenRestTemplate.postForObject(this.docBusinessProperties.getRequestPrefix() + "/deptReceival/listDeptReceivalBySourceId", map, JSONArray.class);
    List<DeptReceival> deptReceivalList = JSONArray.parseArray(jsonArray.toJSONString(), DeptReceival.class);
    Page<DeptReceival> page = new Page<>();
    int size = deptReceivalList.size();
    Long total = new Long(size);
    page.setTotal(total);

    int toIndex = offset+limit;
    if (toIndex > size) {
      toIndex = size;
    }
    page.setList(deptReceivalList.subList(offset, toIndex));
    return page;
  }
  /**
   * 保存办理单信息
   * 注：必要值 user（当前用户）、docId（文档id）、moduleNo（模块编码）、dealForm（阅办单、名称）
   */
  @Override
  public boolean insertDealFormToAtt(CommonToOthersQuery ctq) {
    SecurityUser user = ctq.getUser();
    DealForm dealForm = ctq.getDealForm();
    String docId = ctq.getDocId();
    String dealFormTitle = dealForm.getFileName();
    List<EgovAtt> egovAtts = this.egovAttMng.getEgovAttsByDocIdAndType(docId, "dealForm", false);
    EgovAtt egovAtt = new EgovAtt();
    byte[] fileByte = dealForm.getFile().getBytes();
    if (egovAtts != null && egovAtts.size() > 0) {
      egovAtt = egovAtts.get(0);
      egovAtt.setFile(fileByte);
      egovAtt.setFileSize(fileByte.length);
      egovAtt.setFileName(dealFormTitle);
      this.egovAttMng.updateEgovAtt(egovAtt);
    } else {
      String dealFormAttachId = IdUtil.getUID();
      egovAtt.setId(dealFormAttachId);
      egovAtt.setModuleId(ctq.getModuleNo());
      egovAtt.setDocId(docId);
      egovAtt.setFileSize(fileByte.length);
      egovAtt.setStatus("1");
      egovAtt.setType("dealForm");
      egovAtt.setFile(fileByte);
      egovAtt.setFileName(dealFormTitle);
      egovAtt.setFileSuffix("html");
      egovAtt.setContentType("text/html");
      egovAtt.setSystemNo(user.getSystemNo());
      this.egovAttMng.insertEgovAtt(egovAtt, user);
    }
    return true;
  }

}
