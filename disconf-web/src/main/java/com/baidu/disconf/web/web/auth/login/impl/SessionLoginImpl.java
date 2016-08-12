package com.baidu.disconf.web.web.auth.login.impl;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.baidu.disconf.web.service.app.service.AppMgr;
import com.baidu.disconf.web.service.role.bo.RoleEnum;
import com.baidu.disconf.web.service.sign.service.SignMgr;
import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.constant.UserConstant;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.disconf.web.service.user.service.UserMgr;
import com.baidu.disconf.web.web.auth.login.SessionLogin;
import com.baidu.ub.common.commons.ThreadContext;

/**
 * @author liaoqiqi
 * @version 2014-2-4
 */
public class SessionLoginImpl implements SessionLogin {

    protected static final Logger LOG = LoggerFactory.getLogger(SessionLoginImpl.class);


    @Autowired
    private SignMgr signMgr;
    @Autowired
    private UserMgr userMgr;
    @Autowired
    private AppMgr appMgr;

    /**
     * 登录
     */
    @Override
    public void login(HttpServletRequest request, User user, int expireTime) {

        Visitor visitor = new Visitor();

        //
        //
        //
        visitor.setId(user.getId());
        visitor.setLoginUserId(user.getId());
        visitor.setLoginUserName(user.getName());
        visitor.setRoleId(user.getRoleId());
        if(user.getRoleId().equals(RoleEnum.ADMIN.getValue())){//管理员给所有app权限
            visitor.setAppIds(appMgr.getAppList().stream().map(app -> (Long)app.getId()).collect(Collectors.toSet()));
        }else {
            visitor.setAppIds(userMgr.findUserAppAuthByUserId(user.getId()));
        }

        //
        // 更新session
        //
        updateSessionVisitor(request.getSession(), visitor);

    }


    /**
     * 更新Session中的Userid
     *
     * @param session
     * @param visitor
     */
    public void updateSessionVisitor(HttpSession session, Visitor visitor) {

        if (visitor != null) {
            // 更新
            session.setAttribute(UserConstant.USER_KEY, visitor);
        } else {

            // 删除
            session.removeAttribute(UserConstant.USER_KEY);
        }

        ThreadContext.putSessionVisitor(visitor);
    }

    /**
     * 登出
     */
    @Override
    public void logout(HttpServletRequest request) {

        // 更新session
//        updateSessionVisitor(request.getSession(), null);
        request.getSession().invalidate();
    }
}
