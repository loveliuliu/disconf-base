package com.baidu.disconf.web.service.user.dao;

import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.dto.UserDto;
import com.ymatou.common.mybatis.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by luoshiqian on 2016/4/14.
 */
@MyBatisDao
public interface UserMapper {

    Page<User> findByUser(@Param("user") User user, @Param("pageable")Pageable pageable);

    Page<UserDto> findByUserDto(@Param("user") User user, @Param("pageable")Pageable pageable);


    /**
     * 查询某个app下 某个类的用户
     * @param appId
     * @param type
     * @return
     */
    List<UserDto> findSelectedUserByApp(@Param("appId") Long appId,@Param("type") String type);


    String findUserAppAuthByUserId(@Param("userId") Long userId);

}
