/*
 *
 * (C) Copyright 2017 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.client.common.update;


/**
 * 实现此接口，回调app变更
 */
public interface IDisconfAppUpdate {

    void notify(String value);
}
