getSession();

var id = getQueryString("id");

if(id != undefined && id!=''){
    $.ajax({
        type: "POST",
        url: "/api/tag/findById",
        data: {
            "id": id
        }
    }).done(function (data) {
        if (data.success === "true") {
            $("#tagName").val(data.result.tagName);
            $("#tagValue").val(data.result.tagValue);
            $("#memo").val(data.result.memo);
        }
    });

    $(".tagname").html("修改标签");
}else {
    $(".tagname").html("新建标签");
}

// 提交
$("#item_submit").on("click", function (e) {
    $("#error").addClass("hide");
    var tagName = $("#tagName").val();
    var tagValue = $("#tagValue").val();
    var memo = $("#memo").val();

    // 验证
    if (!tagValue || !tagName) {
        $("#error").removeClass("hide");
        $("#error").html("表单不能为空或填写格式错误！");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/api/tag/save",
        data: {
            "tagName": tagName,
            "tagValue": tagValue,
            "memo": memo,
            "id": id
        }
    }).done(function (data) {
        $("#error").removeClass("hide");
        if (data.success === "true") {
            $("#error").html(data.result);
            if(id == undefined || id == ''  ){
                $("#tagName").val("");
                $("#tagValue").val("");
                $("#desc").val("");
            }
        } else {
            Util.input.whiteError($("#error"), data);
        }
    });
});
