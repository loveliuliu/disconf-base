/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.web.app.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baidu.disconf.web.service.app.bo.Tag;
import com.baidu.disconf.web.service.app.form.TagNewForm;
import com.baidu.disconf.web.service.app.service.TagMgr;
import com.baidu.dsp.common.exception.ValidationException;


@Component
public class TagValidator {

    @Autowired
    private TagMgr tagMgr;

    /**
     * 验证创建
     */
    public void validateSave(TagNewForm tagNewForm) {
        String tagName = tagNewForm.getTagName();
        if (StringUtils.isBlank(tagName)) {
            throw new ValidationException("标签名不能为空!", null);
        }
        if (null != tagNewForm.getId()) {// 修改时
            Tag tag = tagMgr.getById(tagNewForm.getId());
            String oldTagName = tag.getTagName();
            if (!oldTagName.equals(tagName)) {// 不相同，则是修改名称
                if(tagMgr.isTagUsed(tag.getTagName())){
                    throw new ValidationException(oldTagName + "已被使用，不能修改", null);
                }
                Tag isExist = tagMgr.findByTagName(tagName);
                if (isExist != null) {
                    throw new ValidationException(tagName + "已存在，不能修改", null);
                }
            }
        } else {
            Tag tag = tagMgr.findByTagName(tagName);
            if (tag != null) {
                throw new ValidationException(tagName + "已存在，不能创建", null);
            }
        }
    }

    public Tag validateDelete(Long id) {
        if (null == id) {
            throw new ValidationException("标签 ID不能为空!", null);
        }

        Tag tag = tagMgr.getById(id);
        if (null == tag) {
            throw new ValidationException("标签不能为空!", null);
        }

        if(tagMgr.isTagUsed(tag.getTagName())){
            throw new ValidationException(tag.getTagName() + "已被使用，不能删除", null);
        }

        return tag;
    }

}
