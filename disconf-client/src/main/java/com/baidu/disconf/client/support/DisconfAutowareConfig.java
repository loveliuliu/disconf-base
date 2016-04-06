package com.baidu.disconf.client.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.common.annotations.DisconfFileItem;
import com.baidu.disconf.client.config.inner.DisInnerConfigAnnotation;
import com.baidu.disconf.client.utils.ClassUtils;
import com.baidu.disconf.client.utils.ConfigLoaderUtils;

/**
 * 配置导入工具
 *
 * @author liaoqiqi
 * @version 2014-6-6
 */
public final class DisconfAutowareConfig {

    private DisconfAutowareConfig() {

    }

    protected static final Logger LOGGER = LoggerFactory.getLogger(DisconfAutowareConfig.class);


    /**
     * 自动导入配置数据,能识别 DisconfFileItem 或 DisInnerConfigAnnotation 的标识
     *
     * @Description: auto ware
     */
    private static void autowareConfig(final Object obj, Properties prop) throws Exception {
        try {

            Field[] fields = obj.getClass().getDeclaredFields();

            for (Field field : fields) {

                if (field.isAnnotationPresent(DisconfFileItem.class)
                        || field.isAnnotationPresent(DisInnerConfigAnnotation.class)) {

                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }

                    String name;
                    String value;

                    if (field.isAnnotationPresent(DisconfFileItem.class)) {

                        name = field.getName();
                        value = prop.getProperty(name, null);

                    } else {

                        // disconf使用的配置

                        DisInnerConfigAnnotation config = field.getAnnotation(DisInnerConfigAnnotation.class);
                        name = config.name();

                        String defaultValue = config.defaultValue();
                        value = prop.getProperty(name, defaultValue);

                        // using disconf as prefix to avoid env confusion
                        if (value.equals(defaultValue) && name != null) {
                            if (name.contains("disconf.")) {
                                String newName = name.substring(name.indexOf('.') + 1);
                                value = prop.getProperty(newName, defaultValue);
                            }
                        }
                    }

                    field.setAccessible(true);

                    if (null != value) {
                        ClassUtils.setFieldValeByType(field, obj, value);
                    }
                }
            }
        } catch (Exception e) {

            throw new Exception("error while autowire config file for instance " + obj.getClass(), e);
        }
    }

    /**
     * 自动导入某个配置文件
     *
     * @throws Exception
     */
    public static void autowareConfigFromClassPath(final Object obj, final String propertyFilePath ) throws Exception {

        // 读配置文件
        Properties prop = ConfigLoaderUtils.loadWithTomcatMode(propertyFilePath);
        autowareConfig(obj, prop);
    }
    
    
    public static void autowareConfigFromFileSystem(final Object obj, final String propertyFilePath ) throws Exception {

        // 读配置文件
        Properties prop = ConfigLoaderUtils.loadFromFileSystem(propertyFilePath);
 
        autowareConfig(obj, prop);
    }
    

    /**
     * 自动导入Static配置数据,能识别 DisconfFileItem 或 DisconfFileItem 的标识
     *
     * @Description: auto ware
     */
    private static void autowareStaticConfig(Class<?> cls, Properties prop) throws Exception {

        if (null == prop) {
            throw new Exception("cannot autowareConfig null");
        }

        try {

            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {

                if (field.isAnnotationPresent(DisconfFileItem.class)) {

                    if (!Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }

                    field.setAccessible(true);

                    String name = field.getName();
                    Object value = prop.getProperty(name, null);
                    if (value != null) {
                        ClassUtils.setFieldValeByType(field, null, String.valueOf(value));
                    }
                }
            }
        } catch (Exception e) {

            throw new Exception("error while autowire config file", e);
        }
    }

    /**
     * 自动导入配置文件至 static变量
     *
     * @throws Exception
     */
    public static void autowareStaticConfig(Class<?> cls, final String propertyFilePath) throws Exception {

        // 读配置文件
        Properties prop = ConfigLoaderUtils.loadFromFileSystem(propertyFilePath);

        autowareStaticConfig(cls, prop);
    }

}
