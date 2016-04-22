package com.baidu.disconf.web.service.user.service;

import com.baidu.disconf.web.service.user.dto.UserDto;

import java.util.List;

/**
 * Created by luoshiqian on 2016/4/19.
 */
public interface UserAppMgr {

    void userAppManage(Long appId,String normalSelectedIds,String auditSelectedIds);

    List<UserDto> findSelectedUserByApp(Long appId,String type);
}
