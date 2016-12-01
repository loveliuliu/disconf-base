/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.dsp.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author luoshiqian 2016/11/21 10:23
 */
public class PatternUtils {

    public static final Pattern MATCH_TAG_PATTEN = Pattern.compile("(\\$\\{[^}]*})");

    public static List<String> findMatchedTagNames(String str) {

        Matcher m = MATCH_TAG_PATTEN.matcher(str);

        List<String> result = new ArrayList<String>();
        while (m.find()) {
            String matchedTag = m.group();
            result.add(matchedTag.replace("${", "").replace("}", ""));
        }

        return result;
    }

}
