/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.sql.DataSource;

/**
 * @author luoshiqian 2016/11/7 18:56
 */
@EnableAutoConfiguration(
        exclude = {
                DataSourceAutoConfiguration.class, FreeMarkerAutoConfiguration.class, WebMvcAutoConfiguration.class
                ,DataSourceTransactionManagerAutoConfiguration.class
        })
@ImportResource("classpath:spring/applicationContext.xml")
public class Application {

    public static final Logger logger = LoggerFactory.getLogger(Application.class);

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

        ShutdownLatch shutdownLatch = new ShutdownLatch("disconf-web");
        try {
            shutdownLatch.await();
        } catch (Exception e) {
            logger.warn("shut down ", e);
        }
    }
}
