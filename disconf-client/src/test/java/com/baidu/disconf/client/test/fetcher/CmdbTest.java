/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.client.test.fetcher;


import com.baidu.disconf.client.common.model.CmdbEnv;
import com.baidu.disconf.core.common.restful.type.RestfulGet;
import com.baidu.disconf.core.common.utils.http.HttpClientUtil;
import com.google.gson.Gson;
import org.junit.Test;

import java.net.URL;

/**
 * @author luoshiqian 2016/11/23 18:33
 */
public class CmdbTest {

    @Test
    public void testCmdb() throws Exception{
        HttpClientUtil.init();
        URL url = new URL("http://environment.ops.ymatou.cn/api?application=a.pk.ymatou.com&ipaddress=10.12.99.153");
        System.out.println(new Gson().toJson(new RestfulGet<CmdbEnv>(CmdbEnv.class,url).call()));
    }
}
