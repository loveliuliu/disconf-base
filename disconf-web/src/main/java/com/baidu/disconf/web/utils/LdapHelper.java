/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 * All rights reserved.
 *
 */

package com.baidu.disconf.web.utils;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LdapHelper {

    private static final Logger logger = LoggerFactory.getLogger(LdapHelper.class);

    public static boolean authenticate(String userName, String password) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "YMT\\" + userName);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://ymt-ad-01.ymt.corp");
        try {
            DirContext ctx = new InitialDirContext(env);
            ctx.close();
            return true;
        } catch (NamingException e) {
            logger.warn("Authenticate Failed. UserName: {}", userName);
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(authenticate("YMT\\luoshiqian", "Loushi135"));
        System.out.println(authenticate("YMT\\luoshiqian", "Loushi135abc"));
        System.out.println(authenticate("luoshiqian", "Loushi135"));
    }
}
