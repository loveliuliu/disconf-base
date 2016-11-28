/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.service.app.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baidu.disconf.web.service.app.bo.Tag;
import com.baidu.disconf.web.service.app.dao.TagDao;
import com.baidu.disconf.web.service.app.vo.StatusEnum;
import com.baidu.dsp.common.dao.AbstractDao;
import com.baidu.unbiz.common.genericdao.operator.Match;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Service
public class TagDaoImpl extends AbstractDao<Long, Tag> implements TagDao {


    @Override
    public List<Tag> findByTagNames(List<String> tagNames) {
        return find(new Match("tag_name", tagNames), new Match("status", StatusEnum.ENABLE.name()));
    }
}
