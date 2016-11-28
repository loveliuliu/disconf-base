/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.service.app.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baidu.disconf.web.service.app.bo.AppTag;
import com.baidu.disconf.web.service.app.dao.AppTagDao;
import com.baidu.disconf.web.service.app.vo.StatusEnum;
import com.baidu.dsp.common.dao.AbstractDao;
import com.baidu.unbiz.common.genericdao.operator.Match;

@Service
public class AppTagDaoImpl extends AbstractDao<Long, AppTag> implements AppTagDao {


    @Override
    public List<AppTag> findByAppName(String appName) {
        return find(new Match("app_name", appName), new Match("status", StatusEnum.ENABLE.name()));
    }
}
