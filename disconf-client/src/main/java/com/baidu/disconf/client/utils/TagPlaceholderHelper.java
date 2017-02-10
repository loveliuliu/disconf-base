/*
 *
 * (C) Copyright 2017 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.client.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 * @author luoshiqian 2017/2/10 10:56
 */
public class TagPlaceholderHelper {

    private static final PropertyPlaceholderHelper propertyPlaceholderHelper =
            new PropertyPlaceholderHelper("${", "}", null, false);
    private static final MapPlaceholderConfigurerResolver resolver = new MapPlaceholderConfigurerResolver();


    public static String replaceTag(String value) {

        if (StringUtils.isNotBlank(value)) {
            return propertyPlaceholderHelper.replacePlaceholders(value, resolver);
        }
        return value;

    }

    public static class MapPlaceholderConfigurerResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
        @Override
        public String resolvePlaceholder(String placeholderName) {
            return AppTagHelper.TAG_STORE.get(placeholderName);
        }
    }
}
