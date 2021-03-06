package com.baidu.disconf.web.service.user.service;

import java.util.List;

import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.dto.UserDto;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.disconf.web.service.user.vo.VisitorVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author liaoqiqi
 * @version 2013-11-28
 */
public interface UserMgr {

    /**
     * 获取用户的基本信息（登录用户）
     *
     * @return
     */
    Visitor getVisitor(Long userId);

    VisitorVo getCurVisitor();

    User getUser(Long userId);

    /**
     * @return
     */
    Long create(User user);

    /**
     * @param user
     */
    void create(List<User> user);

    /**
     * @return
     */
    List<User> getAll();

    Boolean isExistByName(String name);

    User save(User user);

    void delete(Long userId);

    /**
     * 获取app用户的email
     * @param appId
     * @param type type为null时获取所有角色的用户
     * @return
     */
    String getMailToList(Long appId, String type);
}
