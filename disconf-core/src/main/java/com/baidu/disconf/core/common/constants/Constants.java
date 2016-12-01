package com.baidu.disconf.core.common.constants;

/**
 * @author liaoqiqi
 * @version 2014-6-6
 */
public class Constants {

    public static final String DEFAULT_VERSION = "DEFAULT_VERSION";

    public static final String DEFAULT_ENV = "DEFAULT_ENV";

    /**
     * 配置的常量定义
     */
    public final static String VERSION = "version";
    public final static String APP = "app";
    public final static String MAINTYPE = "maintype";
    public final static String ENV = "env";
    public final static String KEY = "key";
    public final static String TYPE = "type";

    /**
     * Disconf-web返回的常量
     */
    public static final Integer OK = 1;
    public static final Integer NOTOK = 0;

    public final static String SEP_STRING = "/";

    /**
     * zookeeper的一些常量设置
     */
    public final static String STORE_FILE_URL_KEY = "file";
    public final static String STORE_ITEM_URL_KEY = "item";
    public final static String APP_TAG_URL_KEY = "tag";
    public final static String ZOO_HOSTS_URL_KEY = "hosts";
    public final static String ZOO_HOSTS_URL_PREFIX_KEY = "prefix";
    public final static String LOCK_PATH = "locks";
    public final static String CONFIG_CONSISTENCY_LOCK_PATH = LOCK_PATH + SEP_STRING + "configConsistencyLock";
    public final static String DRAFT_TO_CONFIG_LOCK_PATH = LOCK_PATH + SEP_STRING + "draftToConfigLock";
    public final static long CONFIG_CONSISTENCY_SCHEDULE_TIME = 30L * 60 * 1000; //定时任务时间间隔
    public final static long DRAFT_TO_CONFIG_SCHEDULE_TIME = 1L * 60 * 1000; //task生效的定时任务时间间隔
    public final static long CONFIG_CONSISTENCY_LOCK_TIME = 5L * 60 * 1000; //锁住时间
    public final static long DRAFT_TO_CONFIG_LOCK_TIME =  1L * 30 * 1000; //task生效的锁住时间



    // 通知Zookeeper更新配置的消息
    public final static String ZOO_UPDATE_STRING = "UPDATE-NOTIFYING";


    /**
     * 全局标记 iapi是否挂了
     */
    public static Boolean DISCONF_IAPI_IS_DOWN = Boolean.FALSE;
}
