package com.baidu.disconf.client.test.integration;

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

import junit.framework.Assert;
import mockit.Mock;
import mockit.MockUp;

/**
 * 
 * @author tuwenjie
 *
 */
public class StartUpTest extends BaseCoreTestCase {

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
        
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("/integration/applicationContext.xml");
        
        Assert.assertEquals("config1", appContext.getBean(AppConfig.class).getConfig1());
        Assert.assertEquals(100, appContext.getBean(AppConfig.class).getConfig2());
        
        Assert.assertEquals("config1", appContext.getBean(NonAnotationConfig1.class).getConfig1());
        Assert.assertEquals(200, appContext.getBean(NonAnotationConfig1.class).getConfig2());
        Assert.assertEquals("config3", appContext.getBean(NonAnotationConfig1.class).getConfig3());
        
        Assert.assertEquals("config4", appContext.getBean(NonAnotationConfig2.class).getConfig4());
        Assert.assertEquals(500, appContext.getBean(NonAnotationConfig2.class).getConfig5());
        Assert.assertEquals("config6", appContext.getBean(NonAnotationConfig2.class).getConfig6());
        
        
        appContext.close();
    }

}
