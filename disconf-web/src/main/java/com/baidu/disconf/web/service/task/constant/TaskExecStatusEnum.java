package com.baidu.disconf.web.service.task.constant;

/**
 * Created by luoshiqian on 2016/4/21.
 */
public enum TaskExecStatusEnum {

    init("init","初始化"),
    wait("wait","待执行"),
    done("done","已执行"),
    cancel("cancel","取消");

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
}
