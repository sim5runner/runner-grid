/**
 * Created by AbhishekK on 9/26/2016.
 */


exports.mapRunParams = function(req) {

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
     *
     *
     "params": [
     "-DtestName=word.Test_GO16_WD_04_4A_01_A1",
     "-DbrName=firefox",
     "-DbrVersion=ANY", // for hub
     "-Dnode=abhi",
     "-DhubIp=192.168.1.200",
     "-DhubPort=4444",
     "-Dhost=hub/local/saucelabs"
     ],
     */

        var runParams = [];

        runParams.push('mvn test');
        runParams.push(('DtestName='+req.task.appName.toLowerCase()+'.Test_'+req.task.filename));
        runParams.push(('DbrName='+req.run.browser.name.toLowerCase()));

        if(req.run.env.toLowerCase() === 'saucelabs'){  // todo push other saucelabs config
            runParams.push('-Dhost=saucelabs');
            runParams.push(('-DbrVersion='+req.run.browser.version));
        } else {
            runParams.push('-Dhost=hub');
            runParams.push(('Dnode='+req.run.browser.node));
            runParams.push('-DbrVersion=ANY');
            runParams.push('-DhubIp=192.168.1.22'); // todo: set dynamically for deployed machine, currently for loadrunner1
            runParams.push('-DhubPort=4444');
        }

        var command = runParams.join(" ");

        var outRequest = {
            command :command,
            task : req.task,
            clientIp: req.user.ip
        };

    return outRequest;
};