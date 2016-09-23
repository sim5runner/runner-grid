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


$('.flip').click(function(){
    $(this).find('.card').toggleClass('flipped');
    $('#log-div').css('margin-top','150px');
});

/**
 * Topband Updates
 */

// API call to get running test for particular ip

