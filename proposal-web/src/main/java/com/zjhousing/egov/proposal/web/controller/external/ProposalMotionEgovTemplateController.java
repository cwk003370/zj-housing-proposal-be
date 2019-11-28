package com.zjhousing.egov.proposal.web.controller.external;

import com.rongji.egov.flowutil.business.model.EgovTemplate;
import com.rongji.egov.flowutil.business.model.EgovTemplateFile;
import com.rongji.egov.flowutil.business.service.EgovTemplateFileMng;
import com.rongji.egov.flowutil.business.service.EgovTemplateMng;
import com.rongji.egov.flowutil.web.controller.EgovTemplateController;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.business.util.SecurityUtils;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.api.paging.PagingRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 模板
 * @author luzhangfei
 * @date 2018/11/29
 */
@RequestMapping("/proposalmotion")
@RestController
public class ProposalMotionEgovTemplateController  {
  @Resource
  private EgovTemplateFileMng egovTemplateFileMng;
  @Resource
  private EgovTemplateMng egovTemplateMng;
  @GetMapping({"/getTemplateFile"})
  public List<EgovTemplateFile> getTemplateFile(String moduleId, String type, String os) {
    SecurityUser securityUser = (SecurityUser) SecurityUtils.getPrincipal();
    EgovTemplate template = new EgovTemplate();
    template.setModuleId(moduleId);
    template.setType(type);
    template.setSystemNo(securityUser.getSystemNo());
    List<EgovTemplate> templates = this.egovTemplateMng.getEgovTemplates(template);
    if (templates.size() != 0 && templates != null) {
      List<String> templateIds = new ArrayList();
      Iterator var8 = templates.iterator();

      while(var8.hasNext()) {
        EgovTemplate temp = (EgovTemplate)var8.next();
        templateIds.add(temp.getId());
      }

      List<EgovTemplateFile> egovTemplateFileByTemplateIds = this.egovTemplateFileMng.getEgovTemplateFileByTemplateIds(templateIds, os);
      List<EgovTemplateFile> list = new ArrayList();
      Iterator var10 = egovTemplateFileByTemplateIds.iterator();

      while(var10.hasNext()) {
        EgovTemplateFile egovTemplateFile = (EgovTemplateFile)var10.next();
        if ("1".equals(egovTemplateFile.getStatus())) {
          list.add(egovTemplateFile);
        }
      }

      return list;
    } else {
      return null;
    }
  }
}
