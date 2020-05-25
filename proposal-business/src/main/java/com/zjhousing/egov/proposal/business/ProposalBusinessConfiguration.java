package com.zjhousing.egov.proposal.business;

import com.rongji.egov.commonsequence.CommonSequenceConfiguration;
import com.rongji.egov.doc.business.DocBusinessConfiguration;
import com.rongji.egov.flowrelation.business.FlowRelationBusinessConfiguration;
import com.rongji.egov.flowutil.business.FlowUtilBusinessConfiguration;
import com.rongji.egov.maximunno.EgovMaximunNoConfiguration;
import com.rongji.egov.solrData.business.SolrDataBusinessConfiguration;
import com.rongji.egov.user.business.UserBusinessConfiguration;
import com.rongji.egov.utils.mybatis.configuration.MybatisConfiguration;
import com.rongji.egov.wflow.business.WflowBusinessConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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
@Import({
  MybatisConfiguration.class,
  FlowUtilBusinessConfiguration.class,
  WflowBusinessConfiguration.class,
  UserBusinessConfiguration.class,
  DocBusinessConfiguration.class,
  SolrDataBusinessConfiguration.class,
  EgovMaximunNoConfiguration.class,
  CommonSequenceConfiguration.class,
  FlowRelationBusinessConfiguration.class
  })
public class ProposalBusinessConfiguration {
  public static void main(String[] args) {
    SpringApplication.run(ProposalBusinessConfiguration.class, args);
  }

  @Bean
  RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
      .setConnectTimeout(5000)
      .setReadTimeout(5000)
      .build();
  }
}
