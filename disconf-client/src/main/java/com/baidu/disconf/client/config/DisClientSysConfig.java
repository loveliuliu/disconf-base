package com.baidu.disconf.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.config.inner.DisInnerConfigAnnotation;
import com.baidu.disconf.client.support.DisconfAutowareConfig;

/**
 * Disconf 系统自带的配置
 *
 * @author liaoqiqi
 * @version 2014-6-6
 */
public class DisClientSysConfig {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DisClientSysConfig.class);

    protected static final DisClientSysConfig INSTANCE = new DisClientSysConfig();

    public static DisClientSysConfig getInstance() {
        return INSTANCE;
    }

    protected static final String fileName = "disconf_sys.properties";

    private boolean isLoaded = false;

    private DisClientSysConfig() {

    }

    public synchronized boolean isLoaded() {
        return isLoaded;
    }

    /**
     * load config normal
     */
    public synchronized void loadConfig( ) throws Exception {

        if (isLoaded) {
            return;
        }

        DisconfAutowareConfig.autowareConfigFromClassPath(INSTANCE, fileName);

        isLoaded = true;
    }

    /**
     * STORE URL
     *
     * @author
     * @since 1.0.0
     */
    @DisInnerConfigAnnotation(name = "disconf.conf_server_store_action")
    public String CONF_SERVER_STORE_ACTION;

    /**
     * STORE URL
     *
     * @author
     * @since 1.0.0
     */
    @DisInnerConfigAnnotation(name = "disconf.conf_server_zoo_action")
    public String CONF_SERVER_ZOO_ACTION;

}
