(function ($) {


    getSession();

    var appId = -1;
    var envId = -1;
    var version = "#";
    var selectedApp = sessionStorage.selectedApp;
    var selectedEnv = sessionStorage.selectedEnv;
    var selectedVersion = sessionStorage.selectedVersion;


    //
    // 获取APP信息
    //
    $.ajax({
        type: "GET",
        url: "/api/app/list"
    }).done(
        function (data) {
            if (data.success === "true") {
                var html = "";
                var result = data.page.result;
                $
                    .each(
                    result,
                    function (index, item) {
                        html += '<li role="presentation" role="menuitem" tabindex="-1"><a rel='
                            + item.id
                            + ' href="#">APP: '
                            + item.name
                            + '</a></li>';
                    });
                $("#applist").html(html);
                reloadConfig();
            }
        });
    $("#applist").on('click', 'li a', function (e) {
        appId = $(this).attr('rel');
        $("#app_info").html(", " + $(this).text());
        $("#appDropdownMenuTitle").text($(this).text());
        version = "#";
        fetchVersion(appId, envId);
        sessionStorage.selectedApp = appId;
    });

    //
    // 获取版本信息
    //
    function fetchVersion(appId, envId) {

        if(appId == -1 || envId == -1){
            return;
        }
        var base_url = "/api/web/config/versionlist?appId=" + appId;
        url = base_url;
        if (envId != -1) {
            url = base_url + "&envId=" + envId;
        }
        $.ajax({
            type: "GET",
            url: url
        }).done(function (data) {
            if (data.success === "true") {
                var html = "";
                var result = data.page.result;
                var sVersion = version;
                var boolSelOld = false;
                if(selectedVersion) {
                    sVersion = decodeURIComponent(selectedVersion)
                }
                $.each(result, function (index, item) {
                    html += '<li><a href="#" rel="'+item+'">' + item + '</a></li>';
                    if(item == sVersion){
                        boolSelOld = true;
                    }
                });
                $("#versionChoice").html(html);

                if (html != "") {
                    $("#versionChoice li:first").addClass("active");
                    version = $("#versionChoice li:first a").text();
                    sessionStorage.selectedVersion = version;
                }

                if(selectedVersion && boolSelOld){
                    $("#versionChoice").find("a[rel='"+sVersion+"']").click()
                }else{
                    fetchMainList();
                }

            }
        });
    }
    $("#versionChoice").on('click', 'li a', function (e) {
        version = $(this).text();
        $("#versionChoice li").removeClass("active");
        $(this).parent().addClass("active");
        fetchMainList();
        e.stopPropagation();

        sessionStorage.selectedVersion = encodeURIComponent(version);
    });

    //
    // 获取Env信息
    //
    $.ajax({
        type: "GET",
        url: "/api/env/list"
    }).done(
        function (data) {
            if (data.success === "true") {
                var html = "";
                var result = data.page.result;
                $.each(result, function (index, item) {
                    html += '<li><a rel=' + item.id + ' href="#">'
                        + item.name + ' 环境</a></li>';
                });
                $("#envChoice").html(html);
                reloadConfig();
            }
        });
    $("#envChoice").on('click', 'li a', function () {
        envId = $(this).attr('rel');
        $("#env_info").html($(this).text());
        $("#envChoice li").removeClass("active");
        $(this).parent().addClass("active");
        version = "#";
        fetchVersion(appId, envId);

        sessionStorage.selectedEnv = envId;
    });

    function  reloadConfig() {
        if(selectedApp){
            $("#applist").find("[rel='"+selectedApp+"']").click()
        }
        if(selectedEnv){
            $("#envChoice").find("[rel='"+selectedEnv+"']").click()
        }
    }

    // fetchMainList();

    //
    // 渲染主列表
    //
    function fetchMainList() {

        // 参数不正确，清空列表
        if (appId == -1 || envId == -1) {
            $("#mainlist_error").text("请选择" + getTips()).show();
            $("#accountBody").html("");
            $("#mainlist").hide();
            $("#zk_deploy").hide();
            return;
        }

        if (version == "#") {
        }

        $("#zk_deploy").show().children().show();

        $("#batch_download").attr(
            'href',
                "/api/web/config/downloadfilebatch?appId=" + appId + "&envId="
                + envId + "&version=" + version);

        $("#mainlist_error").hide();
        var parameter = ""

        url = "/api/web/config/list";
        if (appId == null && envId == null && version == null) {

        } else {
            url += "?";
            if (appId != -1) {
                url += "appId=" + appId + "&";
            }
            if (envId != -1) {
                url += "envId=" + envId + "&";
            }
            if (version != "#") {
                url += "version=" + version + "&";
            }
        }

        $.ajax({
            type: "GET",
            url: url
        }).done(function (data) {
            if (data.success === "true") {
                var html = "";
                var result = data.page.result;
                $.each(result, function (index, item) {
                    html += renderItem(item, index);
                });
                if (html != "") {
                    $("#mainlist").show();
                    $("#accountBody").html(html);
                } else {
                    $("#accountBody").html("");
                }

            } else {
                $("#accountBody").html("");
                $("#mainlist").hide();
            }

            bindDetailEvent(result);

            // ZK绑定情况
            fetchZkDeploy();
        });
        var mainTpl = $("#tbodyTpl").html();
        // 渲染主列表
        function renderItem(item, i) {

            var link = "";
            del_link = '<a id="itemDel'
                + item.configId
                + '" style="cursor: pointer; cursor: hand; " ><i title="删除" class="icon-remove"></i></a>';
            if (item.type == "配置文件") {
                link = '<a target="_blank" href="modifyFile.html?configId='
                    + item.configId
                    + '"><i title="修改" class="icon-edit"></i></a>';
            } else {
                link = '<a target="_blank" href="modifyItem.html?configId='
                    + item.configId
                    + '"><i title="修改" class="icon-edit"></i></a>';
            }
            var downloadlink = '<a href="/api/web/config/download/'
                + +item.configId
                + '"><i title="下载" class="icon-download-alt"></i></a>';

            var type = '<i title="配置项" class="icon-leaf"></i>';
            if (item.type == "配置文件") {
                type = '<i title="配置文件" class="icon-file"></i>';
            }

            var data_fetch_url = '<a href="javascript:void(0);"  class="valuefetch'
                + item.configId + '" data-placement="left" data-title="'+item.key+'">点击获取</a>'

            var isRight = "OK";
            var style = "";
            if (item.errorNum > 0) {
                isRight = "; 其中" + item.errorNum + "台出现错误";
                style = "text-error";
            }
            var machine_url = '<a href="javascript:void(0);" class="' + style
                + ' machineinfo' + item.configId
                + '" data-placement="left" data-title="'+item.key+'">' + item.machineSize + '台 '
                + isRight + '</a>'

            return Util.string.format(mainTpl,'', item.appId,
                item.version, item.envId, item.envName, type, item.key,
                item.createTime, item.modifyTime, item.value, link,
                del_link, i + 1, downloadlink, data_fetch_url, machine_url);
        }
    }

    /**
     * @param result
     * @returns {String}
     */
    function getMachineList(machinelist) {

        var tip;
        if (machinelist.length == 0) {
            tip = "";
        } else {
            tip = '<div style="overflow-y:scroll;max-height:520px;"><table class="table-bordered"><tr><th>机器</th><th>值</th><th>状态</th></tr>';
            for (var i = 0; i < machinelist.length; i++) {
                var item = machinelist[i];

                var flag = "正常";
                var style = "";
                if (item.errorList.length != 0) {
                    flag = "错误";
                    style = "text-error";
                }

                tip += '<tr><td><pre>' + item.machine + " </pre></td><td><pre>"
                    + item.value + '</pre></td><td><pre class="' + style
                    + '">' + flag + ": " + item.errorList.join(",")
                    + "</pre></td></tr>";
            }
            tip += '</table></div>';
        }
        return tip;
    }


    //
    // 渲染 配置 value
    //
    function fetchConfigValue(configId, object) {
        //
        // 获取APP信息
        //
        $.ajax({
            type: "GET",
            url: "/api/web/config/" + configId
        }).done(
            function (data) {
                if (data.success === "true") {
                    var result = data.result;

                    // var e = object;
                    // e.popover({
                    //     content: "<pre style='max-height: 800px;overflow-y: auto;'>" + Util.input.escapeHtml(result.value) + "</pre>",
                    //     html: true
                    // }).popover('show');

                    layer.open({
                        title:object.attr("data-title"),
                        type: 1,
                        shadeClose: true,
                        // shade: false,
                        maxmin: true, //开启最大化最小化按钮
                        area: ['960px', '600px'],
                        content: "<pre style='margin:5px;'>" + Util.input.escapeHtml(result.value) + "</pre>",
                    });
                }
            });
    }

    //
    // 获取 ZK
    //
    function fetchZkInfo(configId, object) {
        //
        // 获取APP信息
        //
        $.ajax({
            type: "GET",
            url: "/api/web/config/zk/" + configId
        }).done(
            function (data) {
                if (data.success === "true") {
                    var result = data.result;
                    if(result.datalist != null && result.datalist.length > 0 ){

                        layer.open({
                            title:object.attr("data-title")+"：实例列表",
                            type: 1,
                            shadeClose: true,
                            // shade: false,
                            maxmin: true, //开启最大化最小化按钮
                            area: ['960px', '600px'],
                            content: "<pre style='margin:5px;'>" + getMachineList(result.datalist) + "</pre>",
                        });

                        // var e = object;
                        // e.popover({
                        //     content: getMachineList(result.datalist),
                        //     html: true
                        // }).popover('show');
                    }
                }
            });
    }


    // 详细列表绑定事件
    function bindDetailEvent(result) {
        if (result == null) {
            return;
        }
        $.each(result, function (index, item) {
            var id = item.configId;

            // 绑定删除事件
            $("#itemDel" + id).on("click", function (e) {
                deleteDetailTable(id, item.key);
            });

            $(".valuefetch" + id).on('click', function () {
                var e = $(this);
                // e.unbind('click');
                fetchConfigValue(id, e);
            });

            $(".machineinfo" + id).on('click', function () {
                var e = $(this);
                // e.unbind('click');
                fetchZkInfo(id, e);
            });

        });

    }

    // 删除
    function deleteDetailTable(id, name) {

        var ret = confirm("你确定要删除吗 " + name + "?");
        if (ret == false) {
            return false;
        }

        $.ajax({
            type: "DELETE",
            url: "/api/web/config/" + id
        }).done(function (data) {
            if (data.success === "true") {
                layer.alert(data.result,{closeBtn: 0},function (index) {
                    layer.close(index)
                });
            }else{
                layer.alert(data.message.global);
            }
        });
    }

    //
    function getTips() {
        if (appId == -1) {
            return "APP";
        }
        if (envId == -1) {
            return "环境";
        }
        return "参数";
    }

    //
    function fetchZkDeploy() {
        if ($("#zk_deploy_info").is(':hidden')) {
            var cc = '';
        } else {
            fetchZkDeployInfo();
        }
    }

    //
    // 获取ZK数据信息
    //
    function fetchZkDeployInfo() {

        $("#zk_deploy_info_pre").html("正在获取ZK信息，请稍等......");

        // 参数不正确，清空列表
        if (appId == -1 || envId == -1 || version == "#") {
            $("#zk_deploy_info_pre").html("无ZK信息");
            return;
        }

        var base_url = "/api/zoo/zkdeploy?appId=" + appId + "&envId=" + envId
            + "&version=" + version

        $.ajax({
            type: "GET",
            url: base_url
        }).done(function (data) {
            if (data.success === "true") {
                var html = data.result.hostInfo;
                if (html == "") {
                    $("#zk_deploy_info_pre").html("无ZK信息");
                } else {
                    $("#zk_deploy_info_pre").html(html);
                }
            }
        });
    }

    $("#zk_deploy_button").on('click', function () {
        $("#zk_deploy_info").toggle();
        fetchZkDeploy();
    });


    $("#view").on('click', function () {

        var title = $("#env_info").text() + $("#app_info").text()  +" | 版本：" + version;
        layer.open({
            type: 2,
            title: title,
            shadeClose: false,
            // shade: false,
            maxmin: true, //开启最大化最小化按钮
            area: ['960px', '600px'],
            content: "/app_version_detail.html?appId="+appId+"&envId="+envId+"&version="+version
        });

    });

})(jQuery);
