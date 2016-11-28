/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.config;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luoshiqian 2016/11/7 19:22
 */
@Configuration
public class ServletConfig extends SpringBootServletInitializer {

    @Bean
    public ServletRegistrationBean dispatcherServletRegistration() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();

        ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
        Map<String,String> params = new HashMap<String,String>();
        params.put("contextConfigLocation","classpath*:spring/spring-servlet.xml");
        registration.setInitParameters(params);
        return registration;
    }
}
