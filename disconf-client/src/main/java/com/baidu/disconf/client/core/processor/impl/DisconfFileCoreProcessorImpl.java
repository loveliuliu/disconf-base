package com.baidu.disconf.client.core.processor.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.common.model.DisConfCommonModel;
import com.baidu.disconf.client.common.model.DisconfCenterFile;
import com.baidu.disconf.client.config.DisClientConfig;
import com.baidu.disconf.client.core.filetype.FileTypeProcessorUtils;
import com.baidu.disconf.client.core.processor.DisconfCoreProcessor;
import com.baidu.disconf.client.fetcher.FetcherMgr;
import com.baidu.disconf.client.store.DisconfStoreProcessor;
import com.baidu.disconf.client.store.DisconfStoreProcessorFactory;
import com.baidu.disconf.client.store.processor.model.DisconfValue;
import com.baidu.disconf.client.support.registry.Registry;
import com.baidu.disconf.client.utils.AppTagHelper;
import com.baidu.disconf.client.utils.ConfigLoaderUtils;
import com.baidu.disconf.client.watch.WatchMgr;
import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;

/**
 * 配置文件处理器实现
 *
 * @author liaoqiqi
 * @version 2014-8-4
 */
public class DisconfFileCoreProcessorImpl implements DisconfCoreProcessor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DisconfFileCoreProcessorImpl.class);

    // 监控器
    private WatchMgr watchMgr = null;

    // 抓取器
    private FetcherMgr fetcherMgr = null;

    // 仓库算子
    private DisconfStoreProcessor disconfStoreProcessor = DisconfStoreProcessorFactory.getDisconfStoreFileProcessor();

    // bean registry
    private Registry registry = null;

    public DisconfFileCoreProcessorImpl(WatchMgr watchMgr, FetcherMgr fetcherMgr, Registry registry) {

        this.fetcherMgr = fetcherMgr;
        this.watchMgr = watchMgr;
        this.registry = registry;
    }

    /**
     * @throws Exception 
     *
     */
    @Override
    public void processAllItems() throws Exception {

        // add appwatch
        addAppWatch();

        /**
         * 配置文件列表处理
         */
        for (String fileName : disconfStoreProcessor.getConfKeySet()) {

            processOneItem(fileName);

        }
    }

    /**
     * watch app notify
     */
    private void addAppWatch(){
        if (DisClientConfig.getInstance().ENABLE_DISCONF) {
            //
            // Watch
            //
            try {
                DisConfCommonModel disConfCommonModel = new DisConfCommonModel();
                disConfCommonModel.setApp(DisClientConfig.getInstance().APP);
                disConfCommonModel.setEnv(DisClientConfig.getInstance().ENV);
                disConfCommonModel.setVersion(DisClientConfig.getInstance().VERSION);
                if (watchMgr != null) {
                    watchMgr.watchApp(disConfCommonModel);
                    LOGGER.debug("watch ok.");
                } else {
                    LOGGER.warn("cannot monitor {} because watch mgr is null", disConfCommonModel);
                }
            } catch (Throwable t) {
                LOGGER.error("register file to zookeeper failue:", t);
            }
        }
    }
    

    @Override
    public void processOneItem(String key) throws Exception {

        LOGGER.debug("==============\tstart to process disconf file: " + key +
                "\t=============================");

        DisconfCenterFile disconfCenterFile = (DisconfCenterFile) disconfStoreProcessor.getConfData(key);

        updateOneConfFile(key, disconfCenterFile, true);

    }

    /**
     * 更新 一個配置文件, 下载、注入到仓库、Watch 三步骤
     */
    private void updateOneConfFile(String fileName, DisconfCenterFile disconfCenterFile,
            boolean isInStartup) throws Exception {

        Map<String, Object> dataMap = new HashMap<String, Object>();

        //
        // 开启disconf才需要远程下载, 否则就本地就好
        //
        if (DisClientConfig.getInstance().ENABLE_DISCONF) {

            //
            // 下载配置
            //

            String url = disconfCenterFile.getRemoteServerUrl();
            fetcherMgr.downloadFileFromServer(url, fileName, DisconfCenterFile.getFileDir());
        }

        try {
            dataMap = FileTypeProcessorUtils.getKvMap(disconfCenterFile.getSupportFileTypeEnum(),
                    disconfCenterFile.getFilePath());
        } catch (Exception e) {
            throw new Exception("cannot get kv data for " + fileName, e);
        }

        //
        // 注入到仓库中
        //
        disconfStoreProcessor.inject2Store(fileName, new DisconfValue(dataMap), isInStartup);
        LOGGER.debug("inject ok.");

        //
        // 开启disconf才需要进行watch
        //
        if (DisClientConfig.getInstance().ENABLE_DISCONF) {
            //
            // Watch
            //
            try {
                DisConfCommonModel disConfCommonModel = disconfStoreProcessor.getCommonModel(fileName);
                if (watchMgr != null) {
                    watchMgr.watchPath(this, disConfCommonModel, fileName, DisConfigTypeEnum.FILE,
                            ConfigLoaderUtils.loadFile(disconfCenterFile.getFilePath()));
                    LOGGER.debug("watch ok.");
                } else {
                    LOGGER.warn("cannot monitor {} because watch mgr is null", fileName);
                }
            } catch (Throwable t) {
                LOGGER.error("register file to zookeeper failue:" + fileName, t);
            }
        }
    }

    /**
     * 更新消息: 某个配置文件 + 回调
     */
    @Override
    public void updateOneConfAndCallback(String key) throws Exception {

        // 更新tag 使disconf可以动态增加标签
        AppTagHelper.loadTag();

        // 更新 配置
        updateOneConf(key);

        // 回调
        DisconfCoreProcessUtils.callOneConf(disconfStoreProcessor, key);
    }

    /**
     * 更新消息：某个配置文件
     */
    private void updateOneConf(String fileName) throws Exception {

        DisconfCenterFile disconfCenterFile = (DisconfCenterFile) disconfStoreProcessor.getConfData(fileName);

        if (disconfCenterFile != null) {

            // 更新仓库
            updateOneConfFile(fileName, disconfCenterFile, false);

            // 更新实例
            inject2OneConf(fileName, disconfCenterFile, false);
        }
    }

    /**
     * 为某个配置文件进行注入实例中
     * @throws Exception 
     */
    private void inject2OneConf(String fileName, DisconfCenterFile disconfCenterFile, boolean isInStartup) throws Exception {

        if (disconfCenterFile == null) {
            return;
        }

        Object object;

        object = disconfCenterFile.getObject();
        if (object == null) {
            object = registry.getFirstByType(disconfCenterFile.getCls(), true);
        }

        // 注入实体中
        disconfStoreProcessor.inject2Instance(object, fileName, isInStartup);

    }

    @Override
    public void inject2Conf() throws Exception {

        /**
         * 配置文件列表处理
         */
        for (String key : disconfStoreProcessor.getConfKeySet()) {

            LOGGER.debug("==============\tstart to inject value to disconf file item instance: " + key +
                    "\t=============================");

            DisconfCenterFile disconfCenterFile = (DisconfCenterFile) disconfStoreProcessor.getConfData(key);

            inject2OneConf(key, disconfCenterFile, true);
        }
    }
}
