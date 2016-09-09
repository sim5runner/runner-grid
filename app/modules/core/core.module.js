(function() {
  'use strict';

  var module = angular.module('automationApp.core', [
    'ui.router',
	'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngTouch',
    'ui.bootstrap'
  ]);

  module.config(appConfig);

  appConfig.$inject = ['$stateProvider', '$urlRouterProvider'];

  function appConfig($stateProvider, $urlRouterProvider) {
    $stateProvider
      .state('app', {
        url: '',
        abstract: true,
        controller: 'AppController',
        templateUrl: 'modules/core/app.html'
      });
	  
	  $urlRouterProvider.otherwise(function ($injector) {
          var $state = $injector.get('$state');
          $state.go('app.task-new');
      });
  }
})();
