package com.baidu.disconf.web.service.task.service.impl;

import com.baidu.disconf.web.common.Constants;
import com.baidu.disconf.web.service.config.service.ConfigDraftMgr;
import com.baidu.disconf.web.service.task.bo.Task;
import com.baidu.disconf.web.service.task.constant.TaskAuditStatusEnum;
import com.baidu.disconf.web.service.task.constant.TaskExecStatusEnum;
import com.baidu.disconf.web.service.task.dao.TaskDao;
import com.baidu.disconf.web.service.task.dao.TaskMapper;
import com.baidu.disconf.web.service.task.dto.TaskDto;
import com.baidu.disconf.web.service.task.service.TaskMgr;
import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.dao.UserDao;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.dsp.common.constant.DataFormatConstants;
import com.baidu.ub.common.commons.ThreadContext;
import com.github.knightliao.apollo.utils.time.DateUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by luoshiqian on 2016/4/23.
 */
@Service
public class TaskMgrImpl implements TaskMgr {

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ConfigDraftMgr configDraftMgr;

    @Override
    public Page<TaskDto> findMySubmitTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable) {

        return taskMapper.findMySubmitTask(task,pageable);
    }

    @Override
    public Page<TaskDto> findMyToDoTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable) {
        return taskMapper.findMyToDoTask(task,pageable);
    }

    @Override
    public Page<TaskDto> findMyDoneTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable) {
        return taskMapper.findMyDoneTask(task,pageable);
    }

    @Override
    public Page<TaskDto> findMyFinishedTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable) {
        return taskMapper.findMyFinishedTask(task,pageable);
    }

    @Override
    public void cancel(Long id) {
        taskDao.cancel(id);
    }

    @Override
    public void cancelExec(Long id) {
        taskDao.cancelExec(id);
    }

    @Override
    public TaskDto findById(Long id) {

        Task task = taskDao.get(id);

        User user = userDao.get(task.getCreateUserId());

        TaskDto taskDto = new TaskDto(task);
        taskDto.setCreateUserName(user.getName());

        if (null != task.getAuditUserId() && 0!=task.getAuditUserId().intValue()) {
            User auditUser = userDao.get(task.getAuditUserId());
            taskDto.setAuditUserName(auditUser.getName());
        }

        taskDto.getConfigDraftList().addAll(configDraftMgr.findByTaskId(id));

        return taskDto;
    }

    @Override
    public void taskAudit(Long id, String status, String auditComment) {

        Visitor visitor = ThreadContext.getSessionVisitor();
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);

        Task task = taskDao.get(id);

        task.setAuditStatus(status);
        task.setAuditComment(auditComment);
        task.setAuditTime(curTime);
        task.setAuditUserId(visitor.getId());

        switch (TaskAuditStatusEnum.getByValue(status)){
            case pass:
                task.setExecStatus(TaskExecStatusEnum.wait.getValue()); //审核通过 执行状态改为待执行
                break;
            case fail:
                //不通过不处理  还是在init状态
                break;
        }

        taskDao.update(task);

    }

    @Override
    public List<Task> findAuditingOrNotExecTask(Task task) {
        return taskMapper.findAuditingOrNotExecTask(task);
    }


    @Override
    public List<Task> findToBeActiveTask() {
        Task query = new Task();
        query.setAuditStatus(Constants.TASK_AUDIT_STATUS_PASS);
        query.setExecStatus(Constants.TASK_EXEC_STATUS_WAIT);
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        query.setExecTime(curTime);

        List<Task> result = taskMapper.findToBeActiveTask(query);
        return  result;
    }

    @Override
    public int updateTaskExecStatus(Task task) {
        return taskMapper.updateTaskExecStatusById(task);
    }
}
