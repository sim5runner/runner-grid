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

    String.prototype._format = util.fillKeys;

    var taskId = req.body.testclasses[0].taskid; // fixed in case of sims builder, only one task supported at a time.

    var url = ( config.builder.results.server +
    config.builder.results.api._format([{key:'task-id', value: taskId}]) );

    var builderRequestBody = getResultForBuilder(req.body.testclasses[0]);

    // get request from req body
    // translate results
    // post results to builder

    var options = {
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
/*
            {
                "taskid": "GO16.PPT.05.5A.02.T1",
                "status": "incomplete",
                "pathways": {
                    "unique_pathway_key1": {
                        "status": "pass",
                        "message": "timeout: timed out after 5000 msec waiting for : Issue in Login, will not navigate to dashboard"
                    },
                    "unique_pathway_key2": {
                        "status": "fail",
                        "message": "timeout: timed out after 5000 msec waiting for : Issue in Login, will not navigate to dashboard"
                    },
                    "unique_pathway_key3": {
                        "status": "incomplete"
                    }
                }
            }
        */

        try {

            // todo: merge multiple for loops
            // update method name
            var _taskid = (response.taskid.replace(/\./g,"_") + "_");

            // update pathways keys
            for (var i in response.pathways) {
                var _k = i.replace(_taskid, "");
                response.pathways[_k] = response.pathways[i];
                delete response.pathways[i];
            }

            // get step method detailed object
            for (var i in response.pathways) {

                var stepMethodObject = expandStepMethod(i);

                // extend object
                Object.assign(response.pathways[i], stepMethodObject);

            }

            // temp: if single step - append query param step no
            var _key = Object.keys(response.pathways)[0];
            if((_key.match(/S/g) || []).length === 1) {

                var _step_no = _key.substring(_key.lastIndexOf("S")+1,_key.lastIndexOf("M"));
                url += "?step="+_step_no;
            }

            // update task overall status
            for (var i in response.pathways) {
                if (response.pathways[i].status != 'pass') {
                    response.status = 'fail';
                    return response;
                }
            }
            response.status = 'pass';

        } catch (er) { console.log(er); }

        // todo: update response syntax furthur
        return response;
    };

    function expandStepMethod( stepMethod ){
        //S1M1_S2M4
        var _expandedStepMethod = {};

        try {
            var steps = stepMethod.split('_');

            for (var i =0; i<steps.length; i++)
            {

                /*                "1": {
                 "method": 1
                 },
                 "2": {
                 "method": 1
                 },
                 "3": {
                 "method": 1
                 }*/

                var _step =steps[i].substring(steps[i].lastIndexOf("S")+1,steps[i].lastIndexOf("M"));
                var _method =steps[i].substring(steps[i].lastIndexOf("M")+1,steps[i].length);

                _expandedStepMethod[_step] = {
                    method: _method
                }
            }
        } catch (er) {
            console.log(er);
        }

        return _expandedStepMethod;
    }
};
