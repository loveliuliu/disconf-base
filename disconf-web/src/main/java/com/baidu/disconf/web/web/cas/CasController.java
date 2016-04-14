package com.baidu.disconf.web.web.cas;

import com.baidu.disconf.web.config.ApplicationPropertyConfig;
import com.baidu.disconf.web.web.auth.login.SessionLogin;
import com.baidu.dsp.common.annotation.NoAuth;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping(WebConstants.API_PREFIX + "/cas")
public class CasController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(CasController.class);

    @Autowired
    private SessionLogin sessionLogin;
    @Autowired
    private ApplicationPropertyConfig emailProperties;


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    @NoAuth
    public RedirectView login(HttpServletRequest request) {

        sessionLogin.casLogin(request);

        String domain = emailProperties.getDomain();
        String url = domain.contains("http")?domain:("http://"+domain);

        return new RedirectView(url+"/main.html");
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    @NoAuth
    public RedirectView logout(HttpServletRequest request) {

        return new RedirectView("http://portal.ymatou.cn");
    }
}
