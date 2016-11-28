/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.config;

import com.google.common.collect.Lists;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class ApplicationFilter {

    /**
     * 编码配置
     * @return
     */
    @Bean
    public FilterRegistrationBean encodingFilter() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("utf-8");
        encodingFilter.setForceEncoding(true);
        FilterRegistrationBean mappingEncodingFilter = new FilterRegistrationBean(encodingFilter);

        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/*");
        mappingEncodingFilter.setUrlPatterns(urlPatterns);
        mappingEncodingFilter.setOrder(1);

        return mappingEncodingFilter;
    }

    @Bean
    public FilterRegistrationBean httpPutFormContentFilter(){
        HttpPutFormContentFilter httpPutFormContentFilter = new HttpPutFormContentFilter();

        FilterRegistrationBean mappingEncodingFilter = new FilterRegistrationBean(httpPutFormContentFilter);
        mappingEncodingFilter.setUrlPatterns(Lists.newArrayList("/*"));
        return mappingEncodingFilter;
    }


}
