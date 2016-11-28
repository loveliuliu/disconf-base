package com.baidu.disconf.web.web.config.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baidu.disconf.web.service.app.bo.App;
import com.baidu.disconf.web.service.app.service.AppMgr;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.form.ConfCopyForm;
import com.baidu.disconf.web.service.config.form.ConfNewForm;
import com.baidu.disconf.web.service.config.service.ConfigFetchMgr;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.service.env.bo.Env;
import com.baidu.disconf.web.service.env.service.EnvMgr;
import com.baidu.disconf.web.service.user.service.AuthMgr;
import com.baidu.dsp.common.exception.FieldException;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Service
public class ConfigValidator {

    @Autowired
    private AppMgr appMgr;

    @Autowired
    private EnvMgr envMgr;

    @Autowired
    private ConfigMgr configMgr;

    @Autowired
    private ConfigFetchMgr configFetchMgr;

    @Autowired
    private AuthMgr authMgr;
    /**
     * 校验
     *
     * @param id
     *
     * @return
     */
    public Config valideConfigExist(Long id) {

        //
        // config
        //
        Config config = configMgr.getConfigById(id);
        if (config == null) {
            throw new FieldException("configId", "config.id.not.exist", null);
        }

        //
        // validate app
        //
        validateAppAuth(config.getAppId());

        return config;
    }




    /**
     * 判断配置是否更新
     *
     * @return
     */
    public boolean isValueUpdate(Long configId, String newValue) {

        //
        // 判断值有没有更新
        //
        String oldValue = configMgr.getValue(configId);

        if (newValue.equals(oldValue)) {
            return false;
        }
        return true;
    }

    /**
     * @param appId
     */
    private void validateAppAuth(long appId) {

        boolean ret = authMgr.verifyApp4CurrentUser(appId);
        if (ret == false) {
            throw new FieldException(ConfNewForm.APPID, "app.auth.noright", null);
        }

    }



    /**
     * 校验复制 配置
     *
     * @param confCopyForm
     */
    public void validateCopy(ConfCopyForm confCopyForm) {

        //
        // app
        //
        App app = appMgr.getById(confCopyForm.getAppId());
        if (app == null) {
            throw new FieldException(confCopyForm.APPID, "app.not.exist", null);
        }

        //
        validateAppAuth(app.getId());

        //
        // env
        //
        Env env = envMgr.getById(confCopyForm.getEnvId());
        if (env == null) {
            throw new FieldException(confCopyForm.ENVID, "env.not.exist", null);
        }

        //校验复制同环境同版本
        if(confCopyForm.getEnvId().longValue() == confCopyForm.getNewEnvId().longValue() &&
                confCopyForm.getVersion().equals(confCopyForm.getNewVersion())
                ){
            throw new FieldException(confCopyForm.ENVID, "env.version.copy.same", null);
        }

    }


}
