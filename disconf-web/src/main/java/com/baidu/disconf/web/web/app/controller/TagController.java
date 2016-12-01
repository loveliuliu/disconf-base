/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.web.app.controller;

import com.baidu.disconf.core.common.utils.AesUtil;
import com.baidu.disconf.web.service.app.bo.Tag;
import com.baidu.disconf.web.service.app.dto.TagDto;
import com.baidu.disconf.web.service.app.form.TagNewForm;
import com.baidu.disconf.web.service.app.service.AppMgr;
import com.baidu.disconf.web.service.app.service.TagMgr;
import com.baidu.disconf.web.service.role.bo.RoleEnum;
import com.baidu.disconf.web.service.user.service.UserAppMgr;
import com.baidu.disconf.web.service.user.service.UserMgr;
import com.baidu.disconf.web.service.user.vo.VisitorVo;

import com.baidu.disconf.web.web.app.validator.AppValidator;
import com.baidu.disconf.web.web.app.validator.TagValidator;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import static com.baidu.dsp.common.constant.ErrorCode.ACCESS_NOAUTH_ERROR;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/tag")
public class TagController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private AppMgr appMgr;

    @Autowired
    private AppValidator appValidator;

    @Autowired
    private UserAppMgr userAppMgr;

    @Autowired
    private UserMgr userMgr;

    @Autowired
    private TagMgr tagMgr;

    @Autowired
    private TagValidator tagValidator;


    /**
     * save
     *
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public JsonObjectBase save(@Valid TagNewForm tagNewForm) {

        LOG.info(tagNewForm.toString());

        tagValidator.validateSave(tagNewForm);

        try {
            tagMgr.save(tagNewForm);
            return buildSuccess("保存成功");
        } catch (Exception e) {
            return buildSuccess("保存失败");
        }
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public JsonObjectBase list(TagDto tagDto, Pageable pageable) {
        Page<TagDto> tagDtoPage = null;

        VisitorVo visitorVo = userMgr.getCurVisitor();

        // 管理管或dba才有权限
        if (RoleEnum.ADMIN.getValue() == Integer.valueOf(visitorVo.getRole())
                || RoleEnum.DBA.getValue() == Integer.valueOf(visitorVo.getRole())) {

            tagDtoPage = tagMgr.findTagDtoByTagDto(tagDto, pageable);
            return buildSuccess(tagDtoPage);
        }

        return buildGlobalError("无权限", ACCESS_NOAUTH_ERROR);
    }


    @RequestMapping(value = "/delete")
    @ResponseBody
    public JsonObjectBase delete(Long id) {

        tagValidator.validateDelete(id);

        tagMgr.delete(id);

        return buildSuccess("删除标签成功!");
    }

    @RequestMapping(value = "/findById")
    @ResponseBody
    public JsonObjectBase findById(Long id) {

        Tag tag = tagMgr.getById(id);
        try {
            tag.setTagValue(AesUtil.decrypt(tag.getTagValue()));
        } catch (Exception e) {
            LOG.error("decrpty error",e);
        }
        return buildSuccess(tag);
    }



}
