getSession();
var app = angular.module('myApp', []);
app.controller('validateCtrl',['$scope','$http', function($scope,$http) {

    //获取role select
    $http({
        url:"/api/role/findAll",
        method:'GET'
    }).success(function(data,header,config,status){
        if(data.success){
            $scope.roles = data.result;
        }
    });


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
