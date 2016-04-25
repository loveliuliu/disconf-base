package com.baidu.disconf.web.web.task.controller;

import com.baidu.disconf.web.service.task.bo.Task;
import com.baidu.disconf.web.service.task.dto.TaskDto;
import com.baidu.disconf.web.service.task.service.TaskMgr;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/web/task")
public class TaskController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskMgr taskMgr;


    //我提交的任务
    @RequestMapping(value = "/findMySubmitTask")
    @ResponseBody
    public JsonObjectBase findMySubmitTask(TaskDto task, Pageable pageable){

        Page<TaskDto> taskDtoPage = taskMgr.findMySubmitTask(task,pageable);

        return buildSuccess(taskDtoPage);
    }

    //我的待办任务
    @RequestMapping(value = "/findMyToDoTask")
    @ResponseBody
    public JsonObjectBase findMyToDoTask(TaskDto task, Pageable pageable){

        Page<TaskDto> taskDtoPage = taskMgr.findMyToDoTask(task,pageable);

        return buildSuccess(taskDtoPage);
    }

    //我的已办任务
    @RequestMapping(value = "/findMyDoneTask")
    @ResponseBody
    public JsonObjectBase findMyDoneTask(TaskDto task, Pageable pageable){

        Page<TaskDto> taskDtoPage = taskMgr.findMyDoneTask(task,pageable);

        return buildSuccess(taskDtoPage);
    }


    //已完成任务
    @RequestMapping(value = "/findMyFinishedTask")
    @ResponseBody
    public JsonObjectBase findMyFinishedTask(TaskDto task, Pageable pageable){

        Page<TaskDto> taskDtoPage = taskMgr.findMyFinishedTask(task,pageable);

        return buildSuccess(taskDtoPage);
    }

    @RequestMapping(value = "/cancel")
    @ResponseBody
    public JsonObjectBase cancel(Long id){

        taskMgr.cancel(id);

        return buildSuccess("撤销成功!");
    }


    @RequestMapping(value = "/cancelExec")
    @ResponseBody
    public JsonObjectBase cancelExec(Long id){

        taskMgr.cancelExec(id);

        return buildSuccess("撤销执行成功!");
    }

    @RequestMapping(value = "/taskConfigDetail")
    @ResponseBody
    public JsonObjectBase taskConfigDetail(Long id){

        TaskDto task = taskMgr.findById(id);

        return buildSuccess(task);
    }

    @RequestMapping(value = "/taskAudit")
    @ResponseBody
    public JsonObjectBase taskAudit(Long id,String status,String auditComment){

        taskMgr.taskAudit(id,status,auditComment);

        return buildSuccess("操作成功!");
    }
}
