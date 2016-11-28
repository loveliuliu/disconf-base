package com.baidu.disconf.web.test.service.user.dao;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.baidu.disconf.web.service.user.dao.UserDao;
import com.baidu.disconf.web.test.common.BaseTestCase;

public class UserDaoTestCase extends BaseTestCase {

    protected static final Logger LOG = LoggerFactory.getLogger(UserDaoTestCase.class);

    @Autowired
    private UserDao userDao;


    @Test
    public void test() {
//
//        Pageable pageable = new PageRequest(0,2);
//
//        Page<User> userPage = userMapper.findByUser(new User(),pageable);
//
//
//        System.out.println(userPage.getTotalElements());
//        System.out.println(userPage.getSize());
//        System.out.println(userPage.getContent().get(0));


    }

    

}
