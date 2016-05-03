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
                var tempArr = new Array(data.page.result.length);
                var length = 1;
                var temp = new Object();
                var tempValue = "";
                var firstResult = data.page.result[0];
                if(firstResult != null && firstResult != undefined) {
                    for (var index = 0; index < data.page.result.length; ++index) {
                        if (data.page.result[index].typeId == 1) {
                            tempValue += data.page.result[index].key + "=" +
                                data.page.result[index].value + "\n";
                        } else {
                            tempArr[length++] = data.page.result[index];
                        }
                    }
                    temp.configId = 0;
                    temp.appId = firstResult.appId;
                    temp.appName = firstResult.appName;
                    temp.envId = firstResult.envId;
                    temp.version = firstResult.version;
                    temp.value = tempValue;
                    temp.key = "";
                    temp.typeId = "1";
                    temp.type = "所有配置项";
                    tempArr[0] = temp;
                    tempArr.length = length;
                    data.page.result = tempArr;
                }
                $scope.configList = data.page.result;
                setTimeout(function () {
                    window.prettyPrint && prettyPrint();
                },100)
            }
        });
}]);
