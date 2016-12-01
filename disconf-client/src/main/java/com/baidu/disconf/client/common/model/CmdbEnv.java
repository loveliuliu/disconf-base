/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.client.common.model;

/**
 * @author luoshiqian 2016/11/23 18:28
 */
public class CmdbEnv {
    private int code;
    private String environment;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @Override
    public String toString() {
        return "CmdbEnv{" +
                "code=" + code +
                ", environment='" + environment + '\'' +
                '}';
    }
}
