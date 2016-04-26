getSession();
var app = angular.module('myApp',[]);
var appId = getQueryString("appId");
var envId = getQueryString("envId");
var version = getQueryString("version");

addInterceptor(app);

app.controller('validateCtrl',['$scope','$http', function($scope,$http) {
    $scope.configList = {};
    $http({
        url:"/api/web/config/list",
        method:'GET',
        params:{appId:appId,envId:envId,version:version,needValue:true}
    }).success(function(data,header,config,status){
            if(data.success){
                $scope.configList = data.page.result;
                setTimeout(function () {
                    window.prettyPrint && prettyPrint();
                },100)
            }
        });
}]);
