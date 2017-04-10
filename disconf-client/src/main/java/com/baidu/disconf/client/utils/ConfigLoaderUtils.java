package com.baidu.disconf.client.utils;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.core.common.utils.ClassLoaderUtil;

/**
 * 配置导入工具
 *
 * @author liaoqiqi
 * @version 2014-6-6
 */
public final class ConfigLoaderUtils {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(ConfigLoaderUtils.class);
    
    public static final String WORK_DIR = System.getProperty("user.dir");

    private ConfigLoaderUtils() {

    }

    /**
     * @param propertyFilePath
     *
     * @return void
     *
     * @Description: 使用TOMCAT方式来导入
     * @author liaoqiqi
     * @date 2013-6-19
     */
    public static Properties loadWithTomcatMode(final String propertyFilePath)
            throws Exception {

        Properties props = new Properties();

        try {

            // 先用TOMCAT模式进行导入
            // http://blog.csdn.net/minfree/article/details/1800311
            // http://stackoverflow.com/questions/3263560/sysloader-getresource-problem-in-java
            URL url = ClassLoaderUtil.getLoader().getResource(propertyFilePath);
            URI uri = new URI(url.toString());
            props.load(new FileInputStream(uri.getPath()));

        } catch (Exception e) {

            // http://stackoverflow.com/questions/574809/load-a-resource-contained-in-a-jar
            props.load(ClassLoaderUtil.getLoader().getResourceAsStream(propertyFilePath));
        }
        return props;
    }


    /**
     * @param filePath
     *
     * @return InputStream
     *
     * @Description: 采用两种方式来载入文件
     * @author liaoqiqi
     * @throws IOException 
     * @date 2013-6-20
     */
    public static String loadFile(String filePath) throws IOException {

        InputStream in = null;
        File f = null;
        try {
            if ( filePath.startsWith("/")) {
                f = new File(filePath);
            } else {
                f = new File(WORK_DIR, filePath );
            }
            if (!f.exists()) {
                return null;
            }
            in = new FileInputStream(f);
            return IOUtils.toString(in, "UTF-8");

        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.error("WHY HERE!", e);
                }
            }
        }
    }
    
    

    /**
     * Load from file system, not from class path
     * @param propertyFilePath
     * @return
     * @throws Exception
     */
    public static Properties loadFromFileSystem(final String propertyFilePath)
            throws Exception {

        Properties props = new Properties();
        if ( propertyFilePath.startsWith("/")) {
            propertiesLoadWithUTF8(props,new FileInputStream(propertyFilePath));
        } else {
            File f = new File(WORK_DIR, propertyFilePath);
            propertiesLoadWithUTF8(props,new FileInputStream(f));
        }

        return props;
    }

    private static void propertiesLoadWithUTF8(Properties properties,FileInputStream fileInputStream)throws Exception{
        Reader reader = new InputStreamReader(fileInputStream, "UTF-8");
        properties.load(reader);
    }

    
    public static void main(String[] args ) throws Exception {
//        System.out.println(ConfigLoaderUtils.loadFromFileSystem("/opt/config/disconf/application.properties"));
        System.out.println(ConfigLoaderUtils.loadFromFileSystem("disconf-client/src/test/resources/disconf.properties"));
    }
    
    
}
