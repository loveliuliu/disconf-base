getSession();
var app = angular.module('myApp', []);
var userId = getQueryString("userId");

addInterceptor(app);
app.controller('validateCtrl',['$scope','$http', function($scope,$http) {
    //获取role select
    $http({
        url:"/api/role/findAll",
        method:'GET',
    }).success(function(data,header,config,status){
        if(data.success){
            $scope.roles = data.result;

        }
    });

    if(undefined!=userId && userId!=''){
        $scope.title = "修改用户";
        $http({
            url:"/api/account/findById",
            method:'GET',
            params:{userId:userId}
        }).success(function(data,header,config,status){
            if(data.success){
                var user = data.result;
                $scope.user = user;
                $scope.roleId = user.roleId.toString();
            }
        });
        $("[name=name]").attr("readonly","readonly");
    }else {
        $scope.title = "新建用户";
    }

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
