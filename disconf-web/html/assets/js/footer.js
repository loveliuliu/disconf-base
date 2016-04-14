(function ($) {

    // 登出
    $("#signout").on("click", function () {
        $.ajax({
            type: "GET",
            url: "/api/account/signout"
        }).done(function (data) {

            if(data.result.logout == 'domainUser'){
                var img = new Image();
                img.src = "http://sso.ops.ymatou.cn/logout?service=http://localhost:8080";
                window.location.href = "http://portal.ymatou.cn";
                // window.location.href = "http://sso.ops.ymatou.cn/logout?url=http%3a%2f%2flocalhost%3a8080%2fapi%2fcas%2flogout";
            }else{
                window.location.href = "/login.html";
            }
        });
    });

})(jQuery);