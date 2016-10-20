/**
 * Created by AbhishekK on 9/22/2016.
 */

$(document).ready(function() {
    var $window = $(window);

    function checkWidth() {
        var windowsize = $window.width();
        if (windowsize < 1000) {
            $('#circle-container').hide();
            $('#container').width('90%');
        } else {
            $('#circle-container').show();
            $('#container').width('75%');
        }
    }
    // Execute on load
    checkWidth();
    // Bind event listener
    $(window).resize(checkWidth);
});

$("#grid-url").attr('href', location.protocol+'//'+location.hostname + ':4444/grid/console');

var baseUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port : '');

    setInterval(function(){
        $.get(baseUrl+"/stat/tests", function(data, status){
            console.log(data.tests);
            if (data.tests.length){

                function writeToDom(content) {
                    $("#tests").append("<div><pre>" + content + "</pre></div>");
                }

                function showResults(evnt) {
                    $("#tests").empty();
                    $("#tests").append("<div class='header'>" + 'Active Tests' + ":</div>");

                    $.each(data.tests, function( index, value ) {
                        writeToDom(JSON.stringify(value, null, 4));
                    });
                }
                showResults();

            } else {
                $('#tests').html('No Active Tests');
            }

        });
    }, 5000);


$(document).ready(function() {
    $(document).delegate('.open', 'click', function(event){
        $(this).addClass('oppenned');
        event.stopPropagation();
    })
    $(document).delegate('body', 'click', function(event) {
        $('.open').removeClass('oppenned');
    })
    $(document).delegate('.cls', 'click', function(event){
        $('.open').removeClass('oppenned');
        event.stopPropagation();
    });

    $("#script-dn").click(function(){
        var batFileContent = '@echo off' +'\n' +
            'echo enter username:' + '\n' +
            'set /p username=""' +  '\n' +
            'echo connecting browser node to grid..' + '\n' +
            'java -jar selenium-server-standalone-2.41.0.jar -role webdriver -hub ' +
            location.protocol+'//'+location.hostname + ':4444/grid//register -browser browserName="chrome",version=ANY,platform=WINDOWS,maxInstances=5,applicationName=%username% -Dwebdriver.chrome.driver=chromedriver.exe -port 6666'
        download(batFileContent, "browser-connect.bat", "text/plain");
    });
});


/**
 * Topband dd
 */

$(".account").click(function() {
    var X = $(this).attr('id');
    if (X == 1) {
        $(".submenu").hide();
        $(this).attr('id', '0');
    }
    else {
        $(".submenu").show();
        $(this).attr('id', '1');
    }
});

//Mouse click on sub menu
$(".submenu").mouseup(function() {
    return false
});

//Mouse click on my account link
$(".account").mouseup(function() {
    return false
});

//Document Click
$(document).mouseup(function() {
    $(".submenu").hide();
    $(".account").attr('id', '');
});