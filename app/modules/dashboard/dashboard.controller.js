angular.module('app.dashboard')
	.controller('DashboardController', ['$rootScope', '$scope','$location', '$state', 'dashboardService',
		function($rootScope, $scope, $location, $state, scriptorService) {

            $scope.taskId = "";
            $scope.copy_sle_id = "";
            scriptorService.taskContent = {};

                scriptorService.getGlobalContext().then(function (res) {
                    $rootScope.globalConstants = res.data;
                });

		}]);
