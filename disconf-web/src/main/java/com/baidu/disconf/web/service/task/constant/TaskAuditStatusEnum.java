package com.baidu.disconf.web.service.task.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luoshiqian on 2016/4/21.
 */
public enum TaskAuditStatusEnum {

    waitAudit("wait_audit","待审核"),
    pass("pass","审核通过"),
    fail("fail","审核不通过"),
    cancel("cancel","已撤销");

    private static final Map<String,TaskAuditStatusEnum> maps = new HashMap<String,TaskAuditStatusEnum>();

    static {
        for(TaskAuditStatusEnum taskAuditStatusEnum:TaskAuditStatusEnum.values()){
            maps.put(taskAuditStatusEnum.getValue(),taskAuditStatusEnum);
        }
    }

    String value;
    String desc;

    TaskAuditStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }


    public static TaskAuditStatusEnum getByValue(String value){
        return maps.get(value);
    }
}
