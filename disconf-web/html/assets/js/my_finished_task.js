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

            doPaging($http,"/api/web/task/findMyFinishedTask",param,function (data) {
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
        
        $scope.delete = function (id) {

            layer.confirm('确定撤销执行此任务吗？', {
                    btn: ['确认','取消'] //按钮
                }, function(index){
                    layer.close(index);
                    doDelete(id);
                }, function(){}
            );
        };

        function doDelete(id){

            $.ajax({
                url:"/api/web/task/cancelExec",
                method:'POST',
                data:{id:id}
            }).done(
                function (data) {
                    if(data.success == 'true'){
                        layer.alert(data.result,{closeBtn: 0},function (index) {
                            $scope.search();//删除先再次查询
                            layer.close(index)
                        });
                    }else{
                        layer.alert(data.message.global);
                    }
                }
            );
        }
    }]);
    
})(jQuery);
