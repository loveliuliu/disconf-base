/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;


public class MySimpleMappingExceptionResolver extends SimpleMappingExceptionResolver {

    private final static String HEADER_STRING = "X-Requested-With";
    private final static String AJAX_HEADER = "XMLHttpRequest";
    private final static String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    private final static int HTTP_STATUS_OK = 200;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {


        logger.error("exception:{}",ex.getCause(),ex);


//        responseString(response, JSON.toJSONString(WapperUtil.error(ex.getMessage())));

        return new ModelAndView();
    }

    private void responseString(HttpServletResponse response, String jsonpInfo) {

        try {
            response.getWriter().write(jsonpInfo);

            response.setContentType(JSON_CONTENT_TYPE);
            response.setStatus(HTTP_STATUS_OK);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
