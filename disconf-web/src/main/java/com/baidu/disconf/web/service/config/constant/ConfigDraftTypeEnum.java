package com.baidu.disconf.web.service.config.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luoshiqian on 2016/4/20.
 */
public enum ConfigDraftTypeEnum {

    create("create","新建"),modify("modify","修改"),delete("delete","删除");

    private String value;
    private String name;

    private static final Map<String,ConfigDraftTypeEnum> maps = new HashMap<String,ConfigDraftTypeEnum>();

    static {
        for(ConfigDraftTypeEnum configDraftTypeEnum:ConfigDraftTypeEnum.values()){
            maps.put(configDraftTypeEnum.getValue(),configDraftTypeEnum);
        }
    }

    ConfigDraftTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static ConfigDraftTypeEnum getByValue(String value){
        return maps.get(value);
    }
}
