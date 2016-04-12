package com.baidu.disconf.client.common.model;

/**
 * 实例指纹
 */
public class InstanceFingerprint {

    // 本实例所在机器的IP
    private String ip = "";

    // 实例进程ID
    private String pid = "";

    public InstanceFingerprint(String ip, String pid) {
        this.ip = ip;
        this.pid = pid;
    }
    
    


    public String getIp() {
        return ip;
    }




    public String getPid() {
        return pid;
    }




    @Override
    public String toString() {
        return "InstanceFingerprint [ip=" + ip + ", pid=" + pid + "]";
    }

}
