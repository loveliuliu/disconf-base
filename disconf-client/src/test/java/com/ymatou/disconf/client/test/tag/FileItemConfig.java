/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.ymatou.disconf.client.test.tag;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import org.springframework.stereotype.Component;

import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 空的分布式配置文件,用途有两种:<br/>
 * 1. 对配置文件里的内容不感兴趣，只是单纯的下载<br/>
 * 2. 当配置文件更新时，可以自动下载到本地
 */

@DisconfFile(fileName="manyTag.json")
@Component
public class FileItemConfig {

}
