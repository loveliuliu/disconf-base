/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.client.utils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.baidu.disconf.core.common.constants.Constants;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.baidu.disconf.client.common.model.Tag;
import com.baidu.disconf.client.config.DisClientConfig;
import com.baidu.disconf.client.config.DisClientSysConfig;
import com.baidu.disconf.client.fetcher.FetcherMgr;
import com.baidu.disconf.core.common.path.DisconfWebPathMgr;
import com.baidu.disconf.core.common.utils.AesUtil;
import com.baidu.disconf.core.common.utils.FileUtils;
import com.baidu.disconf.core.common.utils.OsUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * app 标签帮助类 加载标签和值、保存备份
 * @author luoshiqian 2016/11/24 16:41
 */
public class AppTagHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppTagHelper.class);

    public static final Map<String, String> TAG_STORE = new ConcurrentHashMap<>();
    public static final Map<String,Set<String>> USED_TAG_File_FIELDS = Maps.newConcurrentMap();
    private static FetcherMgr fetcherMgr;
    private static Gson gson = new Gson();

    public static void registerFetcherMgr(FetcherMgr fetcherMgr) {
        AppTagHelper.fetcherMgr = fetcherMgr;
    }

    /**
     * 加载app 标签和值 ，不管能否获取到都包住异常，由使用标签时 检查
     */
    public static void loadTag() {

        DisClientConfig clientConfig = DisClientConfig.getInstance();

        // 初始化tag.json文件
        File tagFile = initTagFile(clientConfig);

        try {
            List<Tag> tagList = remoteCallAppTags();

            if (!CollectionUtils.isEmpty(tagList)) {// 存在 直接替换
                FileUtils.writeStringToFile(tagFile, gson.toJson(tagList));
                //保存在内存中
                storeInMemory(tagList);
            } else if (tagFile.exists()) {// 不存在，看本地有没有文件
                settingTagFromBackUp(tagFile);
            }

        } catch (Exception e) {
            if (tagFile.exists()) {
                try {
                    settingTagFromBackUp(tagFile);
                } catch (Exception e1) {
                    LOGGER.error("settingTagFromBackUp error,", e);
                }
            } else {
                LOGGER.error("find app tag error,and tagFile not found,", e);
            }
        }

    }

    private static void settingTagFromBackUp(File tagFile) throws Exception {
        String diskTagStr = FileUtils.readFileToString(tagFile);
        List<Tag> tagList = gson.fromJson(diskTagStr, new TypeToken<List<Tag>>() {}.getType());

        storeInMemory(tagList);
        LOGGER.info("using local backup tag:{}", diskTagStr);
    }

    /**
     * 保存在内存中
     * @param tagList
     * @throws Exception
     */
    public static void storeInMemory(List<Tag> tagList)throws Exception{
        // 保存在内存中
        for (Tag tag : tagList) {
            TAG_STORE.put(tag.getTagName(), AesUtil.decrypt(tag.getTagValue()));
        }
    }

    /**
     * 初始化tag.json文件
     *
     * @return
     */
    private static File initTagFile(DisClientConfig clientConfig) {
        String tagFileDir = "/usr/local/config/" + clientConfig.APP + "/" + clientConfig.ENV;
        OsUtil.makeDirs(tagFileDir);
        String envFilePath = tagFileDir + "/tag.json";
        File file = new File(envFilePath);
        return file;
    }

    /**
     * 远程 iapi获取 此app下所有的标签和值
     *
     * @return
     * @throws Exception
     */
    public static List<Tag> remoteCallAppTags() throws Exception {
        if(Constants.DISCONF_IAPI_IS_DOWN){
           return null;
        }
        String tagAndValueStr = fetcherMgr.getValueFromServer(
                DisconfWebPathMgr.getAppTagUrl(DisClientSysConfig.getInstance().CONF_SERVER_STORE_ACTION,
                        DisClientConfig.getInstance().APP));
        if (StringUtils.isNotBlank(tagAndValueStr)) {
            List<Tag> tagList = gson.fromJson(tagAndValueStr, new TypeToken<List<Tag>>() {}.getType());
            return tagList;
        }
        return null;
    }
}
