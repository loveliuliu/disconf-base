package com.baidu.disconf.web.innerapi.zookeeper.impl;

import java.nio.charset.Charset;
import java.text.Collator;
import java.util.*;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.dsp.common.constant.DataFormatConstants;
import com.github.knightliao.apollo.utils.time.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.core.common.path.ZooPathMgr;
import com.baidu.disconf.core.common.zookeeper.ZookeeperMgr;
import com.baidu.disconf.web.innerapi.zookeeper.ZooKeeperDriver;
import com.baidu.disconf.web.service.zookeeper.config.ZooConfig;
import com.baidu.disconf.web.service.zookeeper.dto.ZkDisconfData;
import com.baidu.dsp.common.exception.RemoteException;

/**
 * Created by knightliao on 15/1/14.
 */
public class ZookeeperDriverImpl implements ZooKeeperDriver, InitializingBean, DisposableBean {

    protected static final Logger LOG = LoggerFactory.getLogger(ZookeeperDriverImpl.class);

    @Autowired
    private ZooConfig zooConfig;

    //
    // 是否初始化
    //
    private static boolean isInit = false;

    /**
     * 通知某个Node更新
     *
     * @param app
     * @param env
     * @param version
     * @param disConfigTypeEnum
     */
    @Override
    public void notifyNodeUpdate(String app, String env, String version, String key, String value,
                                 DisConfigTypeEnum disConfigTypeEnum) {

        //
        // 获取路径
        //
        String baseUrlString = ZooPathMgr.getZooBaseUrl(zooConfig.getZookeeperUrlPrefix(), app, env, version);

        String path = "";
        if (disConfigTypeEnum.equals(DisConfigTypeEnum.ITEM)) {

            path = ZooPathMgr.getItemZooPath(baseUrlString);
        } else {
            path = ZooPathMgr.getFileZooPath(baseUrlString);
        }

        try {

            path = ZooPathMgr.joinPath(path, key);

            boolean isExist = ZookeeperMgr.getInstance().exists(path);
            if (!isExist) {

                LOG.info(path + " not exist. not update ZK.");

            } else {
                //
                // 通知
                //
                ZookeeperMgr.getInstance().writePersistentUrl(path, value);
            }

        } catch (Exception e) {

            LOG.error(e.toString(), e);
            throw new RemoteException("zk.notify.error", e);
        }
    }

    /**
     * 获取分布式配置 Map
     *
     * @param app
     * @param env
     * @param version
     *
     * @return
     */
    @Override
    public Map<String, ZkDisconfData> getDisconfData(String app, String env, String version) {

        String baseUrl = ZooPathMgr.getZooBaseUrl(zooConfig.getZookeeperUrlPrefix(), app, env, version);

        Map<String, ZkDisconfData> fileMap = new HashMap<String, ZkDisconfData>();

        try {

            fileMap = getDisconfData(ZooPathMgr.getFileZooPath(baseUrl));
            Map<String, ZkDisconfData> itemMap = getDisconfData(ZooPathMgr.getItemZooPath(baseUrl));
            fileMap.putAll(itemMap);

        } catch (KeeperException e) {
            LOG.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }

        return fileMap;
    }

    /**
     * 获取分布式配置 Map
     *
     * @param app
     * @param env
     * @param version
     *
     * @return
     */
    @Override
    public ZkDisconfData getDisconfData(String app, String env, String version, DisConfigTypeEnum disConfigTypeEnum,
                                        String keyName) {

        String baseUrl = ZooPathMgr.getZooBaseUrl(zooConfig.getZookeeperUrlPrefix(), app, env, version);

        try {

            ZookeeperMgr zooKeeperMgr = ZookeeperMgr.getInstance();
            ZooKeeper zooKeeper = zooKeeperMgr.getZk();

            if (disConfigTypeEnum.equals(DisConfigTypeEnum.FILE)) {

                return getDisconfData(ZooPathMgr.getFileZooPath(baseUrl), keyName, zooKeeper);

            } else if (disConfigTypeEnum.equals(DisConfigTypeEnum.ITEM)) {

                return getDisconfData(ZooPathMgr.getItemZooPath(baseUrl), keyName, zooKeeper);
            }

        } catch (KeeperException e) {
            LOG.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 广度搜索法：搜索分布式配置对应的两层数据
     *
     * @return
     *
     * @throws InterruptedException
     * @throws KeeperException
     */
    private Map<String, ZkDisconfData> getDisconfData(String path) throws KeeperException, InterruptedException {

        Map<String, ZkDisconfData> ret = new HashMap<String, ZkDisconfData>();

        ZookeeperMgr zooKeeperMgr = ZookeeperMgr.getInstance();
        ZooKeeper zooKeeper = zooKeeperMgr.getZk();

        if (zooKeeper.exists(path, false) == null) {
            return ret;
        }

        List<String> children = zooKeeper.getChildren(path, false);
        for (String firstKey : children) {

            ZkDisconfData zkDisconfData = getDisconfData(path, firstKey, zooKeeper);
            if (zkDisconfData != null) {
                ret.put(firstKey, zkDisconfData);
            }
        }

        return ret;
    }

    /**
     * 获取指定 配置数据
     *
     * @return
     *
     * @throws InterruptedException
     * @throws KeeperException
     */
    private ZkDisconfData getDisconfData(String path, String keyName, ZooKeeper zooKeeper)
            throws KeeperException, InterruptedException {

        String curPath = path + "/" + keyName;

        if (zooKeeper.exists(curPath, false) == null) {
            return null;
        }

        ZkDisconfData zkDisconfData = new ZkDisconfData();
        zkDisconfData.setKey(keyName);

        List<String> secChiList = zooKeeper.getChildren(curPath, false);
        List<ZkDisconfData.ZkDisconfDataItem> zkDisconfDataItems = new ArrayList<ZkDisconfData.ZkDisconfDataItem>();

        // list
        for (String secKey : secChiList) {

            // machine
            ZkDisconfData.ZkDisconfDataItem zkDisconfDataItem = new ZkDisconfData.ZkDisconfDataItem();
            zkDisconfDataItem.setMachine(secKey);

            String thirdPath = curPath + "/" + secKey;

            // value
            byte[] data = zooKeeper.getData(thirdPath, null, null);
            if (data != null) {
                zkDisconfDataItem.setValue(new String(data, CHARSET));
            }

            // add
            zkDisconfDataItems.add(zkDisconfDataItem);
        }

        zkDisconfData.setData(zkDisconfDataItems);

        return zkDisconfData;
    }

    /**
     * 返回groupName结点向下的所有zookeeper信息
     *
     * @param
     */
    @Override
    public List<String> getConf(String groupName) {

        ZookeeperMgr zooKeeperMgr = ZookeeperMgr.getInstance();
        ZooKeeper zooKeeper = zooKeeperMgr.getZk();

        List<String> retList = new ArrayList<String>();
        try {
            getConf(zooKeeper, groupName, groupName, retList);
        } catch (KeeperException e) {
            LOG.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
        return retList;
    }

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private void getConf(ZooKeeper zk, String groupName, String displayName, List<String> retList)
            throws KeeperException, InterruptedException {
        try {

            StringBuffer sb = new StringBuffer();

            int pathLength = StringUtils.countMatches(groupName, "/");
            for (int i = 0; i < pathLength - 2; ++i) {
                sb.append("\t");
            }

            List<String> children = zk.getChildren(groupName, false);

            if (!"/".equals(groupName)) {

                sb.append("|----" + displayName);
                Stat stat = new Stat();
                byte[] data = zk.getData(groupName, null, stat);

                if (data != null && children.size() == 0) {
                    sb.append("\t" + new String(data, CHARSET));
                }
            } else {
                sb.append(groupName);
            }
            retList.add(sb.toString());

            //
            //
            //
            Collections.sort(children, Collator.getInstance(java.util.Locale.CHINA));
            for (String child : children) {

                String nextName = "";

                if (!"/".equals(groupName)) {

                    nextName = groupName + "/" + child;

                } else {
                    nextName = groupName + "/" + child;
                }

                String node = StringUtils.substringAfterLast(nextName, "/");

                getConf(zk, groupName + "/" + child, node, retList);
            }

        } catch (KeeperException.NoNodeException e) {
            LOG.error("Group " + groupName + " does not exist\n");
        }

    }

    @Override
    public void destroy() throws Exception {

        ZookeeperMgr.getInstance().release();
    }

    @Override
    public synchronized void afterPropertiesSet() throws Exception {

        if (!isInit) {

            ZookeeperMgr.getInstance().init(zooConfig.getZooHosts(), zooConfig.getZookeeperUrlPrefix(), false);
            isInit = true;
        }
    }

    @Override
    public boolean tryLockConfigConsistency() {

        return  tryLock(Constants.CONFIG_CONSISTENCY_LOCK_PATH, Constants.CONFIG_CONSISTENCY_SCHEDULE_TIME,
                Constants.CONFIG_CONSISTENCY_LOCK_TIME);
    }

    @Override
    public void releaseConfigConsistencyLock() {
        realseLock(Constants.CONFIG_CONSISTENCY_LOCK_PATH);
    }

    @Override
    public boolean tryLockDraftToConfig() {

        return  tryLock(Constants.DRAFT_TO_CONFIG_LOCK_PATH, Constants.DRAFT_TO_CONFIG_SCHEDULE_TIME,
                Constants.DRAFT_TO_CONFIG_LOCK_TIME);
    }

    @Override
    public void releaseDraftToConfigLock() {
        realseLock(Constants.DRAFT_TO_CONFIG_LOCK_PATH);
    }

    private boolean tryLock(String lockPath, long scheduleTime, long lockTime){

        LOG.info("-------尝试开始获取锁");
        //增加zookeeper 临时节点  不存在则可以执行校验
        String baseLockPath = ZooPathMgr.joinPath(zooConfig.getZookeeperUrlPrefix(), Constants.LOCK_PATH);
        String path = ZooPathMgr.joinPath(zooConfig.getZookeeperUrlPrefix(), lockPath);
        String curTime = String.valueOf(new Date().getTime());

        //创建父节点 locks
        ZookeeperMgr zookeeperMgr = ZookeeperMgr.getInstance();
        zookeeperMgr.makeDir(baseLockPath,curTime);

        boolean locked = false;
        try {

            boolean isExist = ZookeeperMgr.getInstance().exists(path);
            if (!isExist) {
                zookeeperMgr.createEphemeralNode(path, curTime, CreateMode.EPHEMERAL);
                locked = true;

            }else {// 防止 某台机器获取到锁后 没有释放锁
                ZooKeeper zooKeeper = zookeeperMgr.getZk();
                byte[] data = zooKeeper.getData(path, null, null);
                if (data != null) {
                    Long lastUpdateTime = Long.valueOf(new String(data, CHARSET)); //节点上次更新时间
                    Long duration = Long.valueOf(curTime) - lastUpdateTime; //当前时间距离上次更新时间
                    Long constantsDuration = scheduleTime - lockTime;
                    LOG.info(lastUpdateTime + " " + duration  + " " + constantsDuration);
                    if (duration >= constantsDuration) {//锁没有释放 更新节点
                        zookeeperMgr.createEphemeralNode(path, curTime, CreateMode.EPHEMERAL);
                        locked = true;
                    }
                }else {//data 为空 更新节点
                    zookeeperMgr.createEphemeralNode(path, curTime, CreateMode.EPHEMERAL);
                    locked = true;
                }
            }

        } catch (Exception e) {

            LOG.error(e.toString(), e);
            throw new RemoteException("zk.notify.error", e);
        }
        return locked;
    }

    private void realseLock(String lockPath){
        String path = ZooPathMgr.joinPath(zooConfig.getZookeeperUrlPrefix(), lockPath);

        ZookeeperMgr.getInstance().deleteNode(path);
    }
}
