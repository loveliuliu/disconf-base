package com.baidu.disconf.web.web.config.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.core.common.json.ValueVo;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.form.ConfForm;
import com.baidu.disconf.web.service.config.service.ConfigFetchMgr;
import com.baidu.disconf.web.service.config.utils.ConfigUtils;
import com.baidu.disconf.web.web.config.dto.ConfigFullModel;
import com.baidu.disconf.web.web.config.validator.ConfigValidator4Fetch;
import com.baidu.dsp.common.annotation.NoAuth;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.exception.DocumentNotFoundException;

/**
 * 配置获取Controller, Disconf-client专门使用的
 *
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/config")
public class ConfigFetcherController {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigFetcherController.class);

    @Autowired
    private ConfigValidator4Fetch configValidator4Fetch;

    @Autowired
    private ConfigFetchMgr configFetchMgr;

    /**
     * 获取配置项 Item
     *
     * @param confForm
     *
     * @return
     */
    @NoAuth
    @RequestMapping(value = "/item", method = RequestMethod.GET)
    @ResponseBody
    public ValueVo getItem(ConfForm confForm) {

        LOG.info(confForm.toString());

        //
        // 校验
        //
        ConfigFullModel configModel = null;
        try {
            configModel = configValidator4Fetch.verifyConfForm(confForm);
        } catch (Exception e) {
            LOG.error(e.toString());
            return ConfigUtils.getErrorVo(e.getMessage());
        }

        return configFetchMgr.getConfItemByParameter(configModel.getApp().getId(), configModel.getEnv().getId(),
                configModel.getVersion(), configModel.getKey());
    }

    /**
     * 获取配置文件
     *
     * @return
     */
    @NoAuth
    @RequestMapping(value = "/file", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> getFile(ConfForm confForm) {

        boolean hasError = false;

        //
        // 校验
        //
        ConfigFullModel configModel = null;
        try {
            configModel = configValidator4Fetch.verifyConfForm(confForm);
        } catch (Exception e) {
            LOG.error(e.toString());
            hasError = true;
        }

        if (hasError == false) {
            try {
                //
                Config config = configFetchMgr
                        .getConfByParameter(configModel.getApp().getId(), configModel.getEnv().getId(),
                                configModel.getVersion(), configModel.getKey(),
                                DisConfigTypeEnum.FILE);
                if (config == null) {
                    hasError = true;
                    throw new DocumentNotFoundException(configModel.getKey());
                }

                return downloadDspBill(configModel.getKey(), config.getValue());

            } catch (Exception e) {
                LOG.error(e.toString());
            }
        }

        if (confForm.getKey() != null) {
            throw new DocumentNotFoundException(confForm.getKey());
        } else {
            throw new DocumentNotFoundException("");
        }
    }

    /**
     * 下载
     *
     * @param fileName
     *
     * @return
     */
    public HttpEntity<byte[]> downloadDspBill(String fileName, String value) {

        HttpHeaders header = new HttpHeaders();
        byte[] res = value.getBytes();

        String name = null;

        try {
            name = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        header.set("Content-Disposition", "attachment; filename=" + name);
        header.setContentLength(res.length);
        return new HttpEntity<byte[]>(res, header);
    }
    
    /**
     * 获取所有配置或指定Key的Meta
     * 当前仅.NET客户端使用
     * 返回的ValueVo的value是一个json, 示例：
     * [{"upateTime":"20140909123020","name":"c1","type":"1"},{"upateTime":"20150909123020","name":"c2","type":"0"}]
     * 
     * @param confForm
     */
    @NoAuth
    @RequestMapping(value = "/metas", method = RequestMethod.GET)
    @ResponseBody
    public ValueVo getConfigMetas( ConfForm confForm ) {
        LOG.info(confForm.toString());

        //
        // 校验
        //
        ConfigFullModel configModel = null;
        try {
            configModel = configValidator4Fetch.verifyConfFormIgnoreKeyEmpty(confForm);
        } catch (Exception e) {
            LOG.error(e.toString());
            return ConfigUtils.getErrorVo(e.getMessage());
        }

        return configFetchMgr.getConfMetas(configModel.getApp().getId(), configModel.getEnv().getId(), configModel.getVersion(), configModel.getKey());
    }
    
    
    /**
     * 获取所有配置项的值(不包含配置文件)
     * 当前仅.NET客户端使用
     * 返回的ValueVo的value是一个json, 示例：
     * [{"name":"c1","value":"c1Value"},{"name":"c2","value":"c2Value"}]
     * 
     * @param confForm
     */
    @NoAuth
    @RequestMapping(value = "/item/values", method = RequestMethod.GET)
    @ResponseBody
    public ValueVo getConfigItemValues(ConfForm confForm ) {
        LOG.info(confForm.toString());

        //
        // 校验
        //
        ConfigFullModel configModel = null;
        try {
            configModel = configValidator4Fetch.verifyConfFormIgnoreKeyEmpty(confForm);
        } catch (Exception e) {
            LOG.error(e.toString());
            return ConfigUtils.getErrorVo(e.getMessage());
        }

        return configFetchMgr.getConfigItemValues(configModel.getApp().getId(), configModel.getEnv().getId(), configModel.getVersion());
    }

}
