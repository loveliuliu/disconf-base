(function ($) {

    getSession();

    var app = angular.module('listPageApp', ["bw.paging"]); //引入分页组件

    addInterceptor(app);
    
    angular.module('listPageApp').controller('listPageControl', ['$scope', '$log','$http', function($scope, $log,$http) {

        $scope.page = 1; //设置当前页数
        $scope.pageSize = 10;//设置一页多少条
        $scope.user = {};
        $scope.pagingAction = function(page,pageSize) {
            var param = {};
            param.page = page;
            param.size = pageSize;
            param.name = $scope.user.name;
            param.roleId = $scope.user.roleId;

            doPaging($http,"/api/account/list",param,function (data) {
                if(data.success){
                    $scope.total = data.result.totalElements;
                    $scope.userList = data.result.content;
                }
            });
        };
        $scope.pagingAction($scope.page,$scope.pageSize);

        //获取role select
        $http({
            url:"/api/role/findAll",
            method:'GET'
        }).success(function(data,header,config,status){
            if(data.success){
                $scope.roles = data.result;
            }
        });
        
        $scope.search = function () {
            $scope.page = 1;
            $scope.pagingAction($scope.page,$scope.pageSize);
        };

        $scope.add = function () {
           location.href = "/user_add.html";
        };

        $scope.modifyUser = function (userId) {
            location.href = "/user_add.html?userId="+userId;
        };

        $scope.deleteUser = function (userId) {

            layer.confirm('确定删除此用户吗？', {
                    btn: ['确认','取消'] //按钮
                }, function(index){
                    layer.close(index);
                    doDeleteUser(userId);
                }, function(){}
            );
        };

        function doDeleteUser(userId){

            $.ajax({
                url:"/api/account/delete",
                method:'POST',
                data:{userId:userId}
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
