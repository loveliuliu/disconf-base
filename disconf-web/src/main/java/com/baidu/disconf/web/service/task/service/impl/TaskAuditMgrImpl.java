/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.service.task.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baidu.disconf.web.service.task.bo.TaskAudit;
import com.baidu.disconf.web.service.task.constant.TaskAuditStatusEnum;
import com.baidu.disconf.web.service.task.dao.TaskAuditDao;
import com.baidu.disconf.web.service.task.service.TaskAuditMgr;
import com.baidu.disconf.web.service.task.vo.TaskAuditTypeEnum;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.disconf.web.utils.DateHelper;
import com.baidu.ub.common.commons.ThreadContext;

/**
 * @author luoshiqian 2016/11/28 16:38
 */
@Service
public class TaskAuditMgrImpl implements TaskAuditMgr {

    @Autowired
    private TaskAuditDao taskAuditDao;


    public void createTaskAudit(TaskAuditTypeEnum auditTypeEnum,Long taskId){

        Visitor visitor = ThreadContext.getSessionVisitor();
        Long userId = visitor.getId();

        TaskAudit taskAudit = new TaskAudit();
        taskAudit.setAuditType(auditTypeEnum.name());
        taskAudit.setTaskId(taskId);
        taskAudit.setAuditStatus(TaskAuditStatusEnum.waitAudit.getValue());

        taskAudit.setCreateUserId(userId);
        taskAudit.setCreateTime(DateHelper.now());

        taskAuditDao.create(taskAudit);

    }

}
