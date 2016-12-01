/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.service.app.form;

import com.baidu.dsp.common.form.RequestFormBase;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author liaoqiqi
 * @version 2014-1-24
 */
@Data
public class TagNewForm extends RequestFormBase {

    /**
     *
     */
    private static final long serialVersionUID = 4329463343279659715L;

    private Long id;

    @NotBlank(message = "标签名不能为空")
    private String tagName;

    private String tagValue;

    private String memo;


}
