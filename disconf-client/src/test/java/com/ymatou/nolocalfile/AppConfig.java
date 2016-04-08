package com.ymatou.nolocalfile;

import org.springframework.stereotype.Component;

import com.baidu.disconf.client.common.annotations.DisconfFile;

@Component("xxx")
@DisconfFile(fileName="non.properties")
public class AppConfig {

}
