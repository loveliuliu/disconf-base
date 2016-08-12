package com.baidu.disconf.web.service.role.bo;

import java.util.HashMap;
import java.util.Map;

/**
 * 角色分配
 *
 * @author liaoqiqi
 * @version 2014-1-13
 */
public enum RoleEnum {

    NORMAL(1,"普通用户"), ADMIN(2,"管理员"), APP_ADMIN(3, "应用管理员");
    private static final Map<Integer, RoleEnum> intToEnum = new HashMap<Integer, RoleEnum>();

    static {
        for (RoleEnum roleEnum : values()) {
            intToEnum.put(roleEnum.value, roleEnum);
        }
    }


    private final int value;
    private final String name;
    RoleEnum(int value,String name) {
        this.value = value;
        this.name = name;
    }



    public static RoleEnum fromInt(int symbol) {
        return intToEnum.get(symbol);
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
