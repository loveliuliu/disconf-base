package com.baidu.disconf.web.service.app.dao;


import com.baidu.disconf.web.service.app.bo.App;
import com.baidu.disconf.web.service.app.dto.AppDto;
import com.ymatou.common.mybatis.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by luoshiqian on 2016/4/14.
 */
@MyBatisDao
public interface AppMapper {

    Page<App> findByApp(@Param("app") App app, @Param("pageable") Pageable pageable);

    List<App> findByApp(@Param("app") App app);
    
    Page<AppDto> findAppDtoByAppDto(@Param("app") AppDto app,@Param("pageable") Pageable pageable);
    
    Page<AppDto> findAppDtoByAppDtoForManager(@Param("app") AppDto app,@Param("pageable") Pageable pageable);
}
