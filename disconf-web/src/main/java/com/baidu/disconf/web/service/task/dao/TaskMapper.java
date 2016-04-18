package com.baidu.disconf.web.service.task.dao;


import com.baidu.disconf.web.service.app.bo.App;
import com.baidu.disconf.web.service.task.bo.Task;
import com.ymatou.common.mybatis.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by luoshiqian on 2016/4/14.
 */
@MyBatisDao
public interface TaskMapper {

    Page<Task> findByTask(@Param("app") App user, @Param("pageable") Pageable pageable);

    List<Task> findByTask(@Param("app") App user);

}
