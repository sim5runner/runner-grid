

/**
 * helpers
 */

var baseUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port : '');
(function(){if(window.location.hash.length) {window.location = baseUrl}})();

/**
 * Dummy post function
 * @param path
 * @param params
 * @param method
 */
function post(path, params, method) {
    method = method || "post"; // Set method to post by default if not specified.

    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for(var key in params) {
        if(params.hasOwnProperty(key)) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
        }
    }

    document.body.appendChild(form);
    form.submit();
};

/**
 * For user pass login
 */

$(document).ready(function() {

    // process the form
    $('#password-login').submit(function(event) {

        var formData = {
            'username'              : $('input[name=username]').val(),
            'password'             : $('input[name=password]').val()
        };

        // process the form
        $.ajax({
            type        : 'POST', // define the type of HTTP verb we want to use (POST for our form)
            url         : baseUrl + '/login', // the url where we want to POST
            data        : formData, // our data object
            dataType    : 'json', // what type of data do we expect back from the server
            encode          : true
        })
            // using the done promise callback
            .done(function(data) {

                if(data.status === 200){
                    console.log('success');
                    post(baseUrl + '/login/', {}, 'post');
                } else {
                    console.log('fail');
                    alert(data.message);
                }
            });

        // stop the form from submitting the normal way and refreshing the page
        event.preventDefault();
    });

});

/**
 * For google signin.
 */

var userLogin = false;
function onSignInCallback(resp) {
    if(gapi.client !== undefined) {
        gapi.client.load('plus', 'v1', handleLogin(resp));
    }
}

function onLoginClick(){
    userLogin = true;
}

function handleLogin(resp) {
    if(resp.id_token !== undefined && userLogin === true){
        $.ajax({
            url: (baseUrl + '/login'),
            type: "post",
            success: function (data) {
                if(data.status != 200){
                    alert(data.message);
                } else {
                    post(baseUrl + '/login/', {}, 'post');
                }
            },
            failure: function (err) {
                alert('Error in login');
            },
            data: {
                'id_token' : resp.id_token
            }
        });
    }
};
