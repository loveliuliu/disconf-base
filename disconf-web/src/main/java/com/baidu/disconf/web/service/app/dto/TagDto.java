/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.service.app.dto;

import com.baidu.disconf.core.common.utils.AesUtil;
import com.baidu.disconf.web.service.app.bo.Tag;

import org.apache.commons.lang.StringUtils;

/**
 * @author luoshiqian 2016/11/14 15:26
 */
public class TagDto extends Tag{


    private int usedAppNums = 0;

    private String decryptTagValue;

    private String appNames;//逗号分隔

    public int getUsedAppNums() {
        return usedAppNums;
    }

    public void setUsedAppNums(int usedAppNums) {
        this.usedAppNums = usedAppNums;
    }

    @Override
    public String getTagValue() {
        return super.getTagValue();
    }

    @Override
    public void setTagValue(String tagValue) {
        super.setTagValue(tagValue);
        try {
            if (StringUtils.isNotBlank(tagValue)) {
                decryptTagValue = AesUtil.decrypt(tagValue);
            }
        } catch (Exception e) {
        }
    }

    public String getDecryptTagValue() {
        
        return decryptTagValue;
    }

    public void setDecryptTagValue(String decryptTagValue) {
        this.decryptTagValue = decryptTagValue;
    }

    public String getAppNames() {
        return appNames;
    }

    public void setAppNames(String appNames) {
        this.appNames = appNames;
    }
}
