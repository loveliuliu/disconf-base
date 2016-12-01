/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.utils;

import com.baidu.dsp.common.constant.DataFormatConstants;
import com.github.knightliao.apollo.utils.time.DateUtils;

import java.util.Date;

/**
 * @author luoshiqian 2016/11/14 16:17
 */
public class DateHelper {

    public static String now(){
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        return curTime;
    }
}
