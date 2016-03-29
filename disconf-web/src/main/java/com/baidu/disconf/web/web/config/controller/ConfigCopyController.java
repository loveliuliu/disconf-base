package com.baidu.disconf.web.web.config.controller;

import com.baidu.disconf.web.service.config.form.ConfCopyForm;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.web.config.validator.ConfigValidator;
import com.baidu.dsp.common.annotation.NoAuth;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * 专用于配置新建
 *
 * @author liaoqiqi
 * @version 2014-6-24
 */
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/web/config")
public class ConfigCopyController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigCopyController.class);

    @Autowired
    private ConfigMgr configMgr;
    @Autowired
    private ConfigValidator configValidator;

    /**
     * 复制配置
     *
     * @param confCopyForm
     *
     * @return
     */
    @NoAuth
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    @ResponseBody
    public JsonObjectBase copy(@Valid ConfCopyForm confCopyForm) {

        // 业务校验
        configValidator.validateCopy(confCopyForm);


        configMgr.copyConfig(confCopyForm);

        return buildSuccess("复制配置成功");
    }

    @NoAuth
    @RequestMapping(value = "/isEnvAndVersionExist", method = RequestMethod.POST)
    @ResponseBody
    public JsonObjectBase isEnvAndVersionExist(ConfCopyForm confCopyForm){


        return buildSuccess(configMgr.isEnvAndVersionExist(confCopyForm));

    }
}