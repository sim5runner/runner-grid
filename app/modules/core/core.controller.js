(function() {
  'use strict';

  angular.module('app.core')
    .controller('AppController', AppController);
	
AppController.$inject = ['$scope','$location','$state', '$rootScope'];
  function AppController($scope, $location, $state, $rootScope ) {

	  $scope.id = "";

  }

})();