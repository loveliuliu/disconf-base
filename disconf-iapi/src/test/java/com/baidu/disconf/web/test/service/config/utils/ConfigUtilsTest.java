package com.baidu.disconf.web.test.service.config.utils;


import java.util.Arrays;

import org.junit.Test;

import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.utils.ConfigUtils;

/**
 * 
 * @author tuwenjie
 *
 */
public class ConfigUtilsTest {

    @Test
    public void testMetasJson() {
        Config c1 = new Config( );
        c1.setName("c1");
        c1.setType(1);
        c1.setUpdateTime("20140909123020");
        
        
        Config c2 = new Config( );
        c2.setName("c2");
        c2.setType(0);
        c2.setUpdateTime("20150909123020");
        
        Config c3 = new Config( );

        System.out.println(ConfigUtils.getMetasJson(Arrays.asList(c1, c2, c3)));
    }
    
    
    @Test
    public void testValuesJson() {
        Config c1 = new Config( );
        c1.setName("c1");
        c1.setValue("c1Value");
        
        
        Config c2 = new Config( );
        c2.setName("c2");
        c2.setValue("c2Value");
        
        Config c3 = new Config( );

        System.out.println(ConfigUtils.getValuesJson(Arrays.asList(c1, c2, c3)));
    }

}
