package com.baidu.disconf.client;

import java.io.File;

import com.baidu.disconf.client.common.model.DisconfCenterFile;
import com.baidu.disconf.client.config.ConfigMgr;
import com.baidu.disconf.client.utils.AppWatchUtils;

/**
 * 
 * @author tuwenjie
 *
 */
public class DisConf {


    /**
     * 如果需要，应用可以直接获取Disconf保存到本地的配置文件
     * 
     * @param fileName
     * @return
     */
    public static File getLocalConfig(String fileName) {

        if (!ConfigMgr.isInit()) {
            throw new RuntimeException(
                    "Disconf not init yet. Please make sure DisconfMgrBean & DisconfMgrBeanSecond are configured in Spring.");
        }
        return new File(DisconfCenterFile.getFilePath(fileName));

    }


    /**
     * 通知某个app version,同一个环境，修改的值
     * @param appName
     * @param version
     */
    public static void notify(String appName,String version,String value){
        AppWatchUtils.notify(appName,version,value);
    }
}
