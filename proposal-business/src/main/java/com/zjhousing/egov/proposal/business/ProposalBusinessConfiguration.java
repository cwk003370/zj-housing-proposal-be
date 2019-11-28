package com.zjhousing.egov.proposal.business;

import com.rongji.egov.commonsequence.CommonSequenceConfiguration;
import com.rongji.egov.doc.business.constant.ExternalToOthersConstant;
import com.rongji.egov.docconfig.business.DocConfigBusinessConfiguration;
import com.rongji.egov.docnum.DocNumConfiguration;
import com.rongji.egov.flowutil.business.FlowUtilBusinessConfiguration;
import com.rongji.egov.maximunno.EgovMaximunNoConfiguration;
import com.rongji.egov.solrData.business.SolrDataBusinessConfiguration;
import com.rongji.egov.user.business.UserBusinessConfiguration;
import com.rongji.egov.utils.mybatis.configuration.MybatisConfiguration;
import com.rongji.egov.utils.spring.wrapper.EnumBasePackageWrapper;
import com.rongji.egov.wflow.business.WflowBusinessConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;


/**
 * @author luzhangfei
 *
 * 该类的作用是提供spring配置
 * @Configuration 标识该类是一个spring配置类
 * @ComponentScan 默认扫描该包及子包下的spring的组件
 * @MappserScan 扫描指定包的mybatis mapper
 * @EnableTransactionManagement 启用事务管理
 * @EnableCaching 启用缓存
 * @EnableAutoConfiguration 启用spring boot的自动配置
 */
@Configuration
@ComponentScan
@MapperScan({"com/zjhousing/egov/proposal/business/mapper"})
@EnableTransactionManagement
@EnableCaching
@EnableAutoConfiguration
@EnableConfigurationProperties({ProposalBusinessProperties.class})
@Import({
  MybatisConfiguration.class,
  DocConfigBusinessConfiguration.class,
  FlowUtilBusinessConfiguration.class,
  WflowBusinessConfiguration.class,
  UserBusinessConfiguration.class,
  SolrDataBusinessConfiguration.class,
  DocNumConfiguration.class,
  EgovMaximunNoConfiguration.class,
  CommonSequenceConfiguration.class
  })
public class ProposalBusinessConfiguration {
  public static void main(String[] args) {
    SpringApplication.run(ProposalBusinessConfiguration.class, args);
  }

  @Bean
  EnumBasePackageWrapper docBusinessEnumBasePackageWrapper() {
    return new EnumBasePackageWrapper("com.zjhousing.egov.proposal.business.proposal.enums");
  }

  @Bean
  RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
      .setConnectTimeout(ExternalToOthersConstant.CONNECT_TIMEOUT)
      .setReadTimeout(ExternalToOthersConstant.READ_TIMEOUT)
      .build();
  }
}
