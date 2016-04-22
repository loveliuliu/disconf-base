package com.baidu.disconf.web.service.config.dao;

import com.baidu.disconf.web.service.config.bo.ConfigDraft;
import com.baidu.disconf.web.service.config.condition.ConfigDraftCondition;
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
public interface ConfigDraftMapper {

    Page<ConfigDraft> findByConfigDraft(@Param("configDraft") ConfigDraftCondition configDraft, @Param("pageable") Pageable pageable);

}
