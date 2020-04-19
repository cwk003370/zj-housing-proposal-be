package com.zjhousing.egov.proposal;

import com.zjhousing.egov.proposal.web.ProposalWebConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = {ProposalWebConfiguration.class})
@RunWith(SpringRunner.class)
@Transactional
public abstract class BaseTest {
}
