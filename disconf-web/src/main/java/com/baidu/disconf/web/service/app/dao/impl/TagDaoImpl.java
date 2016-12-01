/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.service.app.dao.impl;

import org.springframework.stereotype.Service;

import com.baidu.disconf.web.service.app.bo.Tag;
import com.baidu.disconf.web.service.app.dao.TagDao;
import com.baidu.disconf.web.service.app.vo.StatusEnum;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.disconf.web.utils.DateHelper;
import com.baidu.dsp.common.dao.AbstractDao;
import com.baidu.ub.common.commons.ThreadContext;
import com.baidu.unbiz.common.genericdao.operator.Match;
import com.baidu.unbiz.common.genericdao.operator.Modify;
import com.google.common.collect.Lists;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Service
public class TagDaoImpl extends AbstractDao<Long, Tag> implements TagDao {

    @Override
    public Tag findByTagName(String tagName) {

        return findOne(new Match("tag_name", tagName), new Match("status", StatusEnum.ENABLE.name()));
    }

    @Override
    public void disable(long id) {
        String now = DateHelper.now();
        Visitor visitor = ThreadContext.getSessionVisitor();
        update(Lists.newArrayList(new Modify("status",StatusEnum.DISABLE.name())
                ,new Modify("update_time",now)
                ,new Modify("update_user",visitor.getLoginUserName())),
                new Match("id",id)
        );
    }
}
