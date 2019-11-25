package com.zjhousing.egov.proposal.web;

import com.rongji.egov.attachutil.AttachConfiguration;
import com.rongji.egov.depttask.web.DeptTaskWebConfiguration;
import com.rongji.egov.docconfig.web.DocConfigWebConfiguration;
import com.rongji.egov.email.web.EmailWebConfiguration;
import com.rongji.egov.flowutil.web.FlowUtilWebConfiguration;
import com.rongji.egov.solrcopydata.web.SolrCopyDataWebConfiguration;
import com.rongji.egov.user.web.UserWebConfiguration;
import com.rongji.egov.user.web.security.IgnoredPathsWrapper;
import com.rongji.egov.wflow.web.WflowWebConfiguration;
import com.zjhousing.egov.proposal.business.ProposalBusinessConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author zhangshiyi
 * @date 2018-8-30
 **/
@SpringBootApplication
@Configuration
@Import({
  ProposalBusinessConfiguration.class,
  DocConfigWebConfiguration.class,
  FlowUtilWebConfiguration.class,
  UserWebConfiguration.class,
  EmailWebConfiguration.class,
  WflowWebConfiguration.class
})
public class ProposalWebConfiguration extends WebMvcConfigurerAdapter {
  public static void main(String[] args) {
    SpringApplication.run(ProposalWebConfiguration .class, args);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST").allowCredentials(false).maxAge(3600);
  }

  @Bean
  IgnoredPathsWrapper hsjEgovDocIgnoredPathsWrapper() {
    return new IgnoredPathsWrapper("/dispatch/signDocumentFromEx");
  }
}