package com.baidu.disconf.web.service.task.constant;

/**
 * Created by luoshiqian on 2016/4/21.
 */
public enum TaskAuditStatusEnum {

    waitAudit("wait_audit","待审核"),
    pass("pass","审核通过"),
    fail("fail","审核不通过"),
    cancel("cancel","取消");

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
}
