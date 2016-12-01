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

package com.ymatou.disconf.client.test.tag;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author luoshiqian 2016/11/25 14:45
 */
@Component
public class SpringErrorComp2 implements InitializingBean{


    @Override
    public void afterPropertiesSet() throws Exception {
//        throw new RuntimeException("test error");
    }
}
