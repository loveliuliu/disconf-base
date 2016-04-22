package com.baidu.disconf.web.service.task.dto;

import com.baidu.disconf.web.service.task.bo.Task;
import lombok.Data;

/**
 * Created by luoshiqian on 2016/4/22.
 */
@Data
public class TaskDto extends Task{

    private String createUserName;
    private String auditStatusStr;//审核状态
    private String execStatusStr;//执行状态

    private String formatExecTime;//格式化后的生效时间

    private String curAuditUserIds;//当前审核人员
    private String curAuditUserNames;



}
