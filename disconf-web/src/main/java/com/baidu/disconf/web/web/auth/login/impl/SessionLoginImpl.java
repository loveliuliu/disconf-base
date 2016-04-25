package com.baidu.disconf.web.web.auth.login.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.baidu.disconf.web.service.sign.service.SignMgr;
import com.baidu.disconf.web.service.user.service.UserMgr;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.constant.UserConstant;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.disconf.web.web.auth.constant.LoginConstant;
import com.baidu.disconf.web.web.auth.login.SessionLogin;
import com.baidu.ub.common.commons.ThreadContext;
import com.github.knightliao.apollo.redis.RedisCacheManager;
import com.github.knightliao.apollo.utils.web.CookieUtils;

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
        visitor.setAppIds(userMgr.findUserAppAuthByUserId(user.getId()));

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

    @Override
    public boolean casLogin(HttpServletRequest request) {

        Assertion assertion = (Assertion) request.getSession().getAttribute(
                AbstractCasFilter.CONST_CAS_ASSERTION);
        Visitor visitor = (Visitor) request.getSession().getAttribute(UserConstant.USER_KEY);

        if(null != visitor ){
            return true;
        }

        if(null != assertion && null != assertion.getPrincipal()){//cas 认证通过  获取用户名

            AttributePrincipal principal = assertion.getPrincipal();

            String username = principal.getName();

            // 数据库登录
            User user = signMgr.signin(username);

            // 过期时间
            int expireTime = LoginConstant.SESSION_EXPIRE_TIME;

            this.login(request, user, expireTime);

            LOG.info("用户cas 认证通过! username:{}",username);
            return true;
        }
        return false;
    }
}
