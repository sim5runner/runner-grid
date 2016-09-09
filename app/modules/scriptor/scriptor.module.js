/**
 * Created by Shipra
 */
(function() {
  'use strict';

  var module = angular.module('automationApp.scriptor', [
    'ui.router',
	'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngTouch',
    'ui.bootstrap'
  ]);

  module.config(appConfig);

  appConfig.$inject = ['$stateProvider'];

  function appConfig($stateProvider) {
    $stateProvider
      .state('app.task-new', {
        url: '/task/new',
        templateUrl: 'modules/scriptor/views/newTask.html',
        controller: 'NewScriptController'
      })
  }
})();
