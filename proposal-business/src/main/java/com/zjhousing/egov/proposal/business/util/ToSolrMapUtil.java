package com.zjhousing.egov.proposal.business.util;

import com.rongji.egov.attachutil.model.EgovAtt;
import com.rongji.egov.attachutil.service.EgovAttMng;
import com.rongji.egov.utils.spring.SpringBootBeanUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 公文封装solr map工具类
 *
 * @author lindongmei
 * @date 2018/12/12
 */
public class ToSolrMapUtil {

  /**
   * 获取正文内容
   *
   * @param id        文档id
   * @param flowStaus 流程状态
   * @return
   */
  public static List<String> getFileStringByTikaList(String id, String flowStaus) {
    List<String> body = new ArrayList<>();
    // 流程办结
    final String FLOW_DONE = "9";
    if (FLOW_DONE.equals(flowStaus)) {
      EgovAttMng attMng = SpringBootBeanUtil.getBean(EgovAttMng.class);
      List<EgovAtt> list = attMng.getEgovAttsByDocId(id, false);
      if (list != null && list.size() > 0) {
        for (EgovAtt att : list) {
          String text = "";
          try {
            text = attMng.getFileStringByTika(att);
          } catch (Exception e) {
            e.printStackTrace();
          }
          body.add(text);
        }
      }
    }
    return body;
  }
}
