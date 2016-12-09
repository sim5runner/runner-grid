/**
 * Created by AbhishekK on 11/22/2016.
 */


(function() {
    var restante = 3;
    'use strict';

    function wait() {
        return new Promise(function(done, reject) {
            setTimeout(function() {
                if (restante > 0) {
                    done();
                } else {
                    reject();
                }
            }, 2000);
        });
    }
    wait().
        then(function() {
            $("h1").text("Ok,Preparando expresso...");
            return wait();
        }).
        then(function() {
            $("h1").text("Listo, tenga su expresos");
            return wait();
        }).
        then(function() {
            restante -= 1;
            $("h1").text("Gracias :)");
        }).
        catch(function() {
            $("h1").text("Lo lamento, no hay expreso :(");
        });
})();
