package com.baidu.disconf.client.test.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/integration/applicationContext.xml"})
public class StartUpTest {
    
    @Test
    public void testStartUp( ) {
        
    }

}
