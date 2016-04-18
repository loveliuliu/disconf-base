package com.baidu.disconf.web.service.user.dao.impl;

import com.baidu.disconf.web.service.role.bo.RoleEnum;
import com.baidu.disconf.web.service.sign.utils.SignUtils;
import org.springframework.stereotype.Repository;

import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.dao.UserDao;
import com.baidu.dsp.common.dao.AbstractDao;
import com.baidu.dsp.common.dao.Columns;

/**
 * @author liaoqiqi
 * @version 2013-11-28
 */
@Repository
public class UserDaoImpl extends AbstractDao<Long, User> implements UserDao {

    /**
     * 执行SQL
     */
    public void executeSql(String sql) {

        executeSQL(sql, null);
    }

    /**
     */
    @Override
    public User getUserByName(String name) {

        return findOne(match(Columns.NAME, name));
    }

    @Override
    public User createUserByNameAndRoleId(String name, Integer roleId) {

        User user = new User();

        user.setName(name);

        user.setPassword(SignUtils.createPassword("123456"));
        // token
        user.setToken(SignUtils.createToken(name));

        // set appids
        user.setOwnApps("");

        // role
        user.setRoleId(roleId);

        user = this.create(user);

        return user;
    }
}