package com.baidu.disconf.web.service.task.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luoshiqian on 2016/4/21.
 */
public enum TaskExecStatusEnum {

    init("init","初始化"),
    wait("wait","待执行"),
    done("done","已执行"),
    cancel("cancel","已撤销");

    private static final Map<String,TaskExecStatusEnum> maps = new HashMap<String,TaskExecStatusEnum>();

    static {
        for(TaskExecStatusEnum taskExecStatusEnum:TaskExecStatusEnum.values()){
            maps.put(taskExecStatusEnum.getValue(),taskExecStatusEnum);
        }
    }

    String value;
    String desc;


    TaskExecStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static TaskExecStatusEnum getByValue(String value){
        return maps.get(value);
    }
}
