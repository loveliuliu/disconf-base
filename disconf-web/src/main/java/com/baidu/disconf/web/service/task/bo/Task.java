package com.baidu.disconf.web.service.task.bo;

import com.baidu.disconf.web.service.task.constant.TaskAuditStatusEnum;
import com.baidu.disconf.web.service.task.constant.TaskExecStatusEnum;
import com.baidu.dsp.common.dao.Columns;
import com.baidu.dsp.common.dao.DB;
import com.baidu.unbiz.common.genericdao.annotation.Column;
import com.baidu.unbiz.common.genericdao.annotation.Table;
import com.github.knightliao.apollo.db.bo.BaseObject;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by luoshiqian on 2016/4/18.
 */
@Table(db = DB.DB_NAME, name = "task", keyColumn = Columns.TASK_ID)
public class Task extends BaseObject<Long> {

    @Column(value = "app_id")
    private Long appId;
    @Column(value = "app_name")
    private String appName;
    @Column(value = "env_id")
    private Long envId;
    @Column(value = "env_name")
    private String envName;
    @Column(value = "version")
    private String version;
    //任务状态(待审核 wait_audit,通过 pass,不通过 fail,取消 cancel)
    @Column(value = "audit_status")
    private String auditStatus;
    @Column(value = "create_user_id")
    private Long createUserId;
    @Column(value = "create_time")
    private String createTime;
    @Column(value = "audit_user_id")
    private Long auditUserId;
    @Column(value = "audit_time")
    private String auditTime;
    @Column(value = "audit_comment")
    private String auditComment;
    @Column(value = "exec_time")
    private String execTime;
    //执行状态(未执行 wait,已执行 done,已取消 cancel)
    @Column(value = "exec_status")
    private String execStatus;
    @Column(value = "memo")
    private String memo;

    public Task() {
    }

    public Task(Long appId, String appName, Long envId, String envName, String version, Long createUserId) {
        this.appId = appId;
        this.appName = appName;
        this.envId = envId;
        this.envName = envName;
        this.version = version;
        this.createUserId = createUserId;
        this.auditStatus = TaskAuditStatusEnum.waitAudit.getValue();
        this.execStatus = TaskExecStatusEnum.init.getValue();
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditComment() {
        return auditComment;
    }

    public void setAuditComment(String auditComment) {
        this.auditComment = auditComment;
    }

    public String getExecTime() {
        return execTime;
    }

    public void setExecTime(String execTime) {
        this.execTime = execTime;
    }

    public String getExecStatus() {
        return execStatus;
    }

    public void setExecStatus(String execStatus) {
        this.execStatus = execStatus;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
