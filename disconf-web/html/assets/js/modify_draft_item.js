(function ($) {

    getSession();

    var id = getQueryString("id");

    fetchItem();

    //
    // 获取配置项
    //
    function fetchItem() {

        //
        // 获取此配置项的数据
        //
        $.ajax({
            type: "GET",
            url: "/api/web/configDraft/findById?id=" + id
        }).done(
            function (data) {
                if (data.success === "true") {
                    var config = data.result.config;
                    var result = data.result.configDraft;
                    $("#app").text(
                            result.appName + ' (appid=' + result.appId
                            + ')');
                    $("#version").text(result.version);
                    $("#env").text(result.envName);
                    $("#key").text(result.name);
                    $("#value").val(result.value);
                    $("#draftOldValue").text(result.value);

                    $("#type").text(result.draftType=='create'?'新建':'修改');

                    if(result.draftType == 'modify'){
                        $("#configOldValue").parent().show();
                        $("#configOldValue").text(config.value);
                    }

                    $("#currentData").text(
                            result.appName + " * " + result.version + " * "
                            + result.envName);
                    // 获取APP下的配置数据
                    fetchItems(result.appId, result.envId, result.version,
                        id);
                }
            });
    }

    // 提交
    $("#submit").on("click", function (e) {
        $("#error").addClass("hide");
        var me = this;
        var value = $("#value").val();
        // 验证
        if (!value) {
            $("#error").removeClass("hide");
            $("#error").html("表单不能为空或填写格式错误！");
            return;
        }
        $.ajax({
            type: "PUT",
            url: "/api/web/configDraft/item/" + id,
            data: {
                "value": value
            }
        }).done(function (data) {
            if (data.success === "true") {
                layer.alert(data.result,{closeBtn: 0},function (index) {
                    layer.close(index);
                    location.reload();
                });
            } else {
                $("#error").removeClass("hide");
                Util.input.whiteError($("#error"), data);
            }
        });
    });

})(jQuery);
