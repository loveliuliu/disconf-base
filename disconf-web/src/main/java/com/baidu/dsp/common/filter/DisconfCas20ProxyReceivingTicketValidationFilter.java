package com.baidu.dsp.common.filter;

import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.util.Properties;

/**
 * Created by qianmin on 2016/4/27.
 */
public class DisconfCas20ProxyReceivingTicketValidationFilter extends Cas20ProxyReceivingTicketValidationFilter{

    private static final Logger logger = LoggerFactory.getLogger(DisconfAuthenticationFilter.class);

    @Override
    protected void initInternal(FilterConfig filterConfig) throws ServletException {
        super.initInternal(filterConfig);

        try {
            final String props = "/config/application.properties";
            final Properties propsFromFile = new Properties();
            propsFromFile.load(getClass().getResourceAsStream(props));
            super.setServerName(propsFromFile.getProperty("domain"));
        }catch (Exception e){
            logger.error(e.toString(), e);
        }
    }
}
