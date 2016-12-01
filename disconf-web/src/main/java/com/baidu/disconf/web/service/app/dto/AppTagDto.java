/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.web.service.app.dto;

import com.github.knightliao.apollo.db.bo.BaseObject;
import lombok.Data;

/**
 * @author luoshiqian 2016/11/14 15:26
 */
@Data
public class AppTagDto extends BaseObject<Long> {

    private Long appId;
    private String appName;
    private String tagName;
    private String status;
}
