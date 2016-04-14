// 初始入口
(function () {
    window.VISITOR = {};
})();

//
// 头部显示初始化
//
function headShowInit() {
    if (VISITOR.id) {
        $(".login-no").hide();
        $(".login-yes").show();
        $("#username").show();
        $("#username").html(VISITOR.name);
    } else {
        $(".login-no").show();
        $(".login-yes").hide();
        $("#username").hide();
    }
}

//
// 登录其它的控制
//
function loginActions() {
    if (VISITOR.id) {
        $("#brand_url").attr("href", "/main.html");
    } else {
        $("#brand_url").attr("href", "/");
    }
}

//
// 获取Session信息
//
function getSession() {
    $.ajax({
        type: "GET",
        url: "/api/account/session",
        timeout: 3000 // 3s timeout
    }).done(function (data) {
        if (data.success === "true") {
            window.VISITOR = data.result.visitor;
            headShowInit();
        } else {
            window.location.href = "/login.html";
        }
    }).fail(function (xmlHttpRequest, textStatus) {
        window.location.href = "/login.html";
    });
}

// 获取是否登录并且进行跳转
function getSession2Redirect() {
    $.ajax({
        type: "GET",
        url: "/api/account/session"
    }).done(function (data) {
        if (data.success === "true") {
            window.location.href = "/main.html";
        } else {
        }
    });
    loginActions();
}

//重写url
$.ajaxPrefilter( function( options ) {
    if(sessionStorage.system && sessionStorage.system == 1 ){
        options.url  = options.url.lastIndexOf("?")!=-1?(options.url+'&system=1'):(options.url+"?system=1");
    }
});

$.ajaxSetup({
    error: function (XMLHttpRequest, textStatus, errorThrown) {
        var system = getQueryString("system");
        if((system == null || system == 'undefined') && (sessionStorage.system == null || sessionStorage.system == 0) ){
            sessionStorage.system = 0;
            location.href = "http://sso.ops.ymatou.cn/login?service=http://localhost:8080/api/cas/login";
        }else{
            sessionStorage.system = 1;
        }
    }
});


function getQueryString(name, url) {
    var str = url || document.location.search || document.location.hash,
        result = null;

    if (!name || str === '') {
        return result;
    }

    result = str.match(
        new RegExp('(^|&|[\?#])' + name + '=([^&]*)(&|$)')
    );

    return result === null ? result : decodeURIComponent(result[2]);
}
