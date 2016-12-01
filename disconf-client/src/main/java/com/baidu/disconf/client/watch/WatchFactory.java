package com.baidu.disconf.client.watch;

import com.baidu.disconf.core.common.constants.Constants;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.config.ConfigMgr;
import com.baidu.disconf.client.config.DisClientConfig;
import com.baidu.disconf.client.config.DisClientSysConfig;
import com.baidu.disconf.client.fetcher.FetcherMgr;
import com.baidu.disconf.client.watch.impl.WatchMgrImpl;
import com.baidu.disconf.core.common.path.DisconfWebPathMgr;

import java.util.Map;

/**
 * 监控器 实例 工厂
 *
 * @author liaoqiqi
 * @version 2014-7-29
 */
public class WatchFactory {

    protected static final Logger LOGGER = LoggerFactory.getLogger(WatchFactory.class);

    private static String hosts = null;
    private static String zooPrefix = null;
    private static final Object hostsSync = new Object();

    /**
     * @throws Exception
     */
    public static WatchMgr getWatchMgr(FetcherMgr fetcherMgr) throws Exception {

        if (!ConfigMgr.isInit()) {
            throw new Exception("ConfigMgr should be init before WatchFactory.getWatchMgr");
        }

        if (hosts == null || zooPrefix == null) {
            synchronized (hostsSync) {
                if (hosts == null || zooPrefix == null) {

                    Map<String,String> map = getZooHostAndPrefix(fetcherMgr);

                    hosts = map.get("hosts");
                    zooPrefix = map.get("zooPrefix");

                    if (hosts != null && zooPrefix != null) {
                        try {
                            WatchMgr watchMgr = new WatchMgrImpl();
                            watchMgr.init(hosts, zooPrefix, DisClientConfig.getInstance().DEBUG);
                            return watchMgr;
                        } catch (Exception e) {
                            LOGGER.error("watch zoo error", e);
                        }
                    }

                }
            }
        }

        return null;
    }

    public static Map<String,String> getZooHostAndPrefix(FetcherMgr fetcherMgr){
        String hosts = null;
        String zooPrefix = null;
        try {

            hosts = fetcherMgr.getValueFromServer(DisconfWebPathMgr.getZooHostsUrl(DisClientSysConfig
                    .getInstance().CONF_SERVER_ZOO_ACTION));

            zooPrefix = fetcherMgr.getValueFromServer(DisconfWebPathMgr.getZooPrefixUrl(DisClientSysConfig
                    .getInstance().CONF_SERVER_ZOO_ACTION));
        } catch (Exception e) {

            LOGGER.error(
                    "cannot get zoo hosts ,consider disconf iapi is down,make global down flag is true ",
                    e);

            // 获取不到zoo 就可以表示，disconf iapi挂了，之后所有文件获取都从本地
            Constants.DISCONF_IAPI_IS_DOWN = true;
        }
        Map<String,String> map = Maps.newHashMap();
        map.put("hosts",hosts);
        map.put("zooPrefix",zooPrefix);
        return map;
    }
}
