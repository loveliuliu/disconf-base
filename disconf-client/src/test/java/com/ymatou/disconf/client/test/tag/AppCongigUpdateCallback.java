/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.ymatou.disconf.client.test.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfUpdateService;
import com.baidu.disconf.client.common.update.IDisconfUpdate;


@Service("appCongigUpdateCallback")
@DisconfUpdateService(classes = {AppConfig.class})
public class AppCongigUpdateCallback implements IDisconfUpdate {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AppCongigUpdateCallback.class);

    @Autowired
    private AppConfig appConfig;

    /**
     *
     */
    public void reload() throws Exception {


        LOGGER.info("config1:======================={}", appConfig.getConfig1());
        LOGGER.info("config2:======================={}", appConfig.getConfig2());
        LOGGER.info("config3:======================={}", appConfig.getConfig3());


    }

}
