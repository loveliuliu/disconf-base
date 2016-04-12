package com.baidu.disconf.client.config.inner;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.common.model.InstanceFingerprint;

/**
 * 一些通用的数据
 * @author liaoqiqi
 * @version 2014-7-1
 */
public class DisClientComConfig {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DisClientComConfig.class);

    protected static final DisClientComConfig INSTANCE = new DisClientComConfig();

    public static DisClientComConfig getInstance() {
        return INSTANCE;
    }

    private DisClientComConfig() {

        initInstanceFingerprint();
    }

    /**
     * 初始化实例指纹<br/>
     */
    private void initInstanceFingerprint() {
        instanceFingerprint = new InstanceFingerprint(getIp(), getPid());
    }

    private InstanceFingerprint instanceFingerprint;

    /**
     * 获取指纹
     */
    public String getInstanceFingerprint() {
        return instanceFingerprint.getIp() + "_" + instanceFingerprint.getPid();
    }
    
    private String getIp( ) {
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface
                .getNetworkInterfaces();

            while (netInterfaces.hasMoreElements() ) {
                NetworkInterface ni = netInterfaces.nextElement();
                if (!ni.isLoopback() && ni.isUp() && !ni.isVirtual()) {
                    Enumeration<InetAddress> address = ni.getInetAddresses();

                    while (address.hasMoreElements() ) {
                        InetAddress addr = address.nextElement();

                        if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
                            && !(addr.getHostAddress().indexOf(":") > -1)) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }

        } catch (Throwable t) {
            LOGGER.error("Failed to extract local machine ip. 127.0.0.1 is as the result instead", t);
        }
        
        return "127.0.0.1";
    }
    
    private String getPid( ) {
        
        try {
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            
            String jvmName = runtimeBean.getName();
            return jvmName.split("@")[0];
        } catch ( Throwable t ) {
            LOGGER.error("Failed to extract java pid. 00000 is as the result instead", t);
        }
        return "00000";
    }
     
    
    public static void main( String[] args ) {
        DisClientComConfig config = new DisClientComConfig();
        System.out.println( config.getIp());
        System.out.println( config.getPid());
        System.out.println(config.getInstanceFingerprint( ));
    }
}
