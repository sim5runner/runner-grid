/**
 * Created by AbhishekK
 */
'use strict';

var util = require('../../utils');
var paramsHandler = require('./params.server.controller');

var fs = require('fs');
var Q = require('q');

var process = require('child_process');
var Client = require('svn-spawn');
var properties = require ("properties");

var tempClientIp = '';

exports.runOrCommitTask = function (req, res) {
    var commitQ = [];
    var processing = false;
    var clientIp = req.body.user.ip;

    var currentTestId = util.getUUID();
    _io.emit(clientIp, 'Client: ' + clientIp + ' requested.');

    paramsHandler.mapRunParams(req.body, currentTestId, function (params) { // mapping params
        /**
         * Write files - java, xml, json
         */
        _io.emit(clientIp, 'Creating files..');
        var filename = params.task.filename;
        var fileSuffixDirectoryPath = util.getDirFromXMlName(filename);
        var files = [];

        // xml file
        files.push( new function() {
            this.dir = (_serverDirectory + "/server/lib/jf/src/test/resources/taskXML" +
            fileSuffixDirectoryPath);
            this.path = (this.dir + "/" + filename + '.xml');
            this.data = params.task.xml;
        });

        // json file
        files.push(new function () {
            this.dir = (_serverDirectory + "/server/lib/jf/src/test/resources/taskJSON" +
            fileSuffixDirectoryPath);
            this.path = (this.dir + "/" + filename + '.json');
            this.data = params.task.json;
        });

        // java file
        files.push(new function () {
            this.dir = _serverDirectory + "/server/lib/jf/src/test/java/testcase/" +
            params.task.appName + "/";
            this.path = ( this.dir + "Test_" + filename + '.java' );
            this.data = params.task.java;
        });

        util.write_files(files)
            .then(function (data) {

                if (params.task.commit.toString() === 'false') {
                    /**
                     * Run tests
                     */
                    trigger_run();
                }
                else if (params.task.commit.toString() === 'true') {
                    /**
                     *
                     * Commit Files
                     */
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
                        files: files,
                        res: res,
                        error: params.error
                    };

                    commitQ.push(newCommit);
                    if (!processing) {
                        processing = true;
                        while (commitQ.length) {
                            var processEl = commitQ.shift();

                            loadAndUpdateApplicationXpathConfig(processEl.appName, processEl.xpaths,
                                function (appXpathConfig) {
                                    processEl.files.push(appXpathConfig); // adding file to list

                                    util.write_files([appXpathConfig]) // write xpath config
                                        .then(function (data) {
                                            trigger_commit(processEl, // trigger commit files
                                                function(doneCommit){
                                                    _io.emit(processEl.clientIp, '<span>' +
                                                    '*** Commit Process Ended ***</span>');
                                                    if (!commitQ.length) {
                                                        processing = false;
                                                    }
                                                });
                                        })
                                        .catch(function (err) {
                                            console.log('Config write failed.');
                                            console.log(err);
                                        })
                                        .done();
                                },
                                function (failure) {
                                }
                            );
                        };
                    }
                }
            })
            .catch(function (err) {
                console.log('error in writing files');
                console.log(err);
            })
            .done();

        function trigger_run() {

            var cmd = params.command;

            _io.emit(params.clientIp, JSON.stringify(req.body));
            console.log('Client: ' + params.clientIp);
            console.log('running command ' + cmd);

            _io.emit(params.clientIp, 'Running command ' + cmd);

            var ls;

            var options = {
                cwd: (_serverDirectory + "/server/lib/jf"),
                env: process.env
            };

            ls = process.spawn('cmd.exe', ['/c', cmd], options);

            ls.stdout.on('data', function (data) {
                _io.emit(params.clientIp, '<span style="color: black">' +
                util.ab2str(data) + '</span>');
            });

            ls.stderr.on('data', function (data) {
                _io.emit(params.clientIp, '<span style="color: red">' +
                util.ab2str(data) + '</span>');
            });

            ls.on('exit', function (code) {
                console.log('run command exited with code ' + code);
            });

            ls.on('close', function (code) {
                removeTestFromRunningList(_runningTests, {id: currentTestId});

                function removeTestFromRunningList(arr) {
                    var what, a = arguments, L = a.length, ax, i = 0;
                    while (L > 1 && arr.length) {
                        what = a[--L];
                        for (var i = arr.length - 1; i >= 0; i--) {
                            if (arr[i].id === what.id) {
                                arr.splice(i, 1);
                            }
                        }
                    }
                    return arr;
                };
            });

            res.json(
                {
                    error: "false",
                    msg: "Script execution triggered on runner server"
                }
            );
        }; // end - trigger test

        function trigger_commit(processEl, doneCommit) {

            tempClientIp = processEl.clientIp;

            _io.emit(processEl.clientIp + '-svn', processEl.filename);
            _io.emit(processEl.clientIp + '-svn', "Committing files to SVN..");

            var changelistCMD = ['changelist'];
            changelistCMD.push('svnbot-' + processEl.clientIp);


            processEl.files.forEach(function(f, i, files){
                if(f.path) {
                    changelistCMD.push(f.path);
                }
            });

            var client;
            try {
                client = new Client({
                    cwd: (_serverDirectory + '/server/lib/jf'),
                    username: processEl.svn.username,
                    password: processEl.svn.password,
                    noAuthCache: true
                });
            } catch (er1){
                _io.emit(processEl.clientIp + '-svn', 'SVN Error');
                _io.emit(processEl.clientIp + '-svn', '<span style="color: #ea5965;">'+er1+'</span>');
                doneCommit();
            }

            /**
             * Adding Files to Commit
             */
            client.cmd(changelistCMD, function(err2, data) {
                if(!err2){
                    console.log(data)
                    _io.emit(processEl.clientIp + '-svn', '<span>' +
                    data +'</span>');
                    var changelistCommitCMD = ['commit','--changelist',('svnbot-' + processEl.clientIp) ,
                        ('-m\"SIMS-0000 '+ processEl.svn.message + '\"')]

                    /**
                     * Committing files to SVN
                     */
                    client.cmd(changelistCommitCMD, function(err3, data) {
                        if(err3) {
                            console.log(err3);
                            _io.emit(processEl.clientIp + '-svn', 'SVN Commit Error');
                            _io.emit(processEl.clientIp + '-svn', '<span style="color: #ea5965;">' +
                            err3 +'</span>');
                            res.json(
                                {
                                    error: "true",
                                    msg: "error in svn commit process"
                                });
                            doneCommit();

                        } else {
                            console.log(data);
                            _io.emit(processEl.clientIp + '-svn', '<span>' +
                            data +'</span>');
                            res.json(
                                {
                                    error: "false",
                                    msg: "SVN committ done server"
                                });
                            doneCommit();
                        }
                    });
                } else {
                    console.log(err2);
                    _io.emit(processEl.clientIp + '-svn', 'SVN Error.');
                    _io.emit(processEl.clientIp + '-svn', '<span style="color: #ea5965;">'+err2+'</span>');
                    res.json(
                        {
                            error: "true",
                            msg: "error in svn commit process"
                        });
                    doneCommit();
                }

            });
        }
    });

    /**
     * Helper Function - Update xpath config
     * @param appName
     * @param xpaths
     * @param success
     * @param failure
     */
    function loadAndUpdateApplicationXpathConfig(appName,xpaths, success, failure) {

        var _configFileLocation = _serverDirectory + "/server/lib/jf/src/test/resources/config/" +
            appName.toLowerCase().trim() + "_config.properties";

        // load config file to memory
        properties.parse (_configFileLocation, { path: true }, function (error, configObj){

            if (error) failure();
            for(var i in xpaths) {

                var _searchKey = xpaths[i].split(/=(.+)?/)[0];
                var _keyValue = xpaths[i].split(/=(.+)?/)[1];
                var _xpathKeys = Object.keys(configObj);

                var iFound = false;
                for (var key in _xpathKeys) {
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
                var _temp = ((key == null ? "": key.trim()).replace(/ /g, "\\ ")) + ' = ' +
                    ((configObj[key] == null ? "": configObj[key].trim()).replace(/ /g, "\\ "));
                _configArray.push(_temp);
            };

            var updatedApplicationXpathConfig = {
                path : _configFileLocation ,
                data : _configArray.join("\n")
            };

            success(updatedApplicationXpathConfig);
        });

    };

};



