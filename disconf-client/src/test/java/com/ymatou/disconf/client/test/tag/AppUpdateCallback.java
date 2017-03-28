/*
 *
 * (C) Copyright 2017 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.ymatou.disconf.client.test.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfUpdateService;
import com.baidu.disconf.client.common.update.IDisconfAppUpdate;


@Service("appUpdateCallback")
@DisconfUpdateService(isAppWatch = true)
public class AppUpdateCallback implements IDisconfAppUpdate {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AppUpdateCallback.class);



    @Override
    public void notify(String value) {
        LOGGER.info("notify:" + value);
    }
}
