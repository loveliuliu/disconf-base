package com.baidu.disconf.web.service.task.dao.impl;

import com.baidu.disconf.web.service.task.bo.Task;
import com.baidu.disconf.web.service.task.constant.TaskAuditStatusEnum;
import com.baidu.disconf.web.service.task.constant.TaskExecStatusEnum;
import com.baidu.disconf.web.service.task.dao.TaskDao;
import com.baidu.dsp.common.dao.AbstractDao;
import com.baidu.unbiz.common.genericdao.operator.Modify;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liaoqiqi
 * @version 2014-1-14
 */
@Repository
public class TaskDaoImpl extends AbstractDao<Long, Task> implements TaskDao {


    public void cancel(Long taskId) {

        List<Modify> modifyList = new ArrayList<Modify>();
        modifyList.add(modify("audit_status", TaskAuditStatusEnum.cancel.getValue()));

        update(modifyList, match("task_id", taskId));
    }

    public void cancelExec(Long taskId){

        List<Modify> modifyList = new ArrayList<Modify>();
        modifyList.add(modify("exec_status", TaskExecStatusEnum.cancel.getValue()));

        update(modifyList, match("task_id", taskId));
    }


}
