/**
 * Created by AbhishekK on 9/26/2016.
 */

var util = require('../../utils')

exports.mapRunParams = function(req,currentTestId) {

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
			"filename": "GO16_WD_04_4A_01_A1",
			"appName" : "word",
			"xml": "xml file content",
			"java": "java file content"
			}
        }
     *
     */
        var serverIP = util.getServerIP();
        var runParams = [];

        runParams.push('mvn test');
        runParams.push(('-DtestName='+req.task.appName.toLowerCase()+'.Test_'+req.task.filename));
        runParams.push(('-DbrName='+req.run.browser.name.toLowerCase()));

        if(req.run.env.toLowerCase() === 'saucelabs'){  // todo push other saucelabs config
            runParams.push('-Dhost=saucelabs');
            runParams.push(('-DbrVersion='+req.run.browser.version));
            runParams.push(('-Dos='+req.run.os));

        } else {
            runParams.push('-Dhost=hub');
            runParams.push(('-Dnode='+req.run.browser.node));
            runParams.push('-DbrVersion=ANY');
            console.log('-DhubIp='+serverIP[0]+'');
            runParams.push('-DhubIp='+serverIP[0]+''); // todo: set dynamically for deployed machine, currently for loadrunner1
            runParams.push('-DhubPort=4444');
        }

        var command = runParams.join(" ");
        console.log('command: ' + command);

        var outRequest = {
            command :command,
            task : req.task,
            clientIp: req.user.ip
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

     _runningTests.push(CurrentTestDetails);

     console.log(_runningTests);

    return outRequest;
};