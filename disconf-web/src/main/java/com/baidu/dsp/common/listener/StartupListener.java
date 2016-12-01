package com.baidu.dsp.common.listener;

import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartupListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(StartupListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {

    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {

        logger.info("Load StartupListener start...");

        try {
            try {
                final String props = "/application.properties";
                final Properties propsFromFile = new Properties();
                propsFromFile.load(getClass().getResourceAsStream(props));
                String domain = propsFromFile.getProperty("domain");
                arg0.getServletContext().setAttribute("domain",domain);
            }catch (Exception e){
                logger.error(e.toString(), e);
            }

            Locale.setDefault(Locale.SIMPLIFIED_CHINESE);

        } catch (Throwable t) {
            logger.error(t.getMessage(), t);

            System.exit(-1);
        }

        logger.info("Load StartupListener end...");
    }
}
