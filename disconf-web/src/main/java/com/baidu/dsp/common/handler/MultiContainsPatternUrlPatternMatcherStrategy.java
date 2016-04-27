package com.baidu.dsp.common.handler;

import org.jasig.cas.client.authentication.UrlPatternMatcherStrategy;

/**
 * Created by qianmin on 2016/4/27.
 */
public class MultiContainsPatternUrlPatternMatcherStrategy implements UrlPatternMatcherStrategy {

    private String[] patternList;

    @Override
    public boolean matches(String url) {
        for(String pattern : patternList){
           if(url.contains(pattern)){
               return true;
           }
        }
        return false;
    }

    @Override
    public void setPattern(String pattern) {
        this.patternList = pattern.split(";");
    }
}
