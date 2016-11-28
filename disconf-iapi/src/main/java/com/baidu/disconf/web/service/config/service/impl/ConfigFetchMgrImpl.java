package com.baidu.disconf.web.service.config.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.core.common.json.ValueVo;
import com.baidu.disconf.web.service.app.bo.AppTag;
import com.baidu.disconf.web.service.app.bo.Tag;
import com.baidu.disconf.web.service.app.dao.AppTagDao;
import com.baidu.disconf.web.service.app.dao.TagDao;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.dao.ConfigDao;
import com.baidu.disconf.web.service.config.service.ConfigFetchMgr;
import com.baidu.disconf.web.service.config.utils.ConfigUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * @author knightliao
 */
@Service
public class ConfigFetchMgrImpl implements ConfigFetchMgr {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigFetchMgrImpl.class);

    @Autowired
    private ConfigDao configDao;
    @Autowired
    private AppTagDao appTagDao;
    @Autowired
    private TagDao tagDao;

    /**
     * 根据详细参数获取配置
     */
    @Override
    public Config getConfByParameter(Long appId, Long envId, String env, String key,
                                     DisConfigTypeEnum disConfigTypeEnum) {

        Config config = configDao.getByParameter(appId, envId, env, key, disConfigTypeEnum);
        return config;
    }

    /**
     * 根据详细参数获取配置返回
     */
    public ValueVo getConfItemByParameter(Long appId, Long envId, String version, String key) {

        Config config = configDao.getByParameter(appId, envId, version, key, DisConfigTypeEnum.ITEM);
        if (config == null) {
            return ConfigUtils.getErrorVo("cannot find this config");
        }

        ValueVo valueVo = new ValueVo();
        valueVo.setValue(config.getValue());
        valueVo.setStatus(Constants.OK);

        return valueVo;
    }

    @Override
    public ValueVo getConfMetas(Long appId, Long envId, String version, String name ) {
        List<Config> configs = configDao.getConfigMetas(appId, envId, version, name);
        ValueVo valueVo = new ValueVo( );
        valueVo.setStatus(Constants.OK);
        valueVo.setValue(ConfigUtils.getMetasJson(configs));
        return valueVo;
    }

    @Override
    public ValueVo getConfigItemValues(Long appId, Long envId, String version) {
        List<Config> configs = configDao.getConfigItems(appId, envId, version);
        ValueVo valueVo = new ValueVo( );
        valueVo.setStatus(Constants.OK);
        valueVo.setValue(ConfigUtils.getValuesJson(configs));
        return valueVo;
    }

    @Override
    public ValueVo getAppTags(String app) {
        List<AppTag> appTags = appTagDao.findByAppName(app);

        ValueVo valueVo = new ValueVo();
        valueVo.setStatus(Constants.OK);

        if (!CollectionUtils.isEmpty(appTags)) {
            List<String> tagNames = Lists.newArrayList();
            appTags.forEach(appTag -> {
                tagNames.add(appTag.getTagName());
            });
            List<Tag> tagList = tagDao.findByTagNames(tagNames);
            if (!CollectionUtils.isEmpty(tagList)) {
                List<Map<String, String>> tagNameAndValues = Lists.newArrayList();
                tagList.forEach(tag -> {
                    Map<String, String> map = Maps.newHashMap();
                    map.put("tagName", tag.getTagName());
                    map.put("tagValue", tag.getTagValue());
                    tagNameAndValues.add(map);
                });
                valueVo.setValue(new Gson().toJson(tagNameAndValues));
            }
        }
        return valueVo;
    }
}
