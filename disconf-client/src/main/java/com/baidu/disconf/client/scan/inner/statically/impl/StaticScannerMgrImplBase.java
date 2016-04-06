package com.baidu.disconf.client.scan.inner.statically.impl;

import com.baidu.disconf.client.common.model.DisConfCommonModel;
import com.baidu.disconf.client.config.DisClientConfig;

/**
 * @author liaoqiqi
 * @version 2014-9-9
 */
public class StaticScannerMgrImplBase {

    /**
     * env/version 默认是应用整合设置的，但用户可以在配置中更改它
     */
    protected static DisConfCommonModel makeDisConfCommonModel( ) {

        DisConfCommonModel disConfCommonModel = new DisConfCommonModel();

        // app
        disConfCommonModel.setApp(DisClientConfig.getInstance().APP);

        //env
        disConfCommonModel.setEnv(DisClientConfig.getInstance().ENV);

        //version
        disConfCommonModel.setVersion(DisClientConfig.getInstance().VERSION);

        return disConfCommonModel;
    }

}
