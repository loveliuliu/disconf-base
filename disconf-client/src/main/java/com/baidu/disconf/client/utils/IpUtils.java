/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.client.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author luoshiqian 2016/11/23 18:55
 */
public class IpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpUtils.class);
    public static String getIp() {
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface
                    .getNetworkInterfaces();

            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                if (!ni.isLoopback() && ni.isUp() && !ni.isVirtual()) {
                    Enumeration<InetAddress> address = ni.getInetAddresses();

                    while (address.hasMoreElements()) {
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

}
