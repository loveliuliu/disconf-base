package com.baidu.disconf.web.service.task.dto;

import com.baidu.disconf.web.service.config.bo.ConfigDraft;
import com.baidu.disconf.web.service.task.bo.Task;
import com.baidu.disconf.web.service.task.constant.TaskAuditStatusEnum;
import com.baidu.disconf.web.service.task.constant.TaskExecStatusEnum;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.ub.common.commons.ThreadContext;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by luoshiqian on 2016/4/22.
 */
public class TaskDto extends Task{

    private String createUserName;
    private String auditStatusStr;//审核状态
    private String execStatusStr;//执行状态

    private String formatExecTime;//格式化后的生效时间

    private String curAuditUserIds;//当前审核人员
    private String curAuditUserNames;

    private String auditUserName;//审核人员

    private Long curUserId;//当前登录人员

    private List<ConfigDraft> configDraftList =Lists.newArrayList();

    public TaskDto(){

    }
    public TaskDto(Task task){
        BeanUtils.copyProperties(task,this);
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public Long getCurUserId() {
        if(curUserId == null){
            Visitor visitor = ThreadContext.getSessionVisitor();
            curUserId = visitor.getId();
        }
        return curUserId;
    }

    public List<ConfigDraft> getConfigDraftList() {
        return configDraftList;
    }

    public void setConfigDraftList(List<ConfigDraft> configDraftList) {
        this.configDraftList = configDraftList;
    }

    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getAuditStatusStr() {
        String auditStatus = getAuditStatus();
        if(!StringUtils.isEmpty(auditStatus)){
            auditStatusStr = TaskAuditStatusEnum.getByValue(auditStatus).getDesc();
        }
        return auditStatusStr;
    }

    public void setAuditStatusStr(String auditStatusStr) {
        this.auditStatusStr = auditStatusStr;
    }

    public String getExecStatusStr() {
        String execStatus = getExecStatus();
        if(!StringUtils.isEmpty(execStatus)){
            execStatusStr = TaskExecStatusEnum.getByValue(execStatus).getDesc();
        }
        return execStatusStr;
    }

    public void setExecStatusStr(String execStatusStr) {
        this.execStatusStr = execStatusStr;
    }

    public String getFormatExecTime() {
        return formatExecTime;
    }

    public void setFormatExecTime(String formatExecTime) {
        this.formatExecTime = formatExecTime;
    }

    public String getCurAuditUserIds() {
        return curAuditUserIds;
    }

    public void setCurAuditUserIds(String curAuditUserIds) {
        this.curAuditUserIds = curAuditUserIds;
    }

    public String getCurAuditUserNames() {
        return curAuditUserNames;
    }

    public void setCurAuditUserNames(String curAuditUserNames) {
        this.curAuditUserNames = curAuditUserNames;
    }
}
