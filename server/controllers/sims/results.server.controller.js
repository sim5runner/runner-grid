/**
 * Created by AbhishekK
 */
'use strict';

var util = require('../../utils');
var fs = require('fs');
var mkdirp = require('mkdirp');
var request = require('request');
var config = require('../../config/index');

exports.pushResults = function (req, res) {

    var taskId = req.body[0].taskid; // fixed in case of sims builder, only one task supported at a time.
    var builderRequestBody = getResultForBuilder(req.body[0]);

    // get request from req body
    // translate results
    // post results to builder

    String.prototype._format = util.fillKeys;

    let url = ( config.builder.results.server +
        config.builder.results.api._format([{key:'task-id', value: taskId}]) ),
        options = {
            method: 'post',
            body: builderRequestBody,
            json: true,
            url: url,
            headers: {
                'Authorization': config.builder.results.token
            }
        };

    request(options, (err, builder_response, body) => {
        console.log('Requested builder with payload: ');
        console.log(JSON.stringify(body));

        console.log('Builder response: ');
        console.log(JSON.stringify(builder_response));

        if (err) {
            res.json(
                {
                    status:"failure",
                    message:"error in posting results to server"
                }
            );
        } else {

            res.json(
                {
                    status:"success",
                    message:"successfully posted results to server"
                }
            );
        }
    });

    function getResultForBuilder(response){

        try {
            // update task overall status
            for (var i in response.pathways) {
                if (response.pathways[i].status != 'pass') {
                    response.status = 'fail';
                    return response;
                }
            }
            response.status = 'pass';
        } catch (er) { console.log(er); }

        return response;
    };
};
