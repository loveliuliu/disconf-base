package com.baidu.disconf.web.tasks.runnable;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.web.innerapi.zookeeper.ZooKeeperDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by luoshiqian on 2016/3/30.
 */
public class ReleaseConfigConsistencyLock implements Runnable{

    protected static final Logger LOG = LoggerFactory.getLogger(ReleaseConfigConsistencyLock.class);

    private ZooKeeperDriver zooKeeperDriver;

    public ReleaseConfigConsistencyLock(ZooKeeperDriver zooKeeperDriver) {
        this.zooKeeperDriver = zooKeeperDriver;
    }

    @Override
    public void run() {


        try {

            TimeUnit.MILLISECONDS.sleep(Constants.CONFIG_CONSISTENCY_LOCK_TIME);//sleep
            zooKeeperDriver.releaseConfigConsistencyLock();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            LOG.info("-------删除锁!");
        }

    }
}
