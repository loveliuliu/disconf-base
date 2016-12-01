getSession();
var app = angular.module('myApp', []);

addInterceptor(app);
app.controller('validateCtrl',['$scope','$http', function($scope,$http) {

    var id = getQueryString("id");

    $scope.hasAuth = getAuth("updateAppUserManager");

    //获取app数据
    $http({
        url:"/api/app/findById",
        method:'GET',
        params:{id:id}
    }).success(function(data,header,config,status){
        if(data.success){
            $scope.app = data.result;
        }
    });

    //获取app 已中数据
    $http({
        url:"/api/app/selectedUsers",
        method:'GET',
        params:{id:id}
    }).success(function(data,header,config,status){
        if(data.success){
            $scope.normalSelected = data.result.normalSelected;
            $scope.auditSelected = data.result.auditSelected;
            $scope.normalUsers = data.result.normalUsers;
            $scope.auditUsers = data.result.auditUsers;
        }
    });

    jQuery(document).ready(function($) {
        $('#undo_redo').multiselect({

            search: {
                left: '<input type="text" name="q" class="form-control" style="width: 95%;" placeholder="Search..." />',
                right: '<input type="text" name="q" class="form-control" style="width: 95%;"  placeholder="Search..." />',
            }

        });

        $('#auditFrom').multiselect({

            search: {
                left: '<input type="text" name="q" class="form-control" style="width: 95%;" placeholder="Search..." />',
                right: '<input type="text" name="q" class="form-control" style="width: 95%;"  placeholder="Search..." />',
            }

        });

    });


    $scope.submitForm = function(isValid) {
        if (isValid) {

            var opts = $("[name='normalSelected'] option");
            var normalSelectedIds = "";
            opts.each(function (i,item) {
                if(i != 0){
                    normalSelectedIds +=",";
                }
                normalSelectedIds += $(item).val();
            });

            var auditOpts = $("[name='auditSelected'] option");
            var auditSelectedIds = "";
            auditOpts.each(function (i,item) {
                if(i != 0){
                    auditSelectedIds +=",";
                }
                auditSelectedIds += $(item).val();
            });

            $.ajax({
                url:"/api/app/appUserManage",
                method:'POST',
                data:{id:id,normalSelectedIds:normalSelectedIds,auditSelectedIds:auditSelectedIds}
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
