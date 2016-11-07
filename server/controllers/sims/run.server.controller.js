/**
 * Created by AbhishekK
 */
'use strict';

var util = require('../../utils');
var fs = require('fs');
var mkdirp = require('mkdirp');
var paramsHandler = require('./params.server.controller');
var Client = require('svn-spawn');

var properties = require ("properties");

var tempClientIp = '';

exports.runTask = function (req, res) {
    var commitQ = [];
    var processing = false;

    var currentTestId = util.getUUID();
    _io.emit(req.body.user.ip, 'Client: '+req.body.user.ip + ' requested.');

    paramsHandler.mapRunParams(req.body,currentTestId, function(params){ // mapping params

        /**
         * Handling user request for run / commit
         *
         */

        writeTestFile(params.task.filename,params.task.appName,params.task.java,params.task.xml, params.task.json ,params.clientIp,
            function(){
                if (params.task.commit.toString() === 'true') {

                    var newCommit = {
                        clientIp: params.clientIp,
                        filename: params.task.filename,
                        appName: params.task.appName,
                        json: params.task.json,
                        svn: {
                            username: params.svn.username,
                            password: params.svn.password,
                            message: params.svn.message
                        },
                        xpaths: params.task.xpaths,
                        res: res
                    };

                    commitQ.push(newCommit);

                    if (!processing) {
                        processing = true;

                        while (commitQ.length) {
                            var processEl = commitQ.shift() ;
                            tempClientIp = processEl.clientIp ;
                            console.log(tempClientIp);
                            /**
                             * if commit
                             */
                            _io.emit(processEl.clientIp + '-svn', "Committing files to SVN..");
                            _io.emit(processEl.clientIp + '-svn', processEl.filename);
                            commitFileToSvn( processEl.filename, processEl.svn.username, processEl.svn.password, processEl.svn.message, processEl.appName, processEl.res,
                                function (success){ // success

                                    console.log("Java & Xml files committed successfully");
                                    _io.emit(processEl.clientIp + '-svn', '<span style="color: green">Java & Xml files committed successfully.</span>');
                                    _io.emit(processEl.clientIp + '-svn', 'Updating xpath config.. Please Wait !');

                                    /**
                                     * commit xpath config
                                     */
                                    commitApplicationXpath(processEl.appName, processEl.xpaths, processEl.svn.username, processEl.svn.password, processEl.svn.message, function (done){
                                        console.log("Xpath committed successfully");
                                        _io.emit(processEl.clientIp + '-svn', '<span style="color: green">Xpath committed successfully</span>');

                                            /**
                                             * commit json file
                                             */
                                            var _taskXmlPath = util.getDirFromXMlName(processEl.filename);
                                            var _jsonFileLocation =  _serverDirectory + '/server/lib/jf/src/test/resources/taskJSON' + _taskXmlPath + '/' + processEl.filename + '.json';

                                            commitJsonFile(_jsonFileLocation , processEl.json, processEl.svn.username, processEl.svn.password, processEl.svn.message, function (done){
                                                    if(!commitQ.length) {processing = false;}
                                                    console.log("Xpath committed successfully");
                                                    _io.emit(processEl.clientIp + '-svn', '<span style="color: green">Json committed successfully</span>');
                                                    _io.emit(processEl.clientIp + '-svn', '<span style="color: green; font-size: 16px"><b><br><br>SVN COMMIT SUCCESSFUL.<br><br>=========================</b></span>');
                                                    res.json(
                                                        {
                                                            error:"false",
                                                            msg:" All Files committed successfully"
                                                        }
                                                    );
                                                }, function(done){
                                                    if(!commitQ.length) {processing = false;}
                                                    _io.emit(processEl.clientIp + '-svn', '<span style="color: red">Files Committed successfully. Error in Committing json file..</span>');

                                                    res.json(
                                                        {
                                                            error:"true",
                                                            msg:"Files Committed successfully. Error in Committing xpath config. Please Retry !"
                                                        }
                                                    );
                                                }

                                            )

                                    }, function(done){
                                            if(!commitQ.length) {processing = false;}
                                            _io.emit(processEl.clientIp + '-svn', '<span style="color: red">Files Committed successfully. Error in Committing xpath config..</span>');

                                            res.json(
                                                {
                                                    error:"true",
                                                    msg:"Files Committed successfully. Error in Committing xpath config. Please Retry !"
                                                }
                                            );
                                        }

                                    )

                                },function (failure){ // failure
                                    if(!commitQ.length) {processing = false;}
                                    _io.emit(processEl.clientIp + '-svn', '<span style="color: red">Error in pushing Java / Xml files to svn.<br>Please Retry !</span>');
                                    res.json(
                                        {
                                            error:"true",
                                            msg:"Error in pushing Java / Xml files to svn. Please Retry !"
                                        }
                                    );
                                });
                        }

                    }
                }
                else {
                    /**
                     * else run and return
                     */

                    var cmd = params.command;

                    _io.emit(params.clientIp, JSON.stringify(req.body));
                    console.log('Client: '+params.clientIp);
                    console.log('running command ' + cmd);

                    _io.emit(params.clientIp, 'Running command ' + cmd);

                    var process = require('child_process');
                    var ls;

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

                    res.json(
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
                res.json(
                    {
                        error:"true",
                        msg:"Error in script execution on runner server"
                    }
                );
            }
        )

    });

};

function writeTestFile(filename,appName,java,xml,json,clientIp,done, err){

    _io.emit(clientIp, 'Creating files..');

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

function commitFileToSvn(_filename,user, pass, message, app, res, success, failure){
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
    client.commit([('SIMS-0000: ' + message), (_serverDirectory + '/server/lib/jf' + javaFilePath)], function(err, data) {
        if (data) {
            _io.emit(tempClientIp + '-svn', '<span style="color: black">' + (data).replace(/\r\n/g,'<br>') + '</span>');
        }
        if (err) {
            client.add(_serverDirectory + '/server/lib/jf' + javaFilePath, function(err, data) {
                if (err) {
                    if(otherCompleted) {
                        failure();
                    } else {otherCompleted = true;commiterr=true;}
                } else {
                    client.commit([('SIMS-0000: ' + message), (_serverDirectory + '/server/lib/jf' + javaFilePath)], function(err, data) {
                        if (data) {
                            _io.emit(tempClientIp + '-svn', '<span style="color: black">' + (data).replace(/\r\n/g, '<br>') + '</span>');
                        }
                        if (err) {
                            if(otherCompleted) {
                                failure();
                            } else {otherCompleted = true;commiterr=true;}
                        }
                        if(otherCompleted && (!commiterr)) {
                            success();
                        } else if(otherCompleted && commiterr) {
                            failure();
                        } else {otherCompleted = true;}
                    });
                }

            });
        } else {

            if(otherCompleted && (!commiterr)) {
                success();
            } else if(otherCompleted && commiterr) {
                failure();
            } else {otherCompleted = true;}
        }

    });

    /**
     * Commiting XML
     */
    client.commit([('SIMS-0000: ' + message), (_serverDirectory + '/server/lib/jf' + xmlFilePath)], function(err, data) {
        if (data) {
            _io.emit(tempClientIp + '-svn', '<span style="color: black">' + (data).replace(/\r\n/g, '<br>') + '</span>');
        }
        if (err) {
            client.add(_serverDirectory + '/server/lib/jf' + xmlFilePath, function(err, data) {
                if (err) {
                    if(otherCompleted) {
                        failure();
                    } else {otherCompleted = true;commiterr=true;}
                } else {
                    client.commit([('SIMS-0000: ' + message), (_serverDirectory + '/server/lib/jf' + xmlFilePath)], function(err, data) {
                        if (data) {
                            _io.emit(tempClientIp + '-svn', '<span style="color: black">' + (data).replace(/\r\n/g, '<br>') + '</span>');
                        }
                        if (err) {
                            if(otherCompleted) {
                                failure();
                            } else {otherCompleted = true;commiterr=true;}
                        }
                        if(otherCompleted && (!commiterr)) {
                            success();
                        } else if(otherCompleted && commiterr) {
                            failure();
                        } else {otherCompleted = true;}
                    });
                }
            });
        } else {
            if(otherCompleted && (!commiterr)) {
                success();
            } else if(otherCompleted && commiterr) {
                failure();
            } else {otherCompleted = true;}
        }

    });

};

function commitApplicationXpath(appName,xpaths, user, pass, message, success,failure){

    var _configFileLocation = _serverDirectory+"/server/lib/jf/src/test/resources/config/"+ appName.toLowerCase().trim() + "_config.properties"

        // load config file to memory
    properties.parse (_configFileLocation, { path: true }, function (error, configObj){

        if (error) failure();

        for(var i in xpaths) {

            var _searchKey = xpaths[i].split(/=(.+)?/)[0];
            var _keyValue = xpaths[i].split(/=(.+)?/)[1];

            var _xpathKeys = Object.keys(configObj);

            var iFound = false;
            for (var key in _xpathKeys) {
                //console.log(_searchKey.toString().trim());
                //console.log(_xpathKeys[key].toString().trim());

                if (_xpathKeys[key].toString().trim() === _searchKey.toString().trim()){

                    iFound = true;
                    console.log('updating xpath: ' + _searchKey + ' = ' + _xpathKeys[key]);
                    configObj[_xpathKeys[key]] = _keyValue;
                }
            }

            if(!iFound) {
                console.log('adding xpath: ' + _searchKey + ' = ' + _keyValue);
                var __temp = new String(_searchKey);
                configObj[__temp] = _keyValue;
            }

        }
        // updating content
        var _configArray = [];
        for (var key in configObj) {
            var _temp = ((key == null ? "": key.trim()).replace(/ /g, "\\ ")) + ' = ' + ((configObj[key] == null ? "": configObj[key].trim()).replace(/ /g, "\\ "));
            _configArray.push(_temp);
        };

        var configFileContent = _configArray.join("\n");

        // write config file
        fs.writeFile( _configFileLocation, configFileContent, function(error) {
            if (error) {

                err("write error:  " + error.message);
                console.error("write error:  " + error.message);

                failure();

            } else {

                // commit config
                var client = new Client({
                    cwd: (_serverDirectory + '/server/lib/jf'),
                    username: user, // optional if authentication not required or is already saved
                    password: pass, // optional if authentication not required or is already saved
                    noAuthCache: true // optional, if true, username does not become the logged in user on the machine
                });

                client.commit([('SIMS-0000: ' + message), _configFileLocation], function(err, data) {
                    if (data) {
                        _io.emit(tempClientIp + '-svn', '<span style="color: black">' + (data).replace(/\r\n/g, '<br>') + '</span>');
                    }
                    if (err) {
                        client.add(_configFileLocation, function(err, data) {
                            if (err) {
                                failure();
                            } else {
                                client.commit([('SIMS-0000: ' + message), _configFileLocation], function(err, data) {
                                    if (data) {
                                        _io.emit(tempClientIp + '-svn', '<span style="color: black">' + (data).replace(/\r\n/g, '<br>') + '</span>');
                                    }
                                    if (err) {
                                        failure();
                                    } else {
                                        success();
                                    }
                                });
                            }

                        });
                    } else {
                        success();
                    }
                });
            }
        });

    });

};


function commitJsonFile(_jsonFileLocation, jsonFileContent, user, pass, message, success, failure) {

    var _direc = _jsonFileLocation.substring(0, _jsonFileLocation.lastIndexOf("/"));

    if (!(fs.existsSync(_direc))){
        console.log('creating dir.. '+ _direc);
        mkdirp(_direc, function (err) {
            if (err) console.error(err)
            else {
                processJsonCommit();
            }
        });
    } else {
        processJsonCommit();
    }

    function processJsonCommit(){
        // write config file
        fs.writeFile( _jsonFileLocation, jsonFileContent, function(error) {
            if (error) {
                console.error("write error:  " + error.message);

                failure();

            } else {

                // commit config
                var client = new Client({
                    cwd: (_serverDirectory + '/server/lib/jf'),
                    username: user, // optional if authentication not required or is already saved
                    password: pass, // optional if authentication not required or is already saved
                    noAuthCache: true // optional, if true, username does not become the logged in user on the machine
                });

                client.commit([('SIMS-0000: ' + message), _jsonFileLocation], function(err, data) {
                    if (data) {
                        _io.emit(tempClientIp + '-svn', '<span style="color: black">' + (data).replace(/\r\n/g, '<br>') + '</span>');
                    }
                    if (err) {
                        client.add(_jsonFileLocation, function(err, data) {
                            if (err) {
                                failure();
                            } else {
                                client.commit([('SIMS-0000: ' + message), _jsonFileLocation], function(err, data) {
                                    if (data) {
                                        _io.emit(tempClientIp + '-svn', '<span style="color: black">' + (data).replace(/\r\n/g, '<br>') + '</span>');
                                    }
                                    if (err) {
                                        failure();
                                    } else {
                                        success();
                                    }
                                });
                            }

                        });
                    } else {
                        success();
                    }
                });
            }
        });
    };
};

