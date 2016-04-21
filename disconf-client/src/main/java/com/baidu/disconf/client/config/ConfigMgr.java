package com.baidu.disconf.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.baidu.disconf.client.config.inner.DisClientComConfig;
import com.baidu.disconf.client.config.inner.DisInnerConfigHelper;

/**
 * 配置模块
 *
 * @author liaoqiqi
 * @version 2014-6-6
 */
public class ConfigMgr {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ConfigMgr.class);

    private static boolean isInit = false;

    /**
     * 初始化配置
     *
     * @throws Exception
     */
    public synchronized static void init(Resource propertiesLocation ) throws Exception {

        LOGGER.info("--------------- LOAD CONFIG START ---------------");

        //
        LOGGER.info("Finer print: " + DisClientComConfig.getInstance().getInstanceFingerprint());

        // 导入系统配置
        DisClientSysConfig.getInstance().loadConfig( );

        // 校验 系统配置
        DisInnerConfigHelper.verifySysConfig();

        // 导入用户配置
        DisClientConfig.getInstance().loadConfig(propertiesLocation);

        // 校验 用户配置
        DisInnerConfigHelper.verifyUserConfig();
        
        if ( "local".equalsIgnoreCase(DisClientConfig.getInstance().ENV) ) {
            LOGGER.info("Disconf is running in LOCAL env, all config files are loaded from classpath");
            DisClientConfig.getInstance().unitTestMode = true;
            DisClientConfig.getInstance().ENABLE_DISCONF = false;
        }

        isInit = true;

        LOGGER.info("--------------- LOAD CONFIG END ---------------");
    }

    /**
     */
    public synchronized static boolean isInit() {
        return isInit;
    }


}
