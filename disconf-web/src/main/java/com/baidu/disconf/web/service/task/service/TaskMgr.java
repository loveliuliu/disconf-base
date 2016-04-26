package com.baidu.disconf.web.service.task.service;

import com.baidu.disconf.web.service.config.bo.ConfigDraft;
import com.baidu.disconf.web.service.config.form.ConfDraftSubmitForm;
import com.baidu.disconf.web.service.task.bo.Task;
import com.baidu.disconf.web.service.task.dto.TaskDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by luoshiqian on 2016/4/23.
 */
public interface TaskMgr {

    //我提交的任务
    Page<TaskDto> findMySubmitTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable);

    //我的待办任务
    Page<TaskDto> findMyToDoTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable);

    //我的已办任务
    Page<TaskDto> findMyDoneTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable);

    //已完成任务
    Page<TaskDto> findMyFinishedTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable);


    //撤销任务
    void cancel(Long id);

    //撤销任务执行
    void cancelExec(Long id);

    TaskDto findById(Long id);

    void taskAudit(Long id,String status,String auditComment);


    List<Task> findAuditingOrNotExecTask(Task task);

    /**
     *
     * @return
     */
    List<Task> findToBeActiveTask();

    /**
     *
     * @param task
     * @return
     */
    int updateTaskExecStatus(Task task);
}
