package com.baidu.disconf.client.store.inner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.common.model.DisconfCenterBaseModel;
import com.baidu.disconf.client.common.model.DisconfCenterFile;

/**
 * 配置仓库,是个单例
 *
 * @author liaoqiqi
 * @version 2014-6-9
 */
public class DisconfCenterStore {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DisconfCenterStore.class);

    private DisconfCenterStore() {

    }

    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载。
     */
    private static class SingletonHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static DisconfCenterStore instance = new DisconfCenterStore();
    }

    public static DisconfCenterStore getInstance() {
        return SingletonHolder.instance;
    }

    // 每个配置文件一条
    // key: 配置文件名
    // value: 配置文件数据
    private Map<String, DisconfCenterFile> confFileMap = new HashMap<String, DisconfCenterFile>();


    // 主备切换时的Key列表
    private List<String> activeBackupKeyList;

    // 标识本机器名
    private String machineName;

    /**
     * 存储 一个配置文件
     */
    public void storeOneFile(DisconfCenterBaseModel disconfCenterBaseModel) {

        DisconfCenterFile disconfCenterFile = (DisconfCenterFile) disconfCenterBaseModel;

        String fileName = disconfCenterFile.getFileName();

        if (confFileMap.containsKey(fileName)) {

            throw new RuntimeException("There are two same configure fileName " + "first: " + confFileMap.get(fileName).toString() +
                             ", Second: " + disconfCenterFile.toString());
        } else {
            confFileMap.put(fileName, disconfCenterFile);
        }
    }



    /**
     * 删除一个配置文件
     */
    public void excludeOneFile(String key) {

        if (confFileMap.containsKey(key)) {
            confFileMap.remove(key);
        }
    }

    public Map<String, DisconfCenterFile> getConfFileMap() {
        return confFileMap;
    }



    public List<String> getActiveBackupKeyList() {
        return activeBackupKeyList;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

}
