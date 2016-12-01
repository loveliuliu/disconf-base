/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.client.utils;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.common.model.CmdbEnv;
import com.baidu.disconf.client.config.DisClientConfig;
import com.baidu.disconf.core.common.restful.type.RestfulGet;
import com.baidu.disconf.core.common.utils.FileUtils;
import com.baidu.disconf.core.common.utils.OsUtil;
import com.baidu.disconf.core.common.utils.http.HttpClientUtil;
import com.google.gson.Gson;

/**
 * @author luoshiqian 2016/11/23 19:03
 */
public class EnvHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnvHelper.class);

    public static void fillEnv(DisClientConfig clientConfig) throws Exception {
        // 设置 ENV
        if (StringUtils.isBlank(clientConfig.ENV)) {// 为空时 从启动参数 -Ddisconf.env里获取
                                                    // 防止读取不到（还可以通过配置文件设置读取）
            String env = System.getProperty("disconf.env");

            // 获取到启动环境，如果是为空 从cmdb里获取环境
            if (StringUtils.isBlank(env)) {

                if (StringUtils.isBlank(clientConfig.APP)) {
                    throw new Exception("disconf find env error,please setting appname");
                }
                String ip = IpUtils.getIp();
                if (ip.equals("127.0.0.1")) {
                    throw new Exception("disconf find env error,can not find ipaddress");
                }
                // 初始化httpclient
                HttpClientUtil.init();
                // 初始化disconfCmdbEnv.json文件
                File cmdbEnvFile = initCmdbEnvFile(clientConfig);

                Gson gson = new Gson();
                CmdbEnv cmdbEnv = null;
                boolean isCmdbEnvCorrect = false;
                try {
                    cmdbEnv = remoteCallCmdbEnv(clientConfig.APP, ip);
                    if (null != cmdbEnv && cmdbEnv.getCode() == 0 && StringUtils.isNotBlank(cmdbEnv.getEnvironment())
                            && (cmdbEnv.getEnvironment().equals("STG") || cmdbEnv.getEnvironment().equals("PRD"))) {
                        isCmdbEnvCorrect = true;
                    }
                    // 如果 cmdb返回不正确 并且备份文件不存在 则启动失败
                    if (!isCmdbEnvCorrect && !cmdbEnvFile.exists()) {
                        throw new Exception("disconf find env error,call cmdb,code:" + cmdbEnv.getCode() + ",env:"
                                + cmdbEnv.getEnvironment());
                    } else if (isCmdbEnvCorrect) {// cmdb返回正确
                        clientConfig.ENV = cmdbEnv.getEnvironment();

                        if (cmdbEnvFile.exists()) {// 存在 则对比，如果不同打出error日志。、继续启动
                            String cmdbEnvStr = FileUtils.readFileToString(cmdbEnvFile);
                            CmdbEnv diskCmdbEnv = gson.fromJson(cmdbEnvStr, CmdbEnv.class);
                            if (!diskCmdbEnv.getEnvironment().equals(cmdbEnv.getEnvironment())) {
                                // 磁盘中cmdb环境 与 新请求到的环境不一致！
                                LOGGER.error("disk disconfCmdb Env:{} is different from new request cmdb env:{}",
                                        diskCmdbEnv, cmdbEnv);
                                // 替换原来的环境
                                FileUtils.writeStringToFile(cmdbEnvFile, gson.toJson(cmdbEnv));
                            }
                        } else {// 不存在 写入文件
                            FileUtils.writeStringToFile(cmdbEnvFile, gson.toJson(cmdbEnv));
                        }
                        LOGGER.info("find cmdbEnv:{}", new Gson().toJson(cmdbEnv));
                    } else {// cmdb 返回不正确，但备份文件存在、使用备份文件启动
                        settingEnvFromBackUp(clientConfig, cmdbEnvFile, gson);
                        LOGGER.error("find cmdbEnv remote call get error result:{}, using local backup instead",
                                new Gson().toJson(cmdbEnv));
                    }
                } catch (Exception e) {
                    if (cmdbEnvFile.exists()) {
                        LOGGER.error("find cmdbEnv remote call error,try to load env from backup", e);
                        settingEnvFromBackUp(clientConfig, cmdbEnvFile, gson);
                    } else {
                        LOGGER.error("find cmdbEnv remote call error and local backup env file not found!", e);
                    }
                }
            } else {
                clientConfig.ENV = env;
            }
        }
    }

    /**
     * 使用备份环境
     * 
     * @param clientConfig
     * @param file
     * @param gson
     * @throws Exception
     */
    private static void settingEnvFromBackUp(DisClientConfig clientConfig, File file, Gson gson) throws Exception {
        String cmdbEnvStr = FileUtils.readFileToString(file);
        CmdbEnv diskCmdbEnv = gson.fromJson(cmdbEnvStr, CmdbEnv.class);
        clientConfig.ENV = diskCmdbEnv.getEnvironment();

        LOGGER.info("using local backup env:{}", cmdbEnvStr);
    }

    /**
     * 初始化disconfCmdbEnv.json文件
     * 
     * @param clientConfig
     * @return
     */
    private static File initCmdbEnvFile(DisClientConfig clientConfig) {
        String envFileDir = "/usr/local/config/" + clientConfig.APP;
        OsUtil.makeDirs(envFileDir);
        String envFilePath = envFileDir + "/disconfCmdbEnv.json";
        File file = new File(envFilePath);
        return file;
    }

    /**
     * 远程cmdb调用
     * 
     * @param app
     * @param ip
     * @return
     * @throws Exception
     */
    public static CmdbEnv remoteCallCmdbEnv(String app, String ip) throws Exception {
        URL url = new URL("http://environment.ops.ymatou.cn/api?application=" + app + "&ipaddress=" + ip);
        return new RestfulGet<>(CmdbEnv.class, url).call();
    }
}
