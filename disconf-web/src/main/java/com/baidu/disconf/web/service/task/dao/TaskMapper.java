package com.baidu.disconf.web.service.task.dao;


import com.baidu.disconf.web.service.config.bo.ConfigDraft;
import com.baidu.disconf.web.service.config.condition.ConfigDraftCondition;
import com.baidu.disconf.web.service.task.bo.Task;
import com.baidu.disconf.web.service.task.dto.TaskAuditDto;
import com.baidu.disconf.web.service.task.dto.TaskDto;
import com.ymatou.common.mybatis.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by luoshiqian on 2016/4/14.
 */
@MyBatisDao
public interface TaskMapper {

    Page<Task> findByTask(@Param("task") Task task, @Param("pageable") Pageable pageable);

    List<Task> findByTask(@Param("task") Task task);

    //我提交的任务
    Page<TaskDto> findMySubmitTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable);

    //我的待办任务
    Page<TaskDto> findMyToDoTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable);

    //我的已办任务
    Page<TaskDto> findMyDoneTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable);

    //已完成任务
    Page<TaskDto> findMyFinishedTask(@Param("task") TaskDto task, @Param("pageable") Pageable pageable);


    List<Task> findAuditingOrNotExecTask(@Param("task") Task task);

    List<Task> findToBeActiveTask(@Param("task") Task task);

    int updateTaskExecStatusById(@Param("task") Task task);

    List<TaskAuditDto> findTaskAuditDtoByTaskId(@Param("taskId") Long taskId);
}
