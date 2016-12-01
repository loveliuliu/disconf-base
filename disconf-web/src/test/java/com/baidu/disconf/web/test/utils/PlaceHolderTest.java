/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.test.utils;

import java.util.Properties;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.SimpleIdGenerator;

/**
 * @author luoshiqian 2016/11/10 16:12
 */
public class PlaceHolderTest {

    @Test
    public void test() {

        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}$", ":", true);
        Properties properties = new Properties();
        properties.setProperty("test", "test123");
        properties.setProperty("test2", "test1234");

        System.out.println(helper.replacePlaceholders("abcdefg${test}$,${test2}$hjklmn,${test3:three}$hjklmn",
                new PropertyPlaceholderConfigurerResolver(properties)));


        SimpleIdGenerator simpleIdGenerator = new SimpleIdGenerator();

        System.out.println(simpleIdGenerator.generateId().toString());
        System.out.println(simpleIdGenerator.generateId().toString());
        System.out.println(simpleIdGenerator.generateId().toString());
        System.out.println(simpleIdGenerator.generateId().toString());
    }

    private class PropertyPlaceholderConfigurerResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private final Properties props;

        private PropertyPlaceholderConfigurerResolver(Properties props) {
            this.props = props;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            return props.getProperty(placeholderName);
        }
    }
}
