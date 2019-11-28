package com.zjhousing.egov.proposal.web.controller.external;

import com.zjhousing.egov.proposal.business.query.ProToOthersQuery;
import com.zjhousing.egov.proposal.business.service.ProToOthersMng;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 发文转其他文 controller
 *
 * @author lindongmei
 * @date 2018/10/31
 */
@RestController
@RequestMapping("/proposalmotion")
public class ProToOthersController {

  @Resource
  private ProToOthersMng proToOthersMng;

  /**
   * 发文转其他文<br/>
   * <p>type: 操作类型 必填</p>
   * <p>      取值："归历史公文库";"转部门阅办";"发部局";"转依申请公开库";</p>
   * <p>docId: 文档id 必填</p>
   * <p>publicFlag: 公开属性</p>
   * <p>readers： 可读人员<p/>
   *
   * @param proToOthersQuery
   * @return
   */
  @PostMapping("/proToOthers")
  public boolean disToOthers(@RequestBody ProToOthersQuery proToOthersQuery) {
    return this.proToOthersMng.proToOthers(proToOthersQuery);
  }
}
