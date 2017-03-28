/*
 *
 * (C) Copyright 2017 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.client.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.common.update.IDisconfAppUpdate;
import com.baidu.disconf.client.config.DisClientConfig;
import com.baidu.disconf.core.common.path.ZooPathMgr;
import com.baidu.disconf.core.common.zookeeper.ZookeeperMgr;

/**
 * @author luoshiqian 2017/3/28 12:22
 */
public class AppWatchUtils {

    private static List<IDisconfAppUpdate> disconfAppUpdateList = new ArrayList<IDisconfAppUpdate>();
    private static final Logger LOGGER = LoggerFactory.getLogger(AppWatchUtils.class);
    public static String zooUrlPrefix = "";

    /**
     * 通知
     * 
     * @param value
     */
    public static void notifyApp(String value) {
        for (IDisconfAppUpdate disconfAppUpdate : disconfAppUpdateList) {
            disconfAppUpdate.notify(value);
        }
    }

    public static void add(IDisconfAppUpdate disconfAppUpdate) {
        disconfAppUpdateList.add(disconfAppUpdate);
    }


    public static void notify(String appName, String version, String value) {
        final String zookeeperPath =
                ZooPathMgr.getZooBaseUrl(zooUrlPrefix, appName, DisClientConfig.getInstance().ENV, version);

        try {
            ZookeeperMgr.getInstance().writePersistentUrl(zookeeperPath, value);
        } catch (Exception e) {
            LOGGER.error("notify error", e);
        }
    }

}
