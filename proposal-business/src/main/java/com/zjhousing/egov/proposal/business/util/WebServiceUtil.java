package com.zjhousing.egov.proposal.business.util;

import com.rongji.egov.utils.exception.BusinessException;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

/**
 * webService服务
 *
 * @author lindongmei
 * @date 2018/11/15
 */
public class WebServiceUtil {

  /**
   * webService客户端
   *
   * @param wsdl       WSDL地址
   * @param methodName 方法命名
   * @param param      请求参数
   * @return
   */
  public static String executeWsRequest(String wsdl, String methodName, String param) {
    JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
    Client client = dcf.createClient(wsdl);
    String result;
    try {
      Object[] objects = client.invoke(methodName, param);
      result = objects[0].toString();
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessException("执行webService服务异常");
    }
    return result;
  }
}
