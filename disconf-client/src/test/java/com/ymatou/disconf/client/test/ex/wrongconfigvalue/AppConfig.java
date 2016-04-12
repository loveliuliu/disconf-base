package com.ymatou.disconf.client.test.ex.wrongconfigvalue;

import org.springframework.stereotype.Component;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

@DisconfFile(fileName="wrongConfigValue.properties")
@Component("yyyyy")
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
