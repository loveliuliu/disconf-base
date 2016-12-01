package com.baidu.disconf.web.service.task.service.impl;

import com.baidu.disconf.web.common.Constants;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.service.ConfigDraftMgr;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.service.task.bo.Task;
import com.baidu.disconf.web.service.task.bo.TaskAudit;
import com.baidu.disconf.web.service.task.constant.TaskAuditStatusEnum;
import com.baidu.disconf.web.service.task.constant.TaskExecStatusEnum;
import com.baidu.disconf.web.service.task.dao.TaskAuditDao;
import com.baidu.disconf.web.service.task.dao.TaskDao;
import com.baidu.disconf.web.service.task.dao.TaskMapper;
import com.baidu.disconf.web.service.task.dto.TaskAuditDto;
import com.baidu.disconf.web.service.task.dto.TaskDto;
import com.baidu.disconf.web.service.task.service.TaskMgr;
import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.dao.UserDao;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.disconf.web.utils.DateHelper;
import com.baidu.dsp.common.constant.DataFormatConstants;
import com.baidu.ub.common.commons.ThreadContext;
import com.github.knightliao.apollo.utils.time.DateUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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

    @Autowired
    private TaskAuditDao taskAuditDao;

    @Autowired
    private TaskExecutor proExecutor;

    @Autowired
    private ConfigMgr configMgr;

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
    @Transactional
    public Task taskAudit(Long id,Long taskAuditId, String status, String auditComment) {
        Task task = taskDao.get(id);
        if(task.getAuditStatus().equals(TaskAuditStatusEnum.fail.getValue())){
            throw new IllegalStateException("此任务已被审核不通过，不能再次审核!");
        }
        Visitor visitor = ThreadContext.getSessionVisitor();
        String curTime = DateHelper.now();

        //将taskaudit更新
        TaskAudit taskAudit = taskAuditDao.get(taskAuditId);
        taskAudit.setAuditStatus(status);
        taskAudit.setAuditTime(curTime);
        taskAudit.setAuditComment(auditComment);
        taskAudit.setAuditUserId(visitor.getId());
        taskAuditDao.update(taskAudit);

        //每次审核都更新 当做最后审核人
        task.setAuditComment(auditComment);
        task.setAuditTime(curTime);
        task.setAuditUserId(visitor.getId());

        List<TaskAudit> taskAuditList = taskAuditDao.findByTaskId(id);

        TaskAuditStatusEnum auditStatusEnum = TaskAuditStatusEnum.getByValue(status);

        if(auditStatusEnum == TaskAuditStatusEnum.pass){
            //审核通过，如果所有都通过了，则设置执行状态为待执行
            if(taskAuditList.stream().allMatch(t -> t.getAuditStatus().equals(TaskAuditStatusEnum.pass.getValue()))){
                task.setAuditStatus(TaskAuditStatusEnum.pass.getValue());
                task.setExecStatus(TaskExecStatusEnum.wait.getValue()); //审核通过 执行状态改为待执行
            }
        }else {
            task.setAuditStatus(TaskAuditStatusEnum.fail.getValue());
        }
        taskDao.update(task);

        return task;
    }

    public void decideExecTask(Task task){
        //如果审核通过，并且是马上执行、直接调用执行
        if(task.getAuditStatus().equals(TaskAuditStatusEnum.pass.getValue()) && task.getExecTime().equals("now")){
            proExecutor.execute(() -> {
                List<Config> configList = configDraftMgr.draftToConfig(task);
                //同步zk
                if(!CollectionUtils.isEmpty(configList)){
                    for(Config config : configList){
                        configMgr.notifyZookeeper(config.getId());
                    }
                }
            });
        }
    }

    @Override
    public void systemAutoAuditPass(Long id) {
        List<TaskAudit> taskAuditList = taskAuditDao.findByTaskId(id);
        Task task = null;
        for (TaskAudit taskAudit:taskAuditList){
            task = this.taskAudit(id,taskAudit.getId(),TaskAuditStatusEnum.pass.getValue(),"线下环境系统自动审核通过!");
        }
        decideExecTask(task);
    }

    @Override
    public List<Task> findAuditingOrNotExecTask(Task task) {
        return taskMapper.findAuditingOrNotExecTask(task);
    }


    @Override
    public List<Task> findToBeActiveTask() {
        Task query = new Task();
        query.setAuditStatus(TaskAuditStatusEnum.pass.getValue());
        query.setExecStatus(TaskExecStatusEnum.wait.getValue());
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        query.setExecTime(curTime);

        List<Task> result = taskMapper.findToBeActiveTask(query);
        return  result;
    }

    @Override
    public int updateTaskExecStatus(Task task) {
        return taskMapper.updateTaskExecStatusById(task);
    }

    @Override
    public List<TaskAuditDto> findTaskAuditDtoByTaskId(Long taskId) {
        return taskMapper.findTaskAuditDtoByTaskId(taskId);
    }
}
