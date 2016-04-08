package com.baidu.disconf.client.test.integration;

import org.springframework.stereotype.Component;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

@DisconfFile(fileName="app.properties")
@Component
public class AppConfig {
    
    
    private String config1;
    
    private int config2;

    @DisconfFileItem(name="config1")
    public String getConfig1() {
        return config1;
    }

    public void setConfig1(String config1) {
        this.config1 = config1;
    }

    @DisconfFileItem(name="config2")
    public int getConfig2() {
        return config2;
    }

    public void setConfig2(int config2) {
        this.config2 = config2;
    }
    
    

}
