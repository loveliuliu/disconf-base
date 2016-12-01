/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.service.app.dao;


import com.baidu.disconf.web.service.app.dto.AppTagDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.baidu.disconf.web.service.app.bo.Tag;
import com.baidu.disconf.web.service.app.dto.TagDto;
import com.ymatou.common.mybatis.annotation.MyBatisDao;

import java.util.List;

/**
 * Created by luoshiqian on 2016/4/14.
 */
@MyBatisDao
public interface TagMapper {

    Page<Tag> findByTag(@Param("tag") Tag tag, @Param("pageable") Pageable pageable);

    Page<TagDto> findTagDto(@Param("tag") TagDto tag, @Param("pageable") Pageable pageable);

    List<String> findAppNamesByTagName(@Param("tagName")String tagName);

    List<TagDto> findEnableTagByTagNames(@Param("tagNames")List<String> tagNames);

    List<AppTagDto> findEnableAppTagByTagNames(@Param("tagNames")List<String> tagNames, @Param("appId")Long appId);

    int disableTagByTagNames(@Param("appId")Long appId,@Param("tagNames")List<String> tagNames);

    int insertAppTag(@Param("appId")Long appId,@Param("appName")String appName,@Param("tagNames")List<String> tagNames);
}
