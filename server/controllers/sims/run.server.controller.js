/**
 * Created by AbhishekK
 */
'use strict';

var util = require('../../utils');
var fs = require('fs');
var mkdirp = require('mkdirp');
var paramsHandler = require('./params.server.controller');

exports.runTask = function (req, res) {

    var currentTestId = util.getUUID();
    _io.emit(req.body.user.ip, 'Client: '+req.body.user.ip);
    _io.emit(req.body.user.ip, 'Requested: ');
    _io.emit(req.body.user.ip, JSON.stringify(req.body));

    var params = paramsHandler.mapRunParams(req.body,currentTestId);

    var cmd = params.command;

    /**
     * Handling Tests and Commit
     */
    writeTestFile(params.task.filename,params.task.appName,params.task.java,params.task.xml,params.clientIp,
        function(){

            if (params.task.commit === true) {
                /**
                 * if commit
                 */

                commitFileToSvn(params.task.filename, params.svn.username, params.svn.password, params.svn.url, params.task.appName, res,
                    function(success){ // success
                        console.log("Files committed successfully");
                        res.json(
                            {
                                error:"false",
                                msg:"Files committed successfully"
                            }
                        );
                    },function(err){ // failure
                        res.json(
                            {
                                error:"true",
                                msg:"Error in pushing files to svn"
                            }
                        );
                    });

            }
            else {
                /**
                 * else run and return
                 */

                console.log('Client: '+params.clientIp);
                console.log('running command ' + cmd);

                _io.emit(params.clientIp, 'Running command ' + cmd);

                var process = require('child_process');
                var ls;

                //todo: change dir path
                var options = { cwd: (_serverDirectory+"/server/lib/jf"),
                    env: process.env
                };

                ls = process.spawn('cmd.exe', ['/c', cmd], options);

                ls.stdout.on('data', function(data){
                    // todo: preserve logs
                    _io.emit(params.clientIp, '<span style="color: black">' + util.ab2str(data) + '</span>');
                    //console.log(util.ab2str(data));
                });

                ls.stderr.on('data', function (data) {
                    _io.emit(params.clientIp, '<span style="color: red">' + util.ab2str(data) + '</span>');
                    //console.log(util.ab2str(data));
                });

                ls.on('exit', function (code) {
                    console.log('run command exited with code ' + code);
                });

                ls.on('close', function(code) {
                    console.log('closing code: ' + code);
                    //  todo: remove running test for req.body.clientIp from _runningTests

                    function removeTestFromRunningList(arr) {
                        var what, a = arguments, L = a.length, ax, i=0;
                        while (L > 1 && arr.length) {
                            what = a[--L];
                            for (var i=arr.length-1; i>=0; i--) {
                                if(arr[i].id === what.id){
                                    arr.splice(i, 1);
                                }
                            }
                        }
                        return arr;
                    };

                    removeTestFromRunningList(_runningTests, {id:currentTestId});

                });

                res.end(
                    {
                        error:"false",
                        msg:"Script execution triggered on runner server"
                    }
                );

            }

        },
        function(er) {
            _io.emit(params.clientIp, 'client: '+params.clientIp);
            _io.emit(params.clientIp, '<span style="color: red">' + er + '</span>');
            res.end(
                {
                    error:"true",
                    msg:"Error in script execution on runner server"
                }
            );
        }
    )
};

function writeTestFile(filename,appName,java,xml,clientIp,done, err){

    _io.emit(clientIp, 'Creating test files..');

    var _taskXmlPath = util.getDirFromXMlName(filename);

    var xmlDirectory = (_serverDirectory + "/server/lib/jf/src/test/resources/taskXML" + _taskXmlPath);
    var xmlfilepath = xmlDirectory + "/" + filename + '.xml';

    var javafilepath = (_serverDirectory+"/server/lib/jf/src/test/java/testcase/"+appName + "/Test_" + filename + '.java');

    if (!(fs.existsSync(xmlDirectory))){
        console.log('creating dir.. '+ xmlDirectory);
        mkdirp(xmlDirectory, function (err) {
            if (err) console.error(err)
            else {
                writeFilesToDisk (xmlfilepath,xml,javafilepath,java);
            }
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

function commitFileToSvn(_filename,user, pass, svnUrl, app, res, success, err){
    // todo: get file paths
    var _taskXmlPath = util.getDirFromXMlName(_filename);

    var javaFilePath = '/src/test/java/testcase/' + app + '/'+ 'Test_' + _filename + '.java';
    var jsonFilePath = '/src/test/resources/taskJSON' + _taskXmlPath + '/' + _filename + '.json';
    var xmlFilePath = '/src/test/resources/taskXML' + _taskXmlPath + '/' + _filename + '.xml';

            /**
             * commiting files to svn
             * todo: add to queue here & pop one by one
             * todo: change this to commit files from stream to svn url
             */

            var client = new Client({
                cwd: (_serverDirectory + '/server/lib/jf'),
                username: user, // optional if authentication not required or is already saved
                password: pass, // optional if authentication not required or is already saved
                noAuthCache: true // optional, if true, username does not become the logged in user on the machine
            });



    var otherCompleted = false;
    var commiterr = false;
    /**
     * Commiting java
     */
    client.commit(['SIMS-0000', (_serverDirectory + '/server/lib/jf' + javaFilePath)], function(err, data) {
        if (err) {
            client.add(_serverDirectory + '/server/lib/jf' + javaFilePath, function(err, data) {
                if (err) {
                    if(otherCompleted) {
                        res.json(
                            {
                                error:"true",
                                msg:"Error in commiting files"
                            }
                        )
                    } else {otherCompleted = true;commiterr=true;}
                } else {
                    client.commit(['SIMS-0000', (_serverDirectory + '/server/lib/jf' + javaFilePath)], function(err, data) {
                        if (err) {
                            if(otherCompleted) {
                                res.json(
                                    {
                                        error:"true",
                                        msg:"Error in commiting files"
                                    }
                                )
                            } else {otherCompleted = true;commiterr=true;}
                        }
                        if(otherCompleted && (!commiterr)) {
                            res.json(
                                {
                                    error:"false",
                                    msg:"Files commited successfully"
                                }
                            )
                        } else if(otherCompleted && commiterr) {
                            res.json(
                                {
                                    error:"true",
                                    msg:"Error in commiting files"
                                }
                            )
                        } else {otherCompleted = true;}
                    });
                }

            });
        } else {
            if(otherCompleted && (!commiterr)) {
                res.json(
                    {
                        error:"false",
                        msg:"Files commited successfully"
                    }
                )
            } else if(otherCompleted && commiterr) {
                res.json(
                    {
                        error:"true",
                        msg:"Error in commiting files"
                    }
                )
            } else {otherCompleted = true;}
        }

    });

    /**
     * Commiting XML
     */
    client.commit(['SIMS-0000', (_serverDirectory + '/server/lib/jf' + xmlFilePath)], function(err, data) {
        if (err) {
            client.add(_serverDirectory + '/server/lib/jf' + xmlFilePath, function(err, data) {
                if (err) {
                    if(otherCompleted) {
                        res.json(
                            {
                                error:"true",
                                msg:"Error in commiting files"
                            }
                        )
                    } else {otherCompleted = true;commiterr=true;}
                } else {
                    client.commit(['SIMS-0000', (_serverDirectory + '/server/lib/jf' + xmlFilePath)], function(err, data) {
                        if (err) {
                            if(otherCompleted) {
                                res.json(
                                    {
                                        error:"true",
                                        msg:"Error in commiting files"
                                    }
                                )
                            } else {otherCompleted = true;commiterr=true;}
                        }
                        if(otherCompleted && (!commiterr)) {
                            res.json(
                                {
                                    error:"false",
                                    msg:"Files commited successfully"
                                }
                            )
                        } else if(otherCompleted && commiterr) {
                            res.json(
                                {
                                    error:"true",
                                    msg:"Error in commiting files"
                                }
                            )
                        } else {otherCompleted = true;}
                    });
                }
            });
        } else {
            if(otherCompleted && (!commiterr)) {
                res.json(
                    {
                        error:"false",
                        msg:"Files commited successfully"
                    }
                )
            } else if(otherCompleted && commiterr) {
                res.json(
                    {
                        error:"true",
                        msg:"Error in commiting files"
                    }
                )
            } else {otherCompleted = true;}
        }

    });

};

