package com.ymatou.disconf.client.test.ext.unittestmode;

import org.springframework.stereotype.Component;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

@Component
@DisconfFile(fileName="unitTest.properties")
public class AppConfig {
    
    
    private int config;

    @DisconfFileItem(name="config")
    public int getConfig() {
        return config;
    }

    public void setConfig(int config) {
        this.config = config;
    }
    
}
