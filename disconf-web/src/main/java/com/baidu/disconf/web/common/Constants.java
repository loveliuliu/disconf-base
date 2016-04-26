package com.baidu.disconf.web.common;

/**
 * Created by knightliao on 15/12/25.
 */
public class Constants {

    public final static Integer STATUS_SUBMITED = 2;
    public final static Integer STATUS_NORMAL = 1;
    public final static Integer STATUS_DELETE = 0;

    /**
     * task 审核状态
     */
    public final static String TASK_AUDIT_STATUS_WAIT = "wait_audit";
    public final static String TASK_AUDIT_STATUS_PASS = "pass";
    public final static String TASK_AUDIT_STATUS_FAIL = "fail";
    public final static String TASK_AUDIT_STATUS_CANCEL = "cancel";

    /**
     * task 执行状态
     */
    public final static String TASK_EXEC_STATUS_INIT = "init";
    public final static String TASK_EXEC_STATUS_WAIT = "wait";
    public final static String TASK_EXEC_STATUS_DONE = "done";
    public final static String TASK_EXEC_STATUS_CANCEL = "cancel";

    /**
     * 发送邮件配置
     */
    public final static String YMT_MAIL_DOMAIN = "@ymatou.com";
}
