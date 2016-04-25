(function ($) {

    getSession();

    var app = angular.module('listPage', ["bw.paging"]); //引入分页组件

    addInterceptor(app);


    angular.module('listPage').controller('listPageControl', ['$scope', '$log','$http', function($scope, $log,$http) {

        $scope.page = 1; //设置当前页数
        $scope.pageSize = 20;//设置一页多少条
        $scope.task = {};
        $scope.pagingAction = function(page,pageSize) {
            var param = {};
            param.page = page;
            param.size = pageSize;
            param.appName = $scope.task.appName;
            param.envName = $scope.task.envName;
            param.version = $scope.task.version;

            doPaging($http,"/api/web/task/findMyDoneTask",param,function (data) {
                if(data.success){
                    $scope.total = data.result.totalElements;
                    $scope.dataList = data.result.content;
                }
            });
        };
        $scope.pagingAction($scope.page,$scope.pageSize);

        
        $scope.search = function () {
            $scope.page = 1;
            $scope.pagingAction($scope.page,$scope.pageSize);
        };

        $scope.view = function (id) {
            location.href = "/task_config_detail.html?id="+id;
        };
        
    }]);
    
})(jQuery);
