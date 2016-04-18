package com.baidu.disconf.web.web.auth;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.baidu.disconf.web.service.user.dto.UserDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.disconf.web.service.sign.form.SigninForm;
import com.baidu.disconf.web.service.sign.service.SignMgr;
import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.service.UserMgr;
import com.baidu.disconf.web.service.user.vo.VisitorVo;
import com.baidu.disconf.web.web.auth.constant.LoginConstant;
import com.baidu.disconf.web.web.auth.login.SessionLogin;
import com.baidu.disconf.web.web.auth.validator.AuthValidator;
import com.baidu.dsp.common.annotation.NoAuth;
import com.baidu.dsp.common.constant.ErrorCode;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;

/**
 * @author liaoqiqi
 * @version 2014-1-20
 */
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/account")
public class UserController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserMgr userMgr;

    @Autowired
    private AuthValidator authValidator;

    @Autowired
    private SignMgr signMgr;

    @Autowired
    private SessionLogin sessionLogin;

    /**
     * GET 获取
     *
     * @param
     *
     * @return
     */
    @NoAuth
    @RequestMapping(value = "/session", method = RequestMethod.GET)
    @ResponseBody
    public JsonObjectBase get() {

        VisitorVo visitorVo = userMgr.getCurVisitor();
        if (visitorVo != null) {

            return buildSuccess("visitor", visitorVo);

        } else {

            // 没有登录啊
            return buildGlobalError("syserror.inner", ErrorCode.GLOBAL_ERROR);
        }
    }

    /**
     * 登录
     *
     * @param signin
     * @param request
     *
     * @return
     */
    @NoAuth
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    @ResponseBody
    public JsonObjectBase signin(@Valid SigninForm signin, HttpServletRequest request) {

        LOG.info(signin.toString());

        // 验证
        authValidator.validateLogin(signin);

        // 数据库登录
        User user = signMgr.signin(signin.getName());

        // 过期时间
        int expireTime = LoginConstant.SESSION_EXPIRE_TIME;
        if (signin.getRemember().equals(1)) {
            expireTime = LoginConstant.SESSION_EXPIRE_TIME2;
        }


        sessionLogin.login(request, user, expireTime);

        VisitorVo visitorVo = userMgr.getCurVisitor();

        return buildSuccess("visitor", visitorVo);
    }

    /**
     * 登出
     *
     * @param request
     *
     * @return
     */
    @NoAuth
    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    @ResponseBody
    public JsonObjectBase signout(HttpServletRequest request) {

        sessionLogin.logout(request);
        String logout = "domainUser";
        String system = request.getParameter("system");
        if (null != system && "1".equals(system)) {
            logout = "system";
        }

        return buildSuccess("logout", logout);
    }


    @NoAuth
    @RequestMapping(value = "/list")
    @ResponseBody
    public JsonObjectBase list(User user,Pageable pageable){

        Page<UserDto> userPage = userMgr.findByUser(user,pageable);

        return buildSuccess(userPage);
    }

    @RequestMapping(value = "/save")
    @ResponseBody
    public JsonObjectBase save(User user){

        if(StringUtils.isBlank(user.getName())){
            return buildGlobalError("用户名不能为空!", ErrorCode.DEFAULT_ERROR);
        }

        if(null == user.getRoleId()){
            return buildGlobalError("用户角色不能为空!", ErrorCode.DEFAULT_ERROR);
        }

        if(userMgr.isExistByName(user.getName())){
            return buildGlobalError("用户名已存在!", ErrorCode.DEFAULT_ERROR);
        }

        userMgr.save(user);

        return buildSuccess("新建用户成功!");
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public JsonObjectBase delete(Long userId){
        if(null == userId){
            return buildGlobalError("用户ID不能为空!", ErrorCode.DEFAULT_ERROR);
        }

        User user = userMgr.getUser(userId);
        if(null == user){
            return buildGlobalError("用户不存在，用户ID:" + userId, ErrorCode.DEFAULT_ERROR);
        }
        if(user.getName().equals("admin")){
            return buildGlobalError("不能删除超级管理员!", ErrorCode.DEFAULT_ERROR);
        }
        userMgr.delete(userId);

        return buildSuccess("删除用户成功!");
    }
}
