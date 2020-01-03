package com.zjhousing.egov.proposal.business;

import com.zjhousing.egov.proposal.business.mapper.ProposalMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProposalBusinessConfiguration.class)
public class ProposalTest {
//  @Resource
//  private ProposalMng proposalMng;
//  @Resource
//  private ProposalMapper proposalMapper;
  @Test
  public void test(){
    System.out.println(new Date().getTime());
    //System.out.println(proposalMapper.getProposalMotionById("XdeV5_uqBFGqy9cF"));
//    Proposal proposal = new Proposal();
//    proposalMng.insertProposalMotion(proposal);
//    System.out.println(proposalMng.getProposalMotionById("XdICt_uqMy0pMKHN","1"));
  }

}
