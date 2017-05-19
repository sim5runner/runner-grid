/**
 * Created by AbhishekK on 9/26/2016.
 */

var util = require('../../utils');
var Client = require('svn-spawn');

var Config = require('../../config/index')

var request = require('request');
var Beautify = require('../../utils/beautify');

exports.mapRunParams = function(req,currentTestId,done) {

    // todo: add validation on input json
    /**
     *
       {
		"user" : {
			"name" : "",
			"ip" : "",
			"userdata" : {}
		},
		"run" : {
			"env" : "hub/saucelabs",
			"os" : "windows/mac",
			"resolution": "1280x1280",
			"app" : {
				"url" : "http://grader13/qatrunk/SIM5Frame.aspx",
				"public" : "false",
				"build" : ""
				},
			"browser" : {
				"node" : "abhi",
				"name" : "chrome",
				"version" : "46"
				}
		},
		"task": {
			"filename": "GO16_WD_04_4A_01_A1",  // todo: corresponding update in post json in scriptor
			"appName" : "word",
			"xml": "xml file content",
			"java": "java file content",
			"json": " "
			"commit":"true"
			}
        "svn": {
            "url": "",
            "username":"",
            "password":"",
            "message" :""
            }
        }
     *
     */
        var serverIP = util.getServerIP();
        var runParams = [];

        runParams.push('mvn test');
        runParams.push(('-DtestName='+req.task.appName.toLowerCase()+'.Test_'+req.task.filename)); // todo: check if filename require / contains any extension
        runParams.push(('-DbrName='+req.run.browser.name.toLowerCase()));
        runParams.push(('-Dos='+req.run.os));

        if (req.run.env.toLowerCase() === 'saucelabs') {  // todo push other saucelabs config
            runParams.push('-Dhost=saucelabs');
            runParams.push(('-DbrVersion='+req.run.browser.version));

        } else {
            runParams.push('-Dhost=hub');
            runParams.push(('-Dnode='+req.run.browser.node));
            runParams.push('-DbrVersion=ANY');
            console.log('-DhubIp='+serverIP[0]+'');
            runParams.push('-DhubIp='+serverIP[0]+'');
            runParams.push('-DhubPort=4444');
        }

        var command = runParams.join(" ");
        console.log('command: ' + command);

    // get task script java and xml from external service

        var outRequest = {
            command :command,
            task : req.task,
            clientIp: req.user.ip,
            svn: req.svn,
            error: false
        };

    /**
     * Adding test to active test list
     */

     var CurrentTestDetails = {
         id:currentTestId,
         ip:req.user.ip,
         user:req.user,
         run:req.run
     };

	 if (req.task.commit.toString() === 'true') {
         _io.emit(req.user.ip + '<br>');
         _io.emit(req.user.ip + '<span style="color: green">Svn Commit Acknowledged..</span>');
         console.log('File Commit Request');

         var client;
         try {
             client = new Client({
                 cwd: (_serverDirectory + '/server/lib/jf'),
                 username: req.svn.username, // optional if authentication not required or is already saved
                 password: req.svn.password, // optional if authentication not required or is already saved
                 noAuthCache: true // optional, if true, username does not become the logged in user on the machine
             });
         } catch (er1){
             _io.emit(req.user.ip + '-svn', 'Svn Error');
             _io.emit(req.user.ip + '-svn', '<span style="color: #ea5965;">'+er1+'</span>');
         }

         _io.emit(req.user.ip + '-svn', 'Processing Commit Request..');
            client.cmd(['cleanup'], function(err, data) {
                if (err) {
                    outRequest.error = true;
                    _io.emit(req.user.ip + '-svn', 'Svn Cleanup Error');
                    _io.emit(req.user.ip + '-svn', '<span style="color: #ea5965;">'+err+'</span>');

                    res.json(
                        {
                            error:"true",
                            msg:"Error in SVN cleanup"
                        }
                    )
                } else {

                    _io.emit(req.user.ip + '-svn', data);
/*
                    _io.emit(req.user.ip + '-svn', 'Svn Cleanup..');
                    console.log(outRequest);
                    //return outRequest;
                    done(outRequest);
*/

                    // todo: remove svn update from here and add alternate - > might conflict / overwrite running tests
                    client.update(function(err, data) {
                        if(err){
                            outRequest.error = true;
                            _io.emit(req.user.ip + '-svn', 'Svn Cleanup Error');
                            _io.emit(req.user.ip + '-svn', '<span style="color: #ea5965;">'+err+'</span>');
                            console.log('svn update error');
                        }
                        _io.emit(req.user.ip + '-svn', 'Svn Cleanup & Update..');
                        _io.emit(req.user.ip + '-svn', data);
                        console.log(outRequest);
                        done(outRequest);
                    });
                }
            });
			
	} else {
     _runningTests.push(CurrentTestDetails);
     console.log(_runningTests);
         console.log(outRequest.task.xml);
         if (outRequest.task.xml === 'null' || outRequest.task.java === 'null') {
                console.log('getting script from ext server');
             var _sle = outRequest.task.filename.replace(/_/gi,".");
             // make a call to external service
             request((Config.api.script.java + _sle +'?format=java'), function (error, response, body) {
                 if (!error && response.statusCode == 200) {
                     //console.log(body);

                     var javaContent = body.slice(1, -1);
                     outRequest.task.java = Beautify.js_beautify(javaContent);

                     console.log(outRequest.task.java);

                     request((Config.api.script.xml + _sle + '?format=xml'), function (error, response, body) {
                         if (!error && response.statusCode == 200) {
                             console.log(body)
                             outRequest.task.xml = body;
                             console.log('done');
                             done(outRequest);

                         }
                     })

                 }
             })

         } else { done(outRequest);}
	}

};