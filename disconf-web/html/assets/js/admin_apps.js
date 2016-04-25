(function ($) {

    getSession();

    var app = angular.module('listPage', ["bw.paging"]); //引入分页组件

    addInterceptor(app);

    angular.module('listPage').controller('listPageControl', ['$scope', '$log','$http', function($scope, $log,$http) {

        $scope.page = 1; //设置当前页数
        $scope.pageSize = 10;//设置一页多少条
        $scope.app = {};
        $scope.pagingAction = function(page,pageSize) {
            var param = {};
            param.page = page;
            param.size = pageSize;
            param.name = $scope.app.name;
            param.desc = $scope.app.desc;

            doPaging($http,"/api/app/listApp",param,function (data) {
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

        $scope.add = function () {
           location.href = "/newapp.html";
        };

        $scope.manageAppUser = function (id) {
           location.href = "/app_user_manage.html?id="+id;
        };
        $scope.delete = function (id) {

            layer.confirm('确定删除此APP吗？', {
                    btn: ['确认','取消'] //按钮
                }, function(index){
                    layer.close(index);
                    doDelete(id);
                }, function(){}
            );
        };

        function doDelete(id){

            $.ajax({
                url:"/api/app/delete",
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
