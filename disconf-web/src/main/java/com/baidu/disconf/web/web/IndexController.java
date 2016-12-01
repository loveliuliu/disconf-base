/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author luoshiqian 2016/11/9 12:13
 */
@Controller
public class IndexController {


    @RequestMapping("/")
    public String index(){
        return "redirect:login.html";
    }
}
