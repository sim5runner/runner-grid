(function() {
  //'use strict';

  angular.module('automationApp.core')
    .controller('AppController', AppController);
	
AppController.$inject = ['$scope','$location','$state', '$rootScope'];
  function AppController($scope, $location, $state, $rootScope ) {

	  $scope.loadTaskId = "";

  }

})();