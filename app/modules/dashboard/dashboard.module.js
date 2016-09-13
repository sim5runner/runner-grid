(function() {
  'use strict';

  var module = angular.module('app.dashboard', [
    'ui.router',
    'ngResource',
    'ui.bootstrap'
  ]);

  module.config(appConfig);

  appConfig.$inject = ['$stateProvider'];

  function appConfig($stateProvider) {
    $stateProvider
      .state('app.dashboard', {
        url: '/dashboard',
        templateUrl: 'modules/dashboard/dashboard.html',
        controller: 'DashboardController'
      })
  }
})();
