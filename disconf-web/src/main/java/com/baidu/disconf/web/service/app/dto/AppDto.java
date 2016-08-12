package com.baidu.disconf.web.service.app.dto;

import com.baidu.disconf.web.service.app.bo.App;

/**
 * Created by luoshiqian on 2016/4/18.
 */
public class AppDto extends App{


    private String belongUserIds;
    private String belongUserNames;
    private Long userId;
    private String roleId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
