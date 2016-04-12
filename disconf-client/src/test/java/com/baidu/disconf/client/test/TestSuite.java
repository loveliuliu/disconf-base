package com.baidu.disconf.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.baidu.disconf.client.test.config.ConfigMgrTestCase;
import com.baidu.disconf.client.test.core.DisconfCoreMgrTestCase;
import com.baidu.disconf.client.test.fetcher.FetcherMgrMgrTestCase;
import com.baidu.disconf.client.test.json.JsonTranslate;
import com.baidu.disconf.client.test.scan.ScanMgrTestCase;
import com.baidu.disconf.client.test.scan.inner.ScanPackTestCase;
import com.baidu.disconf.client.test.watch.WatchMgrTestCase;
import com.ymatou.disconf.client.test.ex.nolocalfile.RejectStatupNoLocalFileTest;
import com.ymatou.disconf.client.test.ex.wrongconfigvalue.RejectStartupWrongConfigValueTest;
import com.ymatou.disconf.client.test.integration.StartupTest;

@RunWith(Suite.class)
@SuiteClasses({ScanPackTestCase.class, DisconfMgrTestCase.class, WatchMgrTestCase.class, ScanMgrTestCase.class,
                  JsonTranslate.class, FetcherMgrMgrTestCase.class, DisconfCoreMgrTestCase.class,
                  ConfigMgrTestCase.class,
                  //StartupTest.class,
                  //RejectStartupWrongConfigValueTest.class,
                  //RejectStatupNoLocalFileTest.class
                  })
public class TestSuite {

}