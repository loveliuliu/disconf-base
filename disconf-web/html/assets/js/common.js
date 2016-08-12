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
        if(VISITOR.role == 1){
            removeMenu($(".dropdown-menu:not('#applist')").find("li").get(3));
        }
        if(VISITOR.role == 3){
        	removeMenu($("#createApp"));
        }
    } else {
        $(".login-no").show();
        $(".login-yes").hide();
        $("#username").hide();
    }
}

function removeMenu($element) {
    if($element){
        $element.remove();
    }
}

//
// 登录其它的控制
//
function loginActions() {
    if (VISITOR.id != '') {
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

    loginActions();
}

// 获取是否登录并且进行跳转
function getSession2Redirect() {
    $.ajax({
        type: "GET",
        url: "/api/account/session"
    }).done(function (data) {
        if (data.success === "true") {
            if(data.result.visitor.role == "1" || data.result.visitor.role == "3") {
                window.location.href = "/main.html";
            }else{
                window.location.href = "/admin_users.html";
            }
        }
    });
    loginActions();
}

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


function addInterceptor(app) {
    // 定义一个 Service ，稍等将会把它作为 Interceptors 的处理函数
    app.factory('HttpInterceptor', ['$q', HttpInterceptor]);

    function HttpInterceptor($q) {
        return {
            request: function(config){
                return config;
            },
            requestError: function(err){
                return $q.reject(err);
            },
            response: function(res){
                return res;
            },
            responseError: function(err){
                return $q.reject(err);
            }
        };
    }

// 添加对应的 Interceptors
    app.config(['$httpProvider', function($httpProvider){
        $httpProvider.interceptors.push(HttpInterceptor);
    }]);
}

function removeJumpUrlFromCookie() {
    Util.cookie.remove("jumpUrl");
}

function goBack() {
    location.href=document.referrer;
}