package com.baidu.disconf.web.service.user.dao;

import com.baidu.disconf.web.service.user.bo.UserApp;
import com.baidu.unbiz.common.genericdao.dao.BaseDao;

/**
 * Created by luoshiqian on 2016/4/19.
 */
public interface UserAppDao extends BaseDao<Long,UserApp> {


    void deleteAllByAppIdAndType(Long appId,String type);

    void deleteAllByAppId(Long appId);
}
