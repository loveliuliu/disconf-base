/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.ymatou.disconf.client.test.tag;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baidu.disconf.client.common.model.CmdbEnv;
import com.baidu.disconf.client.fetcher.impl.FetcherMgrImpl;
import com.baidu.disconf.client.test.common.BaseCoreTestCase;
import com.baidu.disconf.client.test.watch.mock.WatchMgrMock;
import com.baidu.disconf.client.utils.EnvHelper;
import com.baidu.disconf.client.watch.WatchFactory;
import com.baidu.disconf.client.watch.WatchMgr;

import mockit.Expectations;

/**
 * 
 * @author tuwenjie
 *
 */
public class StartupTest extends BaseCoreTestCase {

    @Test
    public void mockStg() {

        new Expectations(EnvHelper.class) {
            {
                try {
                    EnvHelper.remoteCallCmdbEnv(anyString, anyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CmdbEnv cmdbEnv = new CmdbEnv();
                cmdbEnv.setEnvironment("STG");
                cmdbEnv.setCode(0);
                result = cmdbEnv;
            }
        };
        new Expectations(WatchFactory.class) {
            {
                try {
                    WatchFactory.getWatchMgr(withAny(new FetcherMgrImpl(null, 1, 1, "", null)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                WatchMgr watchMgr = new WatchMgrMock().getMockInstance();

                result = watchMgr;
            }
        };


        ClassPathXmlApplicationContext appContext =
                new ClassPathXmlApplicationContext("/integration/applicationContext-tag-test.xml");
        System.out.println("--------------------------application started----------------------");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mockPrd() {

        new Expectations(EnvHelper.class) {
            {
                try {
                    EnvHelper.remoteCallCmdbEnv(anyString, anyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CmdbEnv cmdbEnv = new CmdbEnv();
                cmdbEnv.setEnvironment("PRD");
                cmdbEnv.setCode(0);
                result = cmdbEnv;
            }
        };

//        mockZooSuccess();
        mockZooError();
        ClassPathXmlApplicationContext appContext =
                new ClassPathXmlApplicationContext("/integration/applicationContext-tag-test.xml");
        System.out.println("--------------------------application started----------------------");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 运行之前先mock stg或prd 没有的话 就是测试启动不起来
     */
    @Test
    public void mockUsingLocalBackupOrStopStartup() {
        ClassPathXmlApplicationContext appContext =
                new ClassPathXmlApplicationContext("/integration/applicationContext-tag-test.xml");

        System.out.println("--------------------------application started----------------------");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mockUat() {

        System.setProperty("disconf.env", "UAT");

        ClassPathXmlApplicationContext appContext =
                new ClassPathXmlApplicationContext("/integration/applicationContext-tag-test.xml");
        System.out.println("--------------------------application started----------------------");

        TagConfig tagConfig = appContext.getBean(TagConfig.class);
        System.out.println(tagConfig.getConfig1());
        System.out.println(tagConfig.getConfig2());

        NoTagConfig noTagConfig= appContext.getBean(NoTagConfig.class);
        System.out.println(noTagConfig.getNoTagConfig1());
        System.out.println(noTagConfig.getNoTagConfig2());


        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mockZooSuccess(){
        new Expectations(WatchFactory.class) {
            {
                try {
                    WatchFactory.getZooHostAndPrefix(withAny(new FetcherMgrImpl(null, 1, 1, "", null)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Map<String,String> map = Maps.newHashMap();
                map.put("hosts","172.16.103.18:2181");
                map.put("zooPrefix","/disconf");

                result = map;
            }
        };

    }

    private void mockZooError(){
        new Expectations(WatchFactory.class) {
            {
                try {
                    WatchFactory.getZooHostAndPrefix(withAny(new FetcherMgrImpl(null, 1, 1, "", null)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Map<String,String> map = Maps.newHashMap();
                map.put("hosts","192.168.1.101:2181");
                map.put("zooPrefix","/disconf");

                result = map;
            }
        };

    }
}
