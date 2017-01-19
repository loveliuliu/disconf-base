getSession();
var app = angular.module('myApp', []);
var id = getQueryString("id");

addInterceptor(app);
app.controller('validateCtrl',['$scope','$http', function($scope,$http) {

    $scope.title = "修改用户";
    $scope.task = {};
    $http({
        url:"/api/web/task/taskConfigDetail",
        method:'GET',
        params:{id:id}
    }).success(function(data,header,config,status){
        if(data.success){
           $scope.task = data.result;
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

    $scope.submitForm = function(isValid) {
        if (isValid) {
            $.ajax({
                url:"/api/account/save",
                method:'POST',
                data:$("[name='myForm']").serialize()
            }).done(
                function (data) {
                    if(data.success == 'true'){
                        layer.alert(data.result,{closeBtn: 0},function () {
                            location.reload();
                        });
                    }else{
                        layer.alert(data.message.global);
                    }
                }
            );
        }
    };

}]);
