/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.service.task.service;

import com.baidu.disconf.web.service.task.vo.TaskAuditTypeEnum;

/**
 * @author luoshiqian 2016/11/28 16:38
 */
public interface TaskAuditMgr {

    void createTaskAudit(TaskAuditTypeEnum auditTypeEnum, Long taskId);

}
