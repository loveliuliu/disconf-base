package com.ymatou.disconf.client.test.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NonAnotationConfig2 {
    
    @Value("${config4}")
    private String config4;
    
    @Value("${config5}")
    private int config5;
    
    @Value("${config6}")
    private String config6;

    public String getConfig4() {
        return config4;
    }

    public void setConfig4(String config4) {
        this.config4 = config4;
    }

    public int getConfig5() {
        return config5;
    }

    public void setConfig5(int config5) {
        this.config5 = config5;
    }

    public String getConfig6() {
        return config6;
    }

    public void setConfig6(String config6) {
        this.config6 = config6;
    }


    

    
    
}
