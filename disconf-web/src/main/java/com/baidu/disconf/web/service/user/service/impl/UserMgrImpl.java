package com.baidu.disconf.web.service.user.service.impl;

import java.util.List;

import com.baidu.disconf.web.service.role.bo.RoleEnum;
import com.baidu.disconf.web.service.user.dao.UserMapper;
import com.baidu.disconf.web.service.user.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.dao.UserDao;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.disconf.web.service.user.service.UserInnerMgr;
import com.baidu.disconf.web.service.user.service.UserMgr;
import com.baidu.disconf.web.service.user.vo.VisitorVo;
import com.baidu.ub.common.commons.ThreadContext;

/**
 * @author liaoqiqi
 * @version 2013-12-5
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserMgrImpl implements UserMgr {

    protected static final Logger LOG = LoggerFactory.getLogger(UserMgrImpl.class);

    @Autowired
    private UserInnerMgr userInnerMgr;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Visitor getVisitor(Long userId) {

        return userInnerMgr.getVisitor(userId);
    }

    @Override
    public VisitorVo getCurVisitor() {

        Visitor visitor = ThreadContext.getSessionVisitor();
        if (visitor == null) {
            return null;
        }

        VisitorVo visitorVo = new VisitorVo();
        visitorVo.setId(visitor.getId());
        visitorVo.setName(visitor.getLoginUserName());

        return visitorVo;
    }

    /**
     * 创建
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Long create(User user) {

        user = userDao.create(user);
        return user.getId();
    }

    /**
     *
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void create(List<User> users) {

        userDao.create(users);
    }

    @Override
    public List<User> getAll() {

        return userDao.findAll();
    }

    @Override
    public User getUser(Long userId) {

        return userDao.get(userId);
    }

    @Override
    public Page<UserDto> findByUser(User user, Pageable pageable) {

        return userMapper.findByUserDto(user,pageable);
    }

    @Override
    public Page<UserDto> findByUserTest(User user, Pageable pageable) {
        return userMapper.findByUserDto(user,pageable);
    }

    @Override
    public Boolean isExistByName(String name) {
        User user = userDao.getUserByName(name);
        if(user != null){
            return true;
        }
        return false;
    }

    @Override
    public User save(User user) {

        if(null != user.getId()){

            User oldUser = userDao.get(user.getId());
            oldUser.setRoleId(user.getRoleId());
            oldUser.setEmail(user.getEmail());
            oldUser.setPhone(user.getPhone());
            userDao.update(oldUser);

        }else {

            user = userDao.createUser(user.getName(),user.getRoleId(),user.getEmail(),user.getPhone());
        }

        return user;
    }

    @Override
    public void delete(Long userId) {
        userDao.delete(userId);
    }

    @Override
    public String findUserAppAuthByUserId(Long userId) {
        return userMapper.findUserAppAuthByUserId(userId);
    }
}
