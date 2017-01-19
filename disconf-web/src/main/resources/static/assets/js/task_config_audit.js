getSession();
removeJumpUrlFromCookie();
var app = angular.module('myApp', []);
var id = getQueryString("id");
var taskAuditId = getQueryString("taskAuditId");

addInterceptor(app);
app.controller('validateCtrl',['$scope','$http', function($scope,$http) {

    $scope.task = {};
    $http({
        url:"/api/web/task/taskConfigDetail",
        method:'GET',
        params:{id:id}
    }).success(function(data,header,config,status){
        if(data.success){
            var task = data.result;
            if(task.auditStatus == "pass"){
                location.href = "/task_config_detail.html?id=" + task.id;
            }else{
                $scope.task = data.result;
            }
        }
    });
    $("[name=name]").attr("readonly","readonly");

    $scope.compare = function (id,name,draftType) {

        var title = "对比配置：" + name + " --> " + (draftType=='create'?'新建':draftType=='modify'?'修改':'删除');

        layer.open({
            type: 2,
            title: title,
            shadeClose: true,
            // shade: false,
            maxmin: true, //开启最大化最小化按钮
            area: ['1380px', '800px'],
            content: '/config_campare.html?id='+id
        });

    };

    $scope.taskAudit = function(status) {

        var auditComment = $("#auditComment").val();

        $.ajax({
            url:"/api/web/task/taskAudit",
            method:'POST',
            data:{id:id,taskAuditId:taskAuditId,status:status,auditComment:auditComment}
        }).done(
            function (data) {
                if(data.success == 'true'){
                    layer.alert(data.result,{closeBtn: 0},function () {
                        location.href = "/my_todo_task.html";
                    });
                }else{
                    layer.alert(data.message.global);
                }
            }
        );
    };

}]);
