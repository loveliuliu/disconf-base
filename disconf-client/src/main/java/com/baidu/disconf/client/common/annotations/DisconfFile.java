package com.baidu.disconf.client.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式的配置文件
 *
 * @author liaoqiqi
 * @version 2014-5-16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisconfFile {

    /**
     * 配置文件名,必须指定
     */
    String fileName();
}
