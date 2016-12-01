/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.service.app.service;

import java.util.List;

import com.baidu.disconf.web.service.app.dto.AppTagDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.baidu.disconf.web.service.app.bo.Tag;
import com.baidu.disconf.web.service.app.dto.TagDto;
import com.baidu.disconf.web.service.app.form.TagNewForm;


public interface TagMgr {

    /**
     * 获取一个标签
     *
     * @param id
     *
     * @return
     */
    Tag getById(Long id);

    Tag findByTagName(String tagName);

    /**
     * 生成一个标签
     *
     * @param newForm
     *
     * @return
     */
    Tag save(TagNewForm newForm)throws Exception ;

    /**
     * 删除此标签
     *
     * @param tagId
     */
    void delete(Long tagId);


    Page<TagDto> findTagDtoByTagDto(TagDto tagDto, Pageable pageable);

    boolean isTagUsed(String tagName);

    List<TagDto> findEnableTagByTagNames(List<String> tagNames);

    List<AppTagDto> findEnableAppTagByTagNames(List<String> tagNames,Long appId);


    int disableTagByTagNames(Long appId,List<String> tagNames);

    void useTag(Long appId,String appName,List<String> tagNames);
}
