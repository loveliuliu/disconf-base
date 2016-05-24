package com.baidu.disconf.core.common.restful.impl;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.core.common.restful.RestfulMgr;
import com.baidu.disconf.core.common.restful.core.RemoteUrl;
import com.baidu.disconf.core.common.restful.core.UnreliableInterface;
import com.baidu.disconf.core.common.restful.retry.RetryStrategy;
import com.baidu.disconf.core.common.restful.type.FetchConfFile;
import com.baidu.disconf.core.common.restful.type.RestfulGet;
import com.baidu.disconf.core.common.utils.MyStringUtils;
import com.baidu.disconf.core.common.utils.OsUtil;
import com.baidu.disconf.core.common.utils.http.HttpClientUtil;

/**
 * RestFul的一个实现, 独立模块
 *
 * @author liaoqiqi
 * @version 2014-6-10
 */
public class RestfulMgrImpl implements RestfulMgr {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RestfulMgrImpl.class);

    /**
     * 重试策略
     */
    private RetryStrategy retryStrategy;

    public RestfulMgrImpl(RetryStrategy retryStrategy) {

        this.retryStrategy = retryStrategy;
        HttpClientUtil.init();
    }

    /**
     * 获取JSON数据
     *
     * @param clazz
     * @param remoteUrl
     *
     * @return
     *
     * @throws Exception
     */
    public <T> T getJsonData(Class<T> clazz, RemoteUrl remoteUrl, int retryTimes, int retrySleepSeconds)
            throws Exception {

        for (URL url : remoteUrl.getUrls()) {

            // 可重试的下载
            UnreliableInterface unreliableImpl = new RestfulGet<T>(clazz, url);

            try {

                T t = retryStrategy.retry(unreliableImpl, retryTimes, retrySleepSeconds);

                return t;

            } catch (Exception e) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.info("pass");
                }
            }
        }

        throw new Exception("cannot get: " + remoteUrl);
    }

    /**
     * @param remoteUrl 远程地址
     * @param fileName 文件名
     * @param localFileDir 本地文件地址
     * @param retryTimes
     * @param retrySleepSeconds
     *
     * @return
     *
     * @throws Exception
     */
    @Override
    public String downloadFromServer(RemoteUrl remoteUrl, String fileName, String localFileDir,
            int retryTimes, int retrySleepSeconds)
            throws Exception {

        // 目标地址文件
        File localFile = new File(OsUtil.pathJoin(localFileDir, fileName));

        //
        // 进行下载、mv、copy
        //
        try {

            // 可重试的下载
            File tmpFilePathUniqueFile =
                    retryDownload(OsUtil.pathJoin(localFileDir, "temp_download"), fileName, remoteUrl, retryTimes,
                            retrySleepSeconds);

            // 将 tmp file copy localFileDir
            localFile = transfer2SpecifyDir(tmpFilePathUniqueFile, localFileDir, fileName, true);

            LOGGER.debug("Move to: " + localFile.getAbsolutePath());

        } catch (Exception e) {

            LOGGER.error("download file failed, using previous download file.", e);
        }

        if (localFile == null || !localFile.exists()) {
            throw new Exception("Faied to download and not local file existence: " + fileName);
        }

        return localFile.getAbsolutePath();
    }

    @Override
    public void close() {
        HttpClientUtil.close();
    }

    /**
     * 可重试的下载
     *
     * @param fileName
     * @param remoteUrl
     * @param retryTimes
     * @param retrySleepSeconds
     *
     * @throws Exception
     */
    private File retryDownload(String tempDir, String fileName, RemoteUrl remoteUrl, int retryTimes,
            int retrySleepSeconds)
            throws Exception {
        OsUtil.makeDirs(tempDir);

        String tmpFilePath = OsUtil.pathJoin(tempDir, fileName);
        String tmpFilePathUnique = MyStringUtils.getRandomName(tmpFilePath);
        File tmpFilePathUniqueFile = new File(tmpFilePathUnique);
        retry4ConfDownload(remoteUrl, tmpFilePathUniqueFile, retryTimes, retrySleepSeconds);

        return tmpFilePathUniqueFile;
    }

    /**
     * copy/mv 到指定目录
     *
     * @param srcFile
     * @param copy2TargetDirPath
     * @param fileName
     *
     * @return
     *
     * @throws Exception
     */
    private File transfer2SpecifyDir(File srcFile, String copy2TargetDirPath, String fileName,
            boolean isMove) throws Exception {

        // make dir
        OsUtil.makeDirs(copy2TargetDirPath);

        File targetPath = new File(OsUtil.pathJoin(copy2TargetDirPath, fileName));

        // 从下载文件 复制/mv 到targetPath 原子性的做转移
        OsUtil.transferFileAtom(srcFile, targetPath, isMove);
        return targetPath;
    }

    /**
     * Retry封装 RemoteUrl 支持多Server的下载
     *
     * @param remoteUrl
     * @param localTmpFile
     * @param retryTimes
     * @param sleepSeconds
     *
     * @return
     */
    private Object retry4ConfDownload(RemoteUrl remoteUrl, File localTmpFile, int retryTimes, int sleepSeconds)
            throws Exception {

        for (URL url : remoteUrl.getUrls()) {

            // 可重试的下载
            UnreliableInterface unreliableImpl = new FetchConfFile(url, localTmpFile);

            try {

                return retryStrategy.retry(unreliableImpl, retryTimes, sleepSeconds);

            } catch (Exception e) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.info("pass");
                }
            }
        }

        throw new Exception("download failed.");
    }

}
