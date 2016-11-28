
package com.baidu.disconf.web.service.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baidu.disconf.web.service.user.bo.UserApp;
import com.baidu.disconf.web.service.user.constant.UserAppTypeEnum;
import com.baidu.disconf.web.service.user.dao.UserAppDao;
import com.baidu.disconf.web.service.user.service.UserAppMgr;
import com.google.common.collect.Lists;

/**
 * Created by luoshiqian on 2016/4/19.
 */
@Service
public class UserAppMgrImpl implements UserAppMgr{

    @Autowired
    private UserAppDao userAppDao;

    public static final  String split = "[,]";

    @Override
    @Transactional
    public void userAppManage(Long appId, String normalSelectedIds, String auditSelectedIds) {


        //先删除 再新增
        userAppDao.deleteAllByAppId(appId);
        List<UserApp> userAppList = Lists.newArrayList();
        //新增普通用户
        if(!StringUtils.isEmpty(normalSelectedIds)){
            for(String id :normalSelectedIds.split(split)){
                userAppList.add(new UserApp(Long.valueOf(id),appId,UserAppTypeEnum.normal.name()));
            }
        }

        //新增审核员
        if(!StringUtils.isEmpty(auditSelectedIds)){
            for(String id :auditSelectedIds.split(split)){
                userAppList.add(new UserApp(Long.valueOf(id),appId,UserAppTypeEnum.auditor.name()));
            }
        }

        userAppDao.create(userAppList);
    }



}
