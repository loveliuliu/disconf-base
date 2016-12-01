/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.ymatou.disconf.client.test.tag;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;
import org.springframework.stereotype.Component;

@DisconfFile(fileName="app.properties")
@Component
public class AppConfig {
    
    
    private String config1;
    
    private int config2;

    private String config3;

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

    @DisconfFileItem(name="config3")
    public String getConfig3() {
        return config3;
    }

    public void setConfig3(String config3) {
        this.config3 = config3;
    }

    public void setConfig2(int config2) {
        this.config2 = config2;
    }
    
    

}
