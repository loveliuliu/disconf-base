/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.service.task.dao;

import com.baidu.disconf.web.service.task.bo.TaskAudit;
import com.baidu.unbiz.common.genericdao.dao.BaseDao;

import java.util.List;

public interface TaskAuditDao extends BaseDao<Long, TaskAudit> {

    List<TaskAudit> findByTaskId(Long taskId);

}
