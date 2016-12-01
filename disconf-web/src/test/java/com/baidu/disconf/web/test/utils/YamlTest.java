/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.test.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.dsp.common.utils.PatternUtils;
import org.junit.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * @author luoshiqian 2016/11/11 15:12
 */
public class YamlTest {

    @Test
    public void test()throws Exception {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        // yamlPropertiesFactoryBean.setResources(new ClassPathResource("test.yml"));
        yamlPropertiesFactoryBean.setResources(new DefaultResourceLoader().getResource("classpath:test.yml"));

        Properties properties = yamlPropertiesFactoryBean.getObject();

        System.out.println(properties);


        YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
        PropertySource propertySource = yamlPropertySourceLoader.load("test",
                new DefaultResourceLoader().getResource("classpath:test.yml"), null);

        System.out.println(propertySource);

    }

    @Test
    public void systemProperties() {
        System.out.println(System.getProperty("user.dir"));

        System.out.println(System.getProperty("disconf_env"));
        System.out.println(System.getenv("disconf_env"));
        System.out.println(System.getProperty("disconf.env"));
        System.out.println(System.getenv("path"));

//        Map map = System.getenv();
//        Iterator it = map.entrySet().iterator();
//        while(it.hasNext())
//        {
//            Map.Entry entry = (Map.Entry)it.next();
//            System.out.print(entry.getKey()+"=");
//            System.out.println(entry.getValue());
//        }

//
//        System.out.println("----------------------华丽丽的分隔线------------------------------");
//        System.out.println("");
//
//        Properties properties = System.getProperties();
//        Iterator it1 =  properties.entrySet().iterator();
//        while(it1.hasNext())
//        {
//            Map.Entry entry = (Map.Entry)it1.next();
//            System.out.print(entry.getKey()+"=");
//            System.out.println(entry.getValue());
//        }

    }

    @Test
    public void testMatch(){
//        Pattern p = Pattern.compile("(.*)");
//        Pattern pattern = Pattern.compile("(^\\$\\{.*)\\}$");
//
//        String s = "${dfsadsa}abc,${abcde}";
////Pattern.matches("(^\\$\\{.*\\})","${fds},${sdf}")
//        Matcher m = pattern.matcher(s);
//
//        while(m.find()){
//            System.out.println(m.group(0));
//        }

        String s = "dsadsadas<peter>dsadasdas<lionel>\"www.163.com\"<kenny><>${fdsaf}.${fds}";
        Pattern p = Pattern.compile("(\\$\\{[^}]*})");
//        Pattern p = Pattern.compile("(<[^>]*>)");
        Matcher m = p.matcher(s);
        List<String> result=new ArrayList<String>();
        while(m.find()){
            result.add(m.group());
        }
        for(String s1:result){
            System.out.println(s1);
        }


        PatternUtils.findMatchedTagNames("fdsf").forEach(s1 -> System.out.println(s1));
    }
}
