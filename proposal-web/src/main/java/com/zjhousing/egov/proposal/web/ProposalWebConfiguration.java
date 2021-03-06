package com.zjhousing.egov.proposal.web;


import com.rongji.egov.commonsequence.CommonSequenceConfiguration;
import com.rongji.egov.docconfig.web.DocConfigWebConfiguration;
import com.rongji.egov.flowrelation.web.FlowRelationWebConfiguration;
import com.rongji.egov.flowutil.web.FlowUtilWebConfiguration;
import com.rongji.egov.solrData.business.SolrDataBusinessConfiguration;
import com.rongji.egov.user.web.UserWebConfiguration;
import com.rongji.egov.wflow.web.WflowWebConfiguration;
import com.zjhousing.egov.proposal.business.ProposalBusinessConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author chenwenkang
 * @date 2019-11-22
 **/
@SpringBootApplication
@Configuration
@Import({
  ProposalBusinessConfiguration.class,
  FlowUtilWebConfiguration.class,
  UserWebConfiguration.class,
  WflowWebConfiguration.class,
  SolrDataBusinessConfiguration.class,
  CommonSequenceConfiguration.class,
  FlowRelationWebConfiguration.class,
  DocConfigWebConfiguration.class
})
public class ProposalWebConfiguration extends WebMvcConfigurerAdapter {
  public static void main(String[] args) {
    SpringApplication.run(ProposalWebConfiguration .class, args);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST").allowCredentials(false).maxAge(3600);
  }
}
