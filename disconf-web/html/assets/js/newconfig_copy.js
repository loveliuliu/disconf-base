
var appId = -1;
var envId = -1;
var version = "";
var newEnvId = -1;
var newVersion = "";
getSession();

// 提交
$("#item_submit").on("click", function (e) {
    $("#error").addClass("hide");

    if (version == '自定义版本') {
        version = $('#selfversion_value').val();
    }
    if(newVersion == '自定义版本'){
        newVersion = $("#selfversionDest_value").val();
    }

    // 验证
    if (appId < 1 || envId < 1 || version == "" || newEnvId < 1 || newVersion == "") {
        $("#error").removeClass("hide");
        $("#error").html("表单不能为空或填写格式错误！");
        return;
    }
    if (version == newVersion && envId == newEnvId) {
        $("#error").removeClass("hide");
        $("#error").html("配置源不能和配置目的一样！");
        return;
    }
    var needConfirm = false;
    $.ajax({
        async:false,
        type: "POST",
        url: "/api/web/config/isEnvAndVersionExist",
        data: {
            "appId": appId,
            "newVersion": newVersion,
            "newEnvId": newEnvId
        }
    }).done(function (data) {
        needConfirm = data.result;
    });

    if(needConfirm){
        layer.confirm('目的版本已存在配置，肯定覆盖吗？', {
                btn: ['确认','取消'] //按钮
            }, function(index){
                layer.close(index);
                configCopy();
            }, function(){}
        );
    }else{
        configCopy();
    }

});

function configCopy() {
    $.ajax({
        type: "POST",
        url: "/api/web/config/copy",
        data: {
            "appId": appId,
            "version": version,
            "envId": envId,
            "newVersion": newVersion,
            "newEnvId": newEnvId
        }
    }).done(function (data) {
        $("#error").removeClass("hide");
        if (data.success === "true") {
            $("#error").html(data.result);
            clearDest();
        } else {
            Util.input.whiteError($("#error"), data);
        }
    });
}
//清空目的配置
function clearDest(){

    newVersion = "";
    newEnvId = -1;

    $("#versionChoiceD span:first-child").text("选择版本");

    $("#selfversionDest_value").val("");
    $("#selfversionDest").hide();

    $("#envChoiceD span:first-child").text("选择坏境");

    fetchVersion(appId);

}

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
            $.each(result, function (index, item) {
                html += '<li><a rel=' + item.id + ' href="#">APP: '
                    + item.name + '</a></li>';
            });
            $("#appChoice").html(html);
        }
    });

$("#appChoice").on('click', 'li a', function () {
    $("#appChoiceA span:first-child").text($(this).text());
    appId = $(this).attr('rel');
    fetchVersion(appId);
});

//
// 获取版本信息
//
function fetchVersion(appId) {

    $("#versionChoiceA span:first-child").text("选择版本");
    version = "";
    newVersion = "";

    $.ajax({
        type: "GET",
        url: "/api/web/config/versionlist?appId=" + appId
    }).done(function (data) {
        if (data.success === "true") {
            var html = "";
            var result = data.page.result;
            $.each(result, function (index, item) {
                html += '<li><a href="#">' + item + '</a></li>';
            });
            html += '<li><a href="#">' + "自定义版本" + '</a></li>';
            $("#versionChoice").html(html);
            $("#versionChoiceDest").html(html);
        }
    });


}

$("#versionChoice").on('click', 'li a', function () {
    $("#versionChoiceA span:first-child").text($(this).text());
    version = $(this).text();
    if (version == '自定义版本') {
        $("#selfversion").show();
    } else {
        $("#selfversion").hide();
    }
});

$("#versionChoiceDest").on('click', 'li a', function () {
    $("#versionChoiceD span:first-child").text($(this).text());
    newVersion = $(this).text();
    if (newVersion == '自定义版本') {
        $("#selfversionDest").show();
    } else {
        $("#selfversionDest").hide();
    }
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
                html += '<li><a rel=' + item.id + ' href="#">环境:'
                    + item.name + '</a></li>';
            });
            $("#envChoice").html(html);
            $("#envChoiceDest").html(html);
        }
    });
$("#envChoice").on('click', 'li a', function () {
    $("#envChoiceA span:first-child").text($(this).text());
    envId = $(this).attr('rel');
});

$("#envChoiceDest").on('click', 'li a', function () {
    $("#envChoiceD span:first-child").text($(this).text());
    newEnvId = $(this).attr('rel');
});