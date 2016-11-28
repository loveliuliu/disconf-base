package com.baidu.disconf.web.service.user.dao.impl;

import com.baidu.disconf.web.service.user.bo.UserApp;
import com.baidu.disconf.web.service.user.dao.UserAppDao;
import com.baidu.dsp.common.dao.AbstractDao;
import com.baidu.unbiz.common.genericdao.operator.Match;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by luoshiqian on 2016/4/19.
 */
@Repository
public class UserAppImpl extends AbstractDao<Long, UserApp> implements UserAppDao {


    public void deleteAllByAppIdAndType(Long appId,String type){

        List<Match> matchList  = Lists.newArrayList();
        matchList.add(new Match("app_id",appId));
        if(!StringUtils.isEmpty(type)){
            matchList.add(new Match("type",type));
        }

        delete(matchList.toArray(new Match[0]));
    }


    @Override
    public void deleteAllByAppId(Long appId) {
        this.deleteAllByAppIdAndType(appId,null);
    }
}
