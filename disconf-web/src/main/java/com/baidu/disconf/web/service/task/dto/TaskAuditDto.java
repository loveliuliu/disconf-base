/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.service.task.dto;

import com.baidu.disconf.web.service.task.bo.TaskAudit;
import com.baidu.disconf.web.service.task.constant.TaskAuditStatusEnum;
import org.springframework.util.StringUtils;

/**
 * Created by luoshiqian on 2016/4/22.
 */
public class TaskAuditDto extends TaskAudit {


    private String auditStatusStr;// 审核状态
    private String auditUserName;// 审核人员

    public String getAuditStatusStr() {

        String auditStatus = getAuditStatus();
        if(!StringUtils.isEmpty(auditStatus)){
            auditStatusStr = TaskAuditStatusEnum.getByValue(auditStatus).getDesc();
        }
        return auditStatusStr;
    }

    public void setAuditStatusStr(String auditStatusStr) {
        this.auditStatusStr = auditStatusStr;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }
}
