/**
 * Created by AbhishekK on 9/19/2016.
 */

var baseUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port : '');

$.get(baseUrl+"/stat/server", function(data, status){
    var per = 0;
    try{
        per = (parseInt(data.memory.total)-parseInt(data.memory.free)) / parseInt(data.memory.total);
    } catch(er){console.log(er)}

    AnimateCircle("circle-container", per.toFixed(2));
});

//https://github.com/AbhishekCompro/progressbar.js
function AnimateCircle(container_id, animatePercentage) {
    var startColor = '#6FD57F';
    var endColor = '#FC5B3F';

    var element = document.getElementById(container_id);
    $( "#"+container_id ).empty();
    var circle = new ProgressBar.Circle(element, {
        color: startColor,
        trailColor: '#eee',
        trailWidth: 5,
        duration: 2000,
        easing: 'bounce',
        strokeWidth: 5,
        text: {
            value: (animatePercentage * 100).toFixed(2) + "%",
            className: 'progressbar__label'
        },
        // Set default step function for all animate calls
        step: function (state, circle) {
            circle.path.setAttribute('stroke', state.color);
        }
    });

    setInterval(function(){
        $.get(baseUrl+"/stat/server", function(data, status){
            var per = 0;
            try{
                per = (parseInt(data.memory.total)-parseInt(data.memory.free)) / parseInt(data.memory.total);
            } catch(er){console.log(er)}
            circle.text.textContent = (per * 100).toFixed(2) + "%"
            circle.animate(per.toFixed(2), {
                from: {
                    color: startColor
                },
                to: {
                    color: endColor
                }
            });
        });
    }, 3000);

};

$(function () {
    $(document).ready(function () {
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });

        $.get(baseUrl+"/stat/server", function(data, status){
            try{
                var maxServerMemory = parseInt((parseInt(data.memory.total) / 1024).toFixed(2));
                $('#container').highcharts({
                    chart: {
                        type: 'spline',
                        animation: Highcharts.svg, // don't animate in old IE
                        marginRight: 10,
                        events: {
                            load: function () {

                                // set up the updating of the chart each second
                                var series = this.series[0];
                                setInterval(function () {
                                    $.get(baseUrl+"/stat/server", function(data, status){
                                        var usedMemory = 0;
                                        try{
                                            usedMemory = ((parseFloat(data.memory.total)-parseFloat(data.memory.free)) / 1024).toFixed(2);
                                        } catch (er){
                                            console.log(err);
                                        }
                                        var x = (new Date()).getTime(), // current time
                                            y = parseFloat(usedMemory);
                                        series.addPoint([x, y], true, true);
                                    });
                                }, 2000);
                            }
                        }
                    },
                    title: {
                        text: 'Grid Memory | Logger - Client'
                    },
                    xAxis: {
                        type: 'datetime',
                        tickPixelInterval: 150
                    },
                    yAxis: {
                        title: {
                            text: 'Grid Memory: '+ ((data.memory.total) / 1024).toFixed(2) + ' GB'
                        },
                        min: 0,
                        max: maxServerMemory,
                        plotLines: [{
                            value: (maxServerMemory + 0.2),
                            color: 'red',
                            dashStyle: 'shortdash',
                            width: 2,
                            label: {
                                text: 'Maximum memory'
                            }
                        }]
                    },
                    tooltip: {
                        formatter: function () {
                            return '<b>' + this.series.name + '</b><br/>' +
                                Highcharts.numberFormat(this.y, 2) + ' GB';
                        }
                    },
                    legend: {
                        enabled: false
                    },
                    exporting: {
                        enabled: false
                    },
                    series: [{
                        name: 'Memory Used',
                        data: (function () {
                            // generate an array of random data
                            var data = [],
                                time = (new Date()).getTime(),
                                i;

                            for (i = -19; i <= 0; i += 1) {
                                data.push({
                                    x: time + i * 1000,
                                    y: null
                                });
                            }
                            return data;
                        }())
                    }]
                });


            } catch (er){
                console.log(err);
            }
        });

    });
});

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