/**
 * Created by AbhishekK
 */
'use strict';

var util = require('../../utils');
var fs = require('fs');

exports.runTask = function (req, res) {

    //console.log(req.body)

    /**
     * todo:
     * 1. establish 2way connection with client
     * 2. validate & save request data
     * 3. write files to lib/jf
     * 4. run mvn command
     * 5. show logs
     * 6. option to stop test

    post data json format

    {"command": "mvn test",
    "params": [
		"-DtestName=word.Test_GO16_WD_04_4A_01_A1",
		"-DbrName=firefox",
		"-Dnode=abhi",
		"-DhubIp=192.168.1.200",
		"-DhubPort=4444"
    ],
    "task": {
			"filename": "Test_GO16_WD_04_4A_01_A1",
			"xml": "xml file content",
			"java": "java file content"
			},
    "clientIp" : "192.168.1.97"
    }

     POST: http://RunnerGrid:8080/sims/runtask
     */

    var cmd = req.body.command + ' ' + req.body.params.join(" ");

    writeTestFile(req.body.task.filename,req.body.task.appName,req.body.task.java,req.body.task.xml,req.body.clientIp,
        function(){

            console.log('Client: '+req.body.clientIp);
            console.log('running command ' + cmd);

            _io.emit(req.body.clientIp, 'Client: '+req.body.clientIp);
            _io.emit(req.body.clientIp, 'Running command ' + cmd);

            var process = require('child_process');
            var ls;

            //todo: change dir path
            var options = { cwd: (_serverDirectory+"/server/lib/jf"),
                env: process.env
            }

            ls = process.spawn('cmd.exe', ['/c', cmd], options);

            ls.stdout.on('data', function(data){
                // todo: preserve logs
                _io.emit(req.body.clientIp, '<span style="color: darkslategrey">' + util.ab2str(data) + '</span>');
                console.log(util.ab2str(data));
            })

            ls.stderr.on('data', function (data) {
                _io.emit(req.body.clientIp, '<span style="color: red">' + util.ab2str(data) + '</span>');
                console.log(util.ab2str(data));
            });

            ls.on('exit', function (code) {
                console.log('run command exited with code ' + code);
            });

            res.end("CMD_STARTED");
        },
        function(er) {
            _io.emit(req.body.clientIp, 'client: '+req.body.clientIp);
            _io.emit(req.body.clientIp, '<span style="color: red">' + er + '</span>');
            res.end("ERROR");
        }
    )
};

function writeTestFile(filename,appName,java,xml,clientIp,done, err){

    _io.emit(clientIp, 'Creating test files..');

    var _taskXmlPath = util.getDirFromXMlName(filename);

    var xmlDirectory = (_serverDirectory+"/server/lib/jf/src/test/resources/taskXML/" + _taskXmlPath);
    var xmlfilepath = xmlDirectory + "/" + filename + '.xml';

    var javafilepath = (_serverDirectory+"/server/lib/jf/src/test/java/testcase/"+appName + "/" + filename + '.java');

    if (!(fs.existsSync(xmlDirectory))){
        util.mkdirParent(xmlDirectory, function(){
            console.log('creating dir.. '+ xmlDirectory);
            writeFilesToDisk (xmlfilepath,xml,javafilepath,java);
        });
    } else {
        writeFilesToDisk (xmlfilepath, xml, javafilepath, java);
    }

    function writeFilesToDisk( xmlfilepath, xml, javafilepath, java ){

        var otherCompleted = false;
        fs.writeFile( xmlfilepath, xml, function(error) {
            if (error) {
                err("write error:  " + error.message);
                console.error("write error:  " + error.message);
            } else {
                if(otherCompleted) {
                    done();
                } else {
                    otherCompleted = true;
                }
                console.log("Successful Write to " + xmlfilepath);
            }
        });

        fs.writeFile( javafilepath, java, function(error) {
            if (error) {
                err("write error:  " + error.message);
                console.error("write error:  " + error.message);
            } else {
                if(otherCompleted) {
                    done();
                } else {
                    otherCompleted = true;
                }
                console.log("Successful Write to " + javafilepath);
            }
        });
    };

    console.log('writing.. '+ filename);
};