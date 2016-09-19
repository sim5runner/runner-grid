/**
 * Created by AbhishekK on 9/19/2016.
 */

var baseUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port : '');

setInterval(function(){
    $.get(baseUrl+"/stat/server", function(data, status){
        console.log(data);
        var per = 0;
        try{
            per = (parseInt(data.memory.total)-parseInt(data.memory.free)) / parseInt(data.memory.total);
        } catch(er){console.log(er)}

        console.log(per.toFixed(2));
        AnimateCircle("example-animation-container", per.toFixed(2));
    });
}, 15000);

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
            value: (animatePercentage * 100) + " % grid memory used.",
            className: 'progressbar__label'
        },
        // Set default step function for all animate calls
        step: function (state, circle) {
            circle.path.setAttribute('stroke', state.color);
        }
    });

    circle.animate(animatePercentage, {
        from: {
            color: startColor
        },
        to: {
            color: endColor
        }
    });
}