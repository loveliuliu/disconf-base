package com.baidu.disconf.web.service.config.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by luoshiqian on 2016/4/21.
 */
@Data
public class ConfDraftSubmitForm extends ConfListForm {

    @NotNull
    private Integer execNow;
    private String execTime;
    private String memo;


}
