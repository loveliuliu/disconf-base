package com.ymatou.disconf.client.test.integration;

import org.junit.Test;

public class SystemTest {
    
    @Test
    public void test( ) {
       System.out.println(System.getProperties().contains("test")); 
       System.out.println(System.getProperty("test1")); 
      
    }

}
