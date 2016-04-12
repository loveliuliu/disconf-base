package com.baidu.disconf.client.test.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Spring的测试方法
 *
 * @author liaoqiqi
 * @version 2014-6-11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class BaseSpringTestCase {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseSpringTestCase.class);

    @Test
    public void pass() {

    }


}
