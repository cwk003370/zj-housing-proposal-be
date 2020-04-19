package com.zjhousing.egov.proposal;

import com.rongji.egov.doc.business.receival.model.Receival;
import com.rongji.egov.solrData.business.dao.BaseSolrDeal;
import com.rongji.egov.utils.api.paging.Page;
import com.rongji.egov.utils.spring.SpringBootBeanUtil;
import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;

public class TestSolrDemoSolrDeal extends BaseTest {

    //这个是简单测下是否有写到solr
    @Test
    @WithUserDetails("zjadmin")
    public void test() throws Exception {
        BaseSolrDeal solrDeal = this.getBaseSolrDeal();
        //获取数据
        Page<Receival> page = solrDeal.productionQuery(0,15,true,null,null,"");
        //写入solr
        solrDeal.productionAddList(page.getList());
    }

    //这个操作会将把模块所有数据同步到solr
    @Test
    @WithUserDetails("zjadmin")
    public void writeAll() throws Exception {
        BaseSolrDeal solrDeal = this.getBaseSolrDeal();
        //查询偏移量;
        int offset = 0;
        //每次查500条记录批量写入
        int limit = 500;
        //每次查到的条数；
        int total = 0;
        do {
            //获取数据
            Page<Receival> page = solrDeal.productionQuery(offset,limit,true,null,null,"");
            total = page.getList().size();
            if(total > 0){
                //写入solr
                solrDeal.productionAddList(page.getList());
                offset += limit;
            }
        }while(total>0);
    }

    private BaseSolrDeal getBaseSolrDeal(){
        //从spring容器中拿到BaseSolrDeal的实现
        String name = "PROPOSALMOTION";
        BaseSolrDeal solrDeal = (BaseSolrDeal) SpringBootBeanUtil.getBean(name);
        return solrDeal;
    }
}
