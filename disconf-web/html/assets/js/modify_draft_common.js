//
// 获取指定配置下的配置数据
//
function fetchItems(appId, envId, version, currentId) {

    var url = "/api/web/configDraft/list";
    url += "?";
    url += "appId=" + appId + "&";
    url += "envId=" + envId + "&";
    url += "version=" + version + "&";
    url += "draftTypeStr=create,modify&";
    url += "page=0&size=10000";

    $.ajax({
        type: "GET",
        url: url
    }).done(function (data) {
        if (data.success === "true") {
            var html = '<li style="margin-bottom:10px">配置文件/配置项列表</li>';
            var result = data.result.content;
            $.each(result, function (index, item) {
                html += renderItem(item);
            });
            $("#sidebarcur").html(html);
        }
    });
    var mainTpl = $("#tItemTpl").html();
    // 渲染主列表
    function renderItem(item) {

        var link = "";
        var key = "";
        if (item.type == "0") {
            link = 'modify_draft_file.html?id=' + item.id;
            key = '<i title="配置文件" class="icon-file"></i>' + item.name;
        } else {
            link = 'modify_draft_item.html?id=' + item.id;
            key = '<i title="配置项" class="icon-leaf"></i>' + item.name;
        }

        var style = "";
        if (item.id == currentId) {
            style = "active";
        }

        return Util.string.format(mainTpl, key, link, style);
    }
}
