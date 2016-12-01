/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.service.app.dao;

import com.baidu.disconf.web.service.app.bo.Tag;
import com.baidu.unbiz.common.genericdao.dao.BaseDao;

import java.util.List;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
public interface TagDao extends BaseDao<Long, Tag> {

    Tag findByTagName(String tagName);

    void disable(long id);

}
