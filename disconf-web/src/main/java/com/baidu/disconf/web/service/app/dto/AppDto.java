package com.baidu.disconf.web.service.app.dto;

import com.baidu.disconf.web.service.app.bo.App;

/**
 * Created by luoshiqian on 2016/4/18.
 */
public class AppDto extends App{


    private String belongUserIds;
    private String belongUserNames;


    public String getBelongUserIds() {
        return belongUserIds;
    }

    public void setBelongUserIds(String belongUserIds) {
        this.belongUserIds = belongUserIds;
    }

    public String getBelongUserNames() {
        return belongUserNames;
    }

    public void setBelongUserNames(String belongUserNames) {
        this.belongUserNames = belongUserNames;
    }
}
