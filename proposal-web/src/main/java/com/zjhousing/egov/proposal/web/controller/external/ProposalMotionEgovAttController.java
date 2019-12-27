package com.zjhousing.egov.proposal.web.controller.external;

import com.rongji.egov.attachutil.controller.EgovAttController;
import com.rongji.egov.attachutil.model.EgovAtt;
import com.rongji.egov.user.business.model.SecurityUser;
import com.rongji.egov.user.web.annotation.CurrentUser;
import com.rongji.egov.utils.exception.BusinessException;
import com.zjhousing.egov.proposal.business.service.ProposalMng;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 附件相关操作
 *
 * @author lindongmei
 * @date 2018/11/30
 */
@RequestMapping("/proposalmotion")
@RestController
public class ProposalMotionEgovAttController extends EgovAttController {
  @Override
  public List<EgovAtt> getEgovAttByDocId(String docId, String type, boolean isCotainFile) {
    return super.getEgovAttByDocId(docId, type, isCotainFile);
  }

  @Override
  public List<EgovAtt> getEgovAttsByDocIdAndTypes(String docId, @RequestParam("type[]") List<String> type, boolean isCotainFile) {
    return super.getEgovAttsByDocIdAndTypes(docId, type, isCotainFile);
  }

  @Override
  public List<EgovAtt> getEgovAttsByFilter(String docId, @RequestParam("filterType[]") List<String> filterType, boolean isContainFile) {
    return super.getEgovAttsByFilter(docId, filterType, isContainFile);
  }

  @Override
  public EgovAtt updateEgovAttFileName(String id, @RequestParam("fileName") String fileName) {
    return super.updateEgovAttFileName(id, fileName);
  }

  @Override
  public EgovAtt uploadEgovAttFile(@CurrentUser SecurityUser user, @RequestParam("multipartFile") MultipartFile multipartFile, HttpServletRequest request) throws Exception {
    return super.uploadEgovAttFile(user, multipartFile, request);
  }

  @Override
  public String handleFileUpload(@CurrentUser SecurityUser user, HttpServletRequest request) {
    return super.handleFileUpload(user, request);
  }

  @Override
  public void deleteEgovAtt(@RequestBody List<String> list) {
    super.deleteEgovAtt(list);
  }

  @Override
  public void downloadEgovAttFile(@RequestParam("id") String id, HttpServletResponse response) {
    super.downloadEgovAttFile(id, response);
  }

  @Override
  public void moveUpOrDownEgovAtt(@RequestParam("id") String egovAttId, String docId, String upOrDown) {
    super.moveUpOrDownEgovAtt(egovAttId, docId, upOrDown);
  }

  @Override
  public String uploadEgovAttFileWithBase64(@CurrentUser SecurityUser user, EgovAtt egovAtt) {
    return super.uploadEgovAttFileWithBase64(user, egovAtt);
  }
}
