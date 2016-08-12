package com.baidu.disconf.web.web.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.baidu.disconf.web.service.app.bo.App;
import com.baidu.disconf.web.service.app.dto.AppDto;
import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.constant.UserAppTypeEnum;
import com.baidu.disconf.web.service.user.dto.UserDto;
import com.baidu.disconf.web.service.user.service.UserAppMgr;
import com.baidu.disconf.web.service.user.service.UserMgr;
import com.baidu.disconf.web.service.user.vo.VisitorVo;
import com.baidu.dsp.common.constant.ErrorCode;
import com.baidu.dsp.common.form.RequestListBase;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.disconf.web.service.app.form.AppNewForm;
import com.baidu.disconf.web.service.app.service.AppMgr;
import com.baidu.disconf.web.service.app.vo.AppListVo;
import com.baidu.disconf.web.service.role.bo.RoleEnum;
import com.baidu.disconf.web.web.app.validator.AppValidator;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/app")
public class AppController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private AppMgr appMgr;

    @Autowired
    private AppValidator appValidator;

    @Autowired
    private UserAppMgr userAppMgr;

    @Autowired
    private UserMgr userMgr;

    /**
     * list
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public JsonObjectBase list() {

        List<AppListVo> appListVos = appMgr.getAuthAppVoList();

        return buildListSuccess(appListVos, appListVos.size());
    }

    /**
     * create
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public JsonObjectBase create(@Valid AppNewForm appNewForm) {

        LOG.info(appNewForm.toString());

        appValidator.validateCreate(appNewForm);

        appMgr.create(appNewForm);

        return buildSuccess("创建成功");
    }

    @RequestMapping(value = "/listApp")
    @ResponseBody
    public JsonObjectBase listApp(AppDto app , Pageable pageable){
        Page<AppDto> appDtoPage;
        
        VisitorVo visitorVo = userMgr.getCurVisitor();
        if(RoleEnum.ADMIN.getValue() == Integer.valueOf(visitorVo.getRole())){
            appDtoPage = appMgr.findAppDtoByAppDtoForManager(app, pageable);
        }else{
            app.setUserId(userMgr.getCurVisitor().getId());
            appDtoPage = appMgr.findAppDtoByAppDto(app, pageable);
        }

        return buildSuccess(appDtoPage);
    }


    @RequestMapping(value = "/delete")
    @ResponseBody
    public JsonObjectBase delete(Long id){
        if(null == id){
            return buildGlobalError("APP ID不能为空!", ErrorCode.DEFAULT_ERROR);
        }

        App app = appMgr.getById(id);
        if(null == app){
            return buildGlobalError("APP不存在，App ID:" + id, ErrorCode.DEFAULT_ERROR);
        }

        appMgr.delete(id);

        return buildSuccess("删除用户成功!");
    }

    @RequestMapping(value = "/findById")
    @ResponseBody
    public JsonObjectBase findById(Long id){

        App app = appMgr.getById(id);

        return buildSuccess(app);
    }


    @RequestMapping(value = "/appUserManage")
    @ResponseBody
    public JsonObjectBase appUserManage(Long id,String normalSelectedIds,String auditSelectedIds){

        if(null == id){
            return buildGlobalError("APP ID不能为空!", ErrorCode.DEFAULT_ERROR);
        }

        App app = appMgr.getById(id);
        if(null == app){
            return buildGlobalError("APP不存在，App ID:" + id, ErrorCode.DEFAULT_ERROR);
        }


        userAppMgr.userAppManage(id,normalSelectedIds,auditSelectedIds);

        return buildSuccess("保存成功!");
    }

    @RequestMapping(value = "/selectedUsers")
    @ResponseBody
    public JsonObjectBase selectedUsers(Long id){

        List<UserDto> normalSelected = userAppMgr.findSelectedUserByApp(id, UserAppTypeEnum.normal.name());
        List<UserDto> auditSelected = userAppMgr.findSelectedUserByApp(id, UserAppTypeEnum.auditor.name());
        Map<String,Object> map = Maps.newHashMap();
        map.put("normalSelected",normalSelected);
        map.put("auditSelected",auditSelected);

        Page<UserDto> userDtoPage = userMgr.findByUser(new User(),new PageRequest(0,Integer.MAX_VALUE));
        if( null != userDtoPage && !CollectionUtils.isEmpty(userDtoPage.getContent())){
            List<UserDto> userDtos = userDtoPage.getContent();
            List<UserDto> normalUserDtos = Lists.newArrayList();
            List<UserDto> auditUserDtos = Lists.newArrayList();

            normalUserDtos.addAll(userDtos);
            auditUserDtos.addAll(userDtos);

            //清除普通用户已选择的
            this.removeDuplicated(normalUserDtos,normalSelected);

            //清除审核员已选择的
            this.removeDuplicated(auditUserDtos,auditSelected);

            map.put("normalUsers",normalUserDtos); //剩下的待选择的普通用户
            map.put("auditUsers",auditUserDtos);
        }

        return buildSuccess(map);
    }

    private void removeDuplicated(List<UserDto> userDtoList,List<UserDto> selectedList){

        List<UserDto> needRemoveList = Lists.newArrayList();

        for(UserDto userDto :userDtoList){
            for(UserDto selected:selectedList){
                if(userDto.getId().equals(selected.getId())){
                    needRemoveList.add(userDto);
                }
            }
        }

        userDtoList.removeAll(needRemoveList);

    }

}
