package com.baidu.disconf.web.service.user.dto;

import com.baidu.disconf.web.service.role.bo.RoleEnum;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class UserDto {

    private static final long serialVersionUID = 1L;

    private Long id;
    // 唯一
    private String name;

    // token
    private String token;


    private String ownApps;

    /**
     * 角色ID
     */
    private int roleId;
    private String roleName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getOwnApps() {
        return ownApps;
    }

    public void setOwnApps(String ownApps) {
        this.ownApps = ownApps;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return RoleEnum.fromInt(roleId).getName();
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
