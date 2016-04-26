//
// 获取APP信息
//
var appId = -1;
var envId = -1;
var version = "";
var isNowExec = '1';
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
    fetchVersion();
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
        }
    });
$("#envChoice").on('click', 'li a', function () {
    $("#envChoiceA span:first-child").text($(this).text());
    envId = $(this).attr('rel');
    fetchVersion();
});


//
// 获取版本信息
//
function fetchVersion() {

    $("#versionChoiceA span:first-child").text("选择版本");
    version = "";
    if(appId < 1 || envId < 1){
        return;
    }
    $("#versionChoice").html("");
    $.ajax({
        type: "GET",
        url: "/api/web/configDraft/versionlist?appId=" + appId + "&envId="+envId
    }).done(function (data) {
        if (data.success === "true") {
            var html = "";
            var result = data.result;
            $.each(result, function (index, item) {
                html += '<li><a href="#">' + item + '</a></li>';
            });
            $("#versionChoice").html(html);
        }
    });
}

$("#versionChoice").on('click', 'li a', function () {
    $("#versionChoiceA span:first-child").text($(this).text());
    version = $(this).text();
});

$('#execTime').datetimepicker({
    //language:  'fr',
    weekStart: 1,
    todayBtn:  1,
    autoclose: 1,
    todayHighlight: 1,
    startView: 2,
    forceParse: 0,
    showMeridian: 1
});

$("[name='isNowExec']").change(function () {
    var radio = $(this).val();
    isNowExec = $(this).val();
    if(radio == '0'){
        $("#execTime").removeAttr("disabled");
    }else {
        $("#execTime").attr("disabled","disabled");
    }
});

$("#item_submit").on('click',function () {

    $("#error").addClass("hide");

    if (version == '自定义版本') {
        version = $('#selfversion_value').val();
    }

    var memo = $("#memo").val();
    // 验证
    var execTime = $("#execTime").val();
    if (appId < 1 || envId < 1 || version == "" || (isNowExec == '0' && (execTime == ''|| execTime == null))) {
        $("#error").removeClass("hide");
        $("#error").html("表单不能为空或填写格式错误！");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/api/web/configDraft/submit",
        data: {
            "appId": appId,
            "version": version,
            "envId": envId,
            "execNow": isNowExec,
            "execTime": execTime,
            "memo": memo
        }
    }).done(function (data) {
        $("#error").removeClass("hide");
        if (data.success === "true") {
            parent.layer.alert(data.result,{closeBtn: 0},function (index) {
                parent.layer.close(index);
                reloadDraftList();
                parent.layer.closeAll();
            });
        } else {
            parent.Util.input.whiteError($("#error"), data);
        }
    });

});

function reloadDraftList() {

    var appElement  = $(parent.document).find("[ng-controller=listPageControl]");
    var $scope = parent.angular.element(appElement).scope();
    $scope.search();

}