/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

(function ($) {

    getSession();

    var app = angular.module('listPage', ["bw.paging"]); //引入分页组件

    addInterceptor(app);

    angular.module('listPage').filter('limit', function () {
        return function (value, wordwise, max, tail) {
            if (!value) return '';

            max = parseInt(max, 10);
            if (!max) return value;
            if (value.length <= max) return value;

            value = value.substr(0, max);
            if (wordwise) {
                var lastspace = value.lastIndexOf(' ');
                if (lastspace != -1) {
                    value = value.substr(0, lastspace);
                }
            }

            return value + (tail || ' …');
        };
    });
    angular.module('listPage').controller('listPageControl', ['$scope', '$log','$http', function($scope, $log,$http) {

        $scope.page = 1; //设置当前页数
        $scope.pageSize = 10;//设置一页多少条
        $scope.tag = {};
        $scope.pagingAction = function(page,pageSize) {
            var param = {};
            param.page = page;
            param.size = pageSize;
            param.tagName = $scope.tag.tagName;
            param.tagValue = $scope.tag.tagValue;
            param.memo = $scope.tag.memo;

            doPaging($http,"/api/tag/list",param,function (data) {
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
           location.href = "/tag_add.html";
        };

        $scope.modify = function (id) {
            location.href = "/tag_add.html?id="+id;
        };

        $scope.manageAppUser = function (id) {
           location.href = "/app_user_manage.html?id="+id;
        };

        $scope.viewAppInfos = function (id,appInfos) {
            var html = '';
            if(appInfos != undefined &&　appInfos != ''){
                appInfos.split(",").forEach(function (element) {
                    html += element +"<br>"
                });
                layer.tips(html,"#appInfo"+id,{
                    tips: [2, '#3595CC'],
                    time: 10000,
                    maxWidth: 600,
                    closeBtn:1
                });
            }

        };
        
        $scope.delete = function (id) {

            layer.confirm('确定删除此标签吗？', {
                    btn: ['确认','取消'] //按钮
                }, function(index){
                    layer.close(index);
                    doDelete(id);
                }, function(){}
            );
        };

        function doDelete(id){

            $.ajax({
                url:"/api/tag/delete",
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
