package com.baidu.disconf.client.test.integration;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baidu.disconf.client.core.DisconfCoreFactory;
import com.baidu.disconf.client.core.DisconfCoreMgr;
import com.baidu.disconf.client.core.impl.DisconfCoreMgrImpl;
import com.baidu.disconf.client.fetcher.FetcherFactory;
import com.baidu.disconf.client.fetcher.FetcherMgr;
import com.baidu.disconf.client.support.registry.Registry;
import com.baidu.disconf.client.test.common.BaseCoreTestCase;
import com.baidu.disconf.client.test.watch.mock.WatchMgrMock;
import com.baidu.disconf.client.watch.WatchMgr;
import mockit.Mock;
import mockit.MockUp;

/**
 * 
 * @author tuwenjie
 *
 */
public class RejectStartupWrongConfigValueTest extends BaseCoreTestCase {

    @Test
    public void testStartUp( ) {
        new MockUp<DisconfCoreFactory>() {

            @Mock
            public DisconfCoreMgr getDisconfCoreMgr(Registry registry) throws Exception {

                FetcherMgr fetcherMgr = FetcherFactory.getFetcherMgr();

                // Watch 模块
                final WatchMgr watchMgr = new WatchMgrMock().getMockInstance();
                watchMgr.init("", "", true);

                // registry
                DisconfCoreMgr disconfCoreMgr = new DisconfCoreMgrImpl(watchMgr, fetcherMgr,
                        registry);

                return disconfCoreMgr;
            }
        };
        
        try {
            ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("/integration/applicationContext-wrongConfigValue.xml");
            
            Assert.assertTrue( false );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
   

    }

}
