/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.baidu.disconf.web.service.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.baidu.disconf.core.common.utils.AesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baidu.disconf.web.service.app.bo.Tag;
import com.baidu.disconf.web.service.app.dao.TagDao;
import com.baidu.disconf.web.service.app.dao.TagMapper;
import com.baidu.disconf.web.service.app.dto.AppTagDto;
import com.baidu.disconf.web.service.app.dto.TagDto;
import com.baidu.disconf.web.service.app.form.TagNewForm;
import com.baidu.disconf.web.service.app.service.TagMgr;
import com.baidu.disconf.web.service.app.vo.StatusEnum;
import com.baidu.disconf.web.service.user.dto.Visitor;

import com.baidu.disconf.web.utils.DateHelper;
import com.baidu.ub.common.commons.ThreadContext;

/**
 * @author luoshiqian 2016/11/14 15:58
 */
@Service
public class TagMgrImpl implements TagMgr {

    @Autowired
    private TagDao tagDao;
    @Autowired
    private TagMapper tagMapper;

    @Override
    public Tag getById(Long id) {
        return tagDao.get(id);
    }

    @Override
    public Tag findByTagName(String tagName) {
        return tagDao.findByTagName(tagName);
    }

    @Override
    public void delete(Long tagId) {
        tagDao.disable(tagId);
    }

    @Override
    public Tag save(TagNewForm newForm) throws Exception {
        String now = DateHelper.now();
        Visitor visitor = ThreadContext.getSessionVisitor();
        Tag tag;
        if (newForm.getId() == null) {

            tag = new Tag();

            tag.setTagName(newForm.getTagName());
            tag.setTagValue(AesUtil.encrypt(newForm.getTagValue()));
            tag.setMemo(newForm.getMemo());
            tag.setStatus(StatusEnum.ENABLE.name());

            tag.setUpdateTime(now);
            tag.setCreateTime(now);
            tag.setCreateUser(visitor.getLoginUserName());
            tag.setUpdateUser(visitor.getLoginUserName());
            tagDao.create(tag);
        } else {// 修改
            tag = tagDao.get(newForm.getId());

            tag.setTagName(newForm.getTagName());
            tag.setTagValue(AesUtil.encrypt(newForm.getTagValue()));
            tag.setMemo(newForm.getMemo());

            tag.setCreateTime(now);
            tag.setUpdateUser(visitor.getLoginUserName());
            tagDao.update(tag);
        }
        return tag;
    }

    @Override
    public Page<TagDto> findTagDtoByTagDto(TagDto tagDto, Pageable pageable) {

        String tagValue = tagDto.getTagValue();
        tagDto.setTagValue(null);
        tagDto.setStatus(StatusEnum.ENABLE.name());
        Page<TagDto> tagDtoPage;
        if(StringUtils.isBlank(tagValue)){
            tagDtoPage = tagMapper.findTagDto(tagDto, pageable);
        }else {
            //使用标签值搜索
            Pageable newpage = new PageRequest(0,99999);
            tagDtoPage = tagMapper.findTagDto(tagDto,newpage);
            List<TagDto> matched = new ArrayList<>();
            if(!CollectionUtils.isEmpty(tagDtoPage.getContent())){
                for (TagDto tag : tagDtoPage.getContent()) {
                    if (StringUtils.indexOf(tag.getDecryptTagValue(),tagValue) != -1) {// 满足搜索
                        matched.add(tag);
                    }
                    if (matched.size() == pageable.getPageSize()) {
                        break;
                    }
                }
            }
            tagDtoPage = new PageImpl<>(matched,pageable,tagDtoPage.getTotalElements());
        }

        return tagDtoPage;
    }

    @Override
    public boolean isTagUsed(String tagName) {
        List<String> appnames = tagMapper.findAppNamesByTagName(tagName);
        if (CollectionUtils.isEmpty(appnames)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<TagDto> findEnableTagByTagNames(List<String> tagNames) {
        return tagMapper.findEnableTagByTagNames(tagNames);
    }

    @Override
    public List<AppTagDto> findEnableAppTagByTagNames(List<String> tagNames, Long appId) {
        return tagMapper.findEnableAppTagByTagNames(tagNames,appId);
    }


    @Override
    public int disableTagByTagNames(Long appId,List<String> tagNames) {
        return tagMapper.disableTagByTagNames(appId,tagNames);
    }

    @Override
    public void useTag(Long appId,String appName, List<String> tagNames) {
        tagMapper.insertAppTag(appId,appName,tagNames);
    }
}
