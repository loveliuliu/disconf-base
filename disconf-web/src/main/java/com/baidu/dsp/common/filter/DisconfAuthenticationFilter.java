package com.baidu.dsp.common.filter;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.util.Properties;


/**
 * Created by qianmin on 2016/4/27.
 */

public class DisconfAuthenticationFilter extends AuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(DisconfAuthenticationFilter.class);

    @Override
    protected void initInternal(FilterConfig filterConfig) throws ServletException {
        super.initInternal(filterConfig);

        String domain = (String)filterConfig.getServletContext().getAttribute("domain");
        super.setServerName(domain);
    }
}

