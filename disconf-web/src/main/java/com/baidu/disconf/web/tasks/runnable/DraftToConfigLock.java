package com.baidu.disconf.web.tasks.runnable;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.web.innerapi.zookeeper.ZooKeeperDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by qianmin on 2016/4/25.
 */
public class DraftToConfigLock implements Runnable{
    protected static final Logger LOG = LoggerFactory.getLogger(DraftToConfigLock.class);

    private ZooKeeperDriver zooKeeperDriver;

    public DraftToConfigLock(ZooKeeperDriver zooKeeperDriver) {
        this.zooKeeperDriver = zooKeeperDriver;
    }

    @Override
    public void run() {

        try {
            TimeUnit.MILLISECONDS.sleep(Constants.DRAFT_TO_CONFIG_LOCK_TIME);//sleep
            zooKeeperDriver.releaseDraftToConfigLock();
        } catch (Exception e) {
            LOG.warn("release lock error:{}",e.getCause());
        } finally {

            LOG.info("-------删除锁!");
        }

    }
}
