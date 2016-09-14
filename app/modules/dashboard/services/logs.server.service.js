'use strict';

angular.module('app.dashboard')
.factory('logsService', ['$rootScope', '$filter' , '$http', '$q', function($rootScope, $filter, $http, $q) {

        /***************** APIs ********************/

        var updateLogsUi = function(username ) {
            var globalContext = $http.get('api/data/' + username + '/logs');
            console.log('api/data/' + username + '/logs');
            var deferred = $q.defer();
            deferred.resolve(globalContext);
            return deferred.promise;
        };

        var getGlobalConstants = function() {
            var globalContext = $http.get('data/global_constants.json');
            var deferred = $q.defer();
            deferred.resolve(globalContext);
            return deferred.promise;
        };

        return {
        "taskContent" : {},
        "updateLogsUi": updateLogsUi,
        "getGlobalConstants": getGlobalConstants
    };
}]);
