package com.baidu.disconf.client.config.inner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.config.DisClientConfig;
import com.baidu.disconf.client.config.DisClientSysConfig;
import com.baidu.disconf.client.utils.StringUtil;
import com.baidu.disconf.core.common.utils.OsUtil;

/**
 * 用户配置、系统配置 的校验
 *
 * @author liaoqiqi
 * @version 2014-6-6
 */
public class DisInnerConfigHelper {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DisInnerConfigHelper.class);

    /**
     * @throws Exception
     * @Description: 校验用户配置文件是否正常
     * @author liaoqiqi
     * @date 2013-6-13
     */
    public static void verifyUserConfig() throws Exception {

        //
        // 服务器相关
        //

        //
        // 服务器地址

        if (StringUtils.isEmpty(DisClientConfig.getInstance().CONF_SERVER_HOST)) {

            throw new Exception("settings: " + DisClientConfig.CONF_SERVER_HOST_NAME + " cannot find");
        }

        DisClientConfig.getInstance()
                .setHostList(StringUtil.parseStringToStringList(DisClientConfig.getInstance().CONF_SERVER_HOST, ","));
        LOGGER.info(
                "SERVER " + DisClientConfig.CONF_SERVER_HOST_NAME + ": " + DisClientConfig.getInstance().getHostList());

        //
        // 版本

        if (StringUtils.isEmpty(DisClientConfig.getInstance().VERSION)) {

            throw new Exception("settings: " + DisClientConfig.VERSION_NAME + " cannot find");
        }
        LOGGER.info("SERVER " + DisClientConfig.VERSION_NAME + ": " + DisClientConfig.getInstance().VERSION);

        //
        // APP名

        if (StringUtils.isEmpty(DisClientConfig.getInstance().APP)) {

            throw new Exception("settings: " + DisClientConfig.APP_NAME + " cannot find");
        }
        LOGGER.info("SERVER " + DisClientConfig.APP_NAME + ": " + DisClientConfig.getInstance().APP);

        //
        // 环境

        if (StringUtils.isEmpty(DisClientConfig.getInstance().ENV)) {

            throw new Exception("settings: " + DisClientConfig.ENV_NAME + "  cannot find");
        }
        LOGGER.info("SERVER " + DisClientConfig.ENV_NAME + ": " + DisClientConfig.getInstance().ENV);

        //
        // 是否使用远程的配置
        LOGGER.info("SERVER disconf.enable.remote.conf: " + DisClientConfig.getInstance().ENABLE_DISCONF);
        
        
        if (StringUtils.isEmpty(DisClientConfig.getInstance().userDefineDownloadDir)) {
            //默认的配置文件下载目录
            DisClientConfig.getInstance().userDefineDownloadDir = "/usr/local/config/" 
                    + DisClientConfig.getInstance().APP + "/" 
                    + DisClientConfig.getInstance().ENV + "/"
                    + DisClientConfig.getInstance().VERSION;
        }
        LOGGER.info("SERVER " + "user_define_download_dir" + ": " + DisClientConfig.getInstance().userDefineDownloadDir);
        OsUtil.makeDirs(DisClientConfig.getInstance().userDefineDownloadDir);

        //
        // debug mode
        LOGGER.info("SERVER disconf.debug: " + DisClientConfig.getInstance().DEBUG);

        //
        // 忽略哪些分布式配置
        //
        List<String> ignoreDisconfList =
                StringUtil.parseStringToStringList(DisClientConfig.getInstance().IGNORE_DISCONF_LIST, ",");
        Set<String> keySet = new HashSet<String>();
        if (ignoreDisconfList != null) {
            for (String ignoreData : ignoreDisconfList) {
                keySet.add(ignoreData.trim());
            }
        }
        DisClientConfig.getInstance().setIgnoreDisconfKeySet(keySet);
        LOGGER.info("SERVER ignore: " + DisClientConfig.getInstance().getIgnoreDisconfKeySet());

        // 重试
        LOGGER.debug("SERVER conf_server_url_retry_times: " + DisClientConfig
                .getInstance().CONF_SERVER_URL_RETRY_TIMES);

        LOGGER.debug("SERVER conf_server_url_retry_sleep_seconds: " +
                DisClientConfig.getInstance().confServerUrlRetrySleepSeconds);

    }

    /**
     * @throws Exception
     * @Description: 校验系统配置文件是否正常
     * @date 2013-6-13
     */
    public static void verifySysConfig() throws Exception {

        //
        // 服务器相关
        //

        // CONF_SERVER_STORE_ACTION
        if (StringUtils.isEmpty(DisClientSysConfig.getInstance().CONF_SERVER_STORE_ACTION)) {

            throw new Exception("settings: CONF_SERVER_STORE_ACTION cannot find");
        }
        LOGGER.debug("SERVER CONF_SERVER_STORE_ACTION: " + DisClientSysConfig.getInstance().CONF_SERVER_STORE_ACTION);

        // CONF_SERVER_ZOO_ACTION
        if (StringUtils.isEmpty(DisClientSysConfig.getInstance().CONF_SERVER_ZOO_ACTION)) {

            throw new Exception("settings: CONF_SERVER_ZOO_ACTION cannot find");
        }
        LOGGER.debug("SERVER CONF_SERVER_ZOO_ACTION: " + DisClientSysConfig.getInstance().CONF_SERVER_ZOO_ACTION);
    }

}
