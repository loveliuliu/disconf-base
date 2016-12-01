/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.service.task.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.baidu.disconf.web.service.task.bo.TaskAudit;
import com.baidu.disconf.web.service.task.dao.TaskAuditDao;
import com.baidu.dsp.common.dao.AbstractDao;
import com.baidu.unbiz.common.genericdao.operator.Match;

@Repository
public class TaskAuditDaoImpl extends AbstractDao<Long, TaskAudit> implements TaskAuditDao{


    @Override
    public List<TaskAudit> findByTaskId(Long taskId) {
        return find(new Match("task_id",taskId));
    }
}
