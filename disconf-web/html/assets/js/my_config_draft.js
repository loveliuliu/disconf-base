(function ($) {

    getSession();

    var app = angular.module('listPage', ["bw.paging"]); //引入分页组件

    addInterceptor(app);

    angular.module('listPage').controller('listPageControl', ['$scope', '$log','$http', function($scope, $log,$http) {

        $scope.page = 1; //设置当前页数
        $scope.pageSize = 20;//设置一页多少条
        $scope.draft = {};
        $scope.pagingAction = function(page,pageSize) {
            var param = {};
            param.page = page;
            param.size = pageSize;
            param.appName = $scope.draft.appName;
            param.envName = $scope.draft.envName;
            param.version = $scope.draft.version;
            param.sort = "create_time,desc";

            doPaging($http,"/api/web/configDraft/list",param,function (data) {
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

        $scope.modify = function (id,type) {
            var url = type == 0 ? '/modify_draft_file.html' : '/modify_draft_item.html';
            location.href = url + "?id="+id;
        };

        $scope.submitDraft = function () {

            var title = "提交草稿";

            layer.open({
                type: 2,
                title: title,
                shadeClose: false,
                // shade: false,
                maxmin: true, //开启最大化最小化按钮
                area: ['800px', '650px'],
                content: '/config_draft_submit.html'
            });

        };

        $scope.detail = function (id,name,draftType) {

            var title = "对比配置：" + name + " --> " + (draftType=='create'?'新建':draftType=='modify'?'修改':'删除');

            layer.open({
                type: 2,
                title: title,
                shadeClose: false,
                // shade: false,
                maxmin: true, //开启最大化最小化按钮
                area: ['960px', '600px'],
                content: '/config_campare.html?id='+id
            });

        };

        $scope.delete = function (id) {

            layer.confirm('确定删除此条草稿吗？', {
                    btn: ['确认','取消'] //按钮
                }, function(index){
                    layer.close(index);
                    doDelete(id);
                }, function(){}
            );
        };

        function doDelete(id){

            $.ajax({
                url:"/api/web/configDraft/delete",
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
