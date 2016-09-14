"use strict";

angular.module('app.dashboard')
    .directive('logs', ['$timeout','logsService', function($timeout,logsService) {

        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'modules/dashboard/directives/views/logs.tpl.html',
            scope: {

            },
            link: function (scope, element, attributes) {
                console.log('test1');

                element.on('click',".baloo-actions-text",function (event) {
                    event.preventDefault();

                    var parent = $(this).parent();
                    parent.siblings(".data-items").width("60%");
                    parent.siblings(".baloo-action-content").show();

                    event.stopPropagation();
                });

/*                $timeout(function(){
                        scope.$apply();
                },200);*/

                setInterval(function(){
                    logsService.updateLogsUi(username).then(function (res) {
/*                        console.log(res.data.data_value);
                        //$('#log-view').html(new Date() + ' : ' + res.data.data_value);
                        var cr = $('#log-view').html();
                        $('#log-view').html( cr + '<br><br><br><br><br><br><br><br><br><br>'+ res.data.data_value);
*//*                        var textarea = document.getElementById('log-view');
                        console.log(textarea.scrollHeight)
                        textarea.scrollTop = (- textarea.scrollHeight);*//*
                        window.scrollTo(0,document.body.scrollHeight);*/
                    });
                }, 1000);
            }
        }
    }]);

