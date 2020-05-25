package com.zjhousing.egov.proposal.web.controller.external;

import com.rongji.egov.doc.business.common.HsjDocSolrSearchMng;
import com.rongji.egov.flowutil.business.util.ExpireCacheUtil;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.web.annotation.CurrentUser;
import com.rongji.egov.utils.exception.BusinessException;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
/**
 * @author lindongmei
 * @desc 公文solr查询 controller类
 **/

@RestController
@RequestMapping("/proposalmotion")
public class ProSolrSearchController {
  @Resource
  private HsjDocSolrSearchMng hsjDocSolrSearchMng;
  @Resource
  private ExpireCacheUtil expireCacheUtil;

  /**
   * 得到分类种类信息
   *
   * @param facets      查询字段
   * @param moduleName  模块名称
   * @param filterQuery 筛选条件（“;”分隔）
   * @return
   */
  @PostMapping("/docCommon/getCategory")
  String getCategory(@CurrentUser SecurityUser user, @RequestBody String[] facets, String moduleName, String filterQuery) {
    if (0 == facets.length) {
      throw new BusinessException("查询字段不能为空");
    }
    if (StringUtils.isBlank(moduleName)) {
      throw new BusinessException("模块名称不能为空");
    }
    String key = user.getUserNo();
    for (String str : facets) {
      key += "_" + str;
    }
    key += moduleName;
    String uniqueObject = expireCacheUtil.getUniqueObject(key, String.class);
    if (StringUtils.isNotBlank(uniqueObject)) {
      return uniqueObject;
    } else {
      String result = this.hsjDocSolrSearchMng.getCategory(facets, moduleName, filterQuery);
      expireCacheUtil.setUniqueObject(key, result);
      return result;
    }
  }
}
