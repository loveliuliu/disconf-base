package com.baidu.disconf.client;

import java.io.File;

import com.baidu.disconf.client.common.model.DisconfCenterFile;

/**
 * 
 * @author tuwenjie
 *
 */
public class DisConf {

	
    /**
     * 如果需要，应用可以直接获取Disconf保存到本地的配置文件
     * @param fileName
     * @return
     */
    public static File getLocalConfig( String fileName ) {
        return new File(DisconfCenterFile.getFilePath(fileName));
    }
}
