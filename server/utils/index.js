/**
 * Created by AbhishekK on 9/13/2016.
 */
var fs = require('fs');
var net = require('net');
var path = require('path');
var child_process = require('child_process');

var Client = require('svn-spawn');
var Q = require('q');

exports.arrayUnique = function arrayUnique(array) {
        var a = array.concat();
        for(var i=0; i<a.length; ++i) {
            for(var j=i+1; j<a.length; ++j) {
                if(a[i] === a[j])
                    a.splice(j--, 1);
            }
        }
        return a;
    };

exports.ab2str =  function ab2str(buf) {
    return String.fromCodePoint.apply(null, new Uint16Array(buf));
    };

var rmdirAsync = function(path, callback) {
    fs.readdir(path, function(err, files) {
        if(err) {
            // Pass the error on to callback
            callback(err, []);
            return;
        }
        var wait = files.length,
            count = 0,
            folderDone = function(err) {
                count++;
                // If we cleaned out all the files, continue
                if( count >= wait || err) {
                    fs.rmdir(path,callback);
                }
            };
        // Empty directory to bail early
        if(!wait) {
            folderDone();
            return;
        }

        // Remove one or more trailing slash to keep from doubling up
        path = path.replace(/\/+$/,"");
        files.forEach(function(file) {
            var curPath = path + "/" + file;
            fs.lstat(curPath, function(err, stats) {
                if( err ) {
                    callback(err, []);
                    return;
                }
                if( stats.isDirectory() ) {
                    rmdirAsync(curPath, folderDone);
                } else {
                    fs.unlink(curPath, folderDone);
                }
            });
        });
    });
};

var portInUse = function(port, callback) {
    var server = net.createServer(function(socket) {
        socket.write('Echo server\r\n');
        socket.pipe(socket);
    });

    server.listen(port, '127.0.0.1');
    server.on('error', function (e) {
        callback(true);
    });
    server.on('listening', function (e) {
        server.close();
        callback(false);
    });
};

var mkdirParent = function(dirPath, callback) {
    //Call the standard fs.mkdir
    var mode = null;
    fs.mkdir(dirPath, mode, function(error) {
        //When it fail in this way, do the custom steps
        if (error && error.errno === 34) {
            //Create all the parents recursively
            mkdirParent(path.dirname(dirPath), mode, callback);
            //And then the directory
            mkdirParent(dirPath, mode, callback);
        }
        //Manually run the callback since we used our own callback to do all these
        setTimeout(function(){ callback && callback(error); }, 3000);
    });
};

var getDirFromXMlName = function(taskXMLName){

    var folderNames = taskXMLName.split("_");

    if(folderNames.length == 6)
    {
        var dirName = "";
        dirName = "/" + folderNames[0] + "/" + folderNames[1] + "/" + folderNames[2] +
        "/";
        var tmpFolderName = folderNames[0] + "_" + folderNames[1] + "_" + folderNames[2] +
            "_";
        dirName = dirName + taskXMLName.replace(new RegExp(tmpFolderName, 'g'), '')
            .replace(new RegExp('_', 'g'), '.').replace(new RegExp('.xml', 'g'), '');

        console.log('dirName: ' + dirName);
        return dirName;

    }
    else {
        return null;
    }
};

var getServerIP = function() {
    var os = require('os');

    var interfaces = os.networkInterfaces();
    var addresses = [];
    for (var k in interfaces) {
        for (var k2 in interfaces[k]) {
            var address = interfaces[k][k2];
            if (address.family === 'IPv4' && !address.internal) {
                addresses.push(address.address);
            }
        }
    }
    return addresses;
};

var getUUID = function guid() {
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
        s4() + '-' + s4() + s4() + s4();
};

function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
        .toString(16)
        .substring(1);
};

var searchNodeInArray = function searchNodeInArray(arr) {
    var what, a = arguments, L = a.length,ret = [];
    while (L > 1 && arr.length) {
        what = a[--L];
        for (var i=arr.length-1; i>=0; i--) {
            if(JSON.stringify(arr[i]).indexOf((JSON.stringify(what)).substring(1,
                    (JSON.stringify(what)).length-1)) !=-1){
                ret.push(arr[i])
            }
        }
    }
    return ret;
};


function objectsAreSame(x, y) {
    var objectsAreSame = true;
    for(var propertyName in x) {
        if(x[propertyName] !== y[propertyName]) {
            objectsAreSame = false;
            break;
        }
    }
    return objectsAreSame;
};

/**
 * @param directory {string}
 * @returns {*promise}
 * @note: No input validation
 */
function create_dir ( directory ) {
    var def = Q.defer();
    try{ const dir = directory.split('/');
         dir.forEach(function(cp,ci,ar){
            var _td = (ar.slice(0,(ci+1))).join('/');
            if (!(fs.existsSync ( _td ) )) { fs.mkdirSync( _td ); }
            if (dir.length === (ci+1)) { def.resolve(directory); }
        });
    } catch(err){def.reject(err);}
    return def.promise;
};


/**
 * @param files
[
    {
     path: '',  // complete path contaning filename and ext
     data:''
    },
     {
     path: '',
     data:''
    }
]
 *
 * @returns {*promise}
 * @note: No input validation
 */

function write_files ( files ) {
    var deferred = Q.defer();
    var count = files.length;
    var out = [], completed = 0;
    files.forEach(function(f,i,files){
        var path = f.path.replace(/\\/g, "/"), data = f.data;
        create_dir((path.substring(0, path.lastIndexOf("/") + 1))) //create directory if not exist
            .then(function (success) {
                fs.writeFileSync ( path, data );  // write files
                  ++completed;
                if(completed === count) {deferred.resolve('success');}
            })
            .catch(function (err) {console.log(err);})
            .done();
    });
    return deferred.promise;
};

/**
 * @param cmds
 [
    {
      command: '',  // cmd command to run
      options:{
          encoding: 'utf8',
          timeout: 0,
          maxBuffer: 200*1024,
          killSignal: 'SIGTERM',
          cwd: null,
          env: null
        }
    },
     {
      command: '',  // cmd command to run
      options:{
          encoding: 'utf8',
          timeout: 0,
          maxBuffer: 200*1024,
          killSignal: 'SIGTERM',
          cwd: null,
          env: null
        }
    }

 ]
 *
 * @returns {*promise}
 * @note: No input validation
 */

function run_cmd ( cmds ) {
    var deferred = Q.defer();
    var count = cmds.length;
    var out = [], completed = 0;
    cmds.forEach(function(c,i,cmds){
        var cmd = c.command, opt = c.options;
        child_process.exec(cmd, opt, function (err, stdout, stderr){
            ++completed;
            out.push({  'err': err,
                        'stdout': stdout,
                        'stderr':stderr
                    });
            if (err) {deferred.reject(err);}
            if((completed) === count) {deferred.resolve(out);}
        });
    });
    return deferred.promise;
};

exports.portInUse = portInUse;
exports.rmdirAsync = rmdirAsync;
exports.mkdirParent = mkdirParent;
exports.getDirFromXMlName = getDirFromXMlName;
exports.getServerIP = getServerIP;
exports.getUUID = getUUID;
exports.searchNodeInArray = searchNodeInArray;
exports.create_dir = create_dir;
exports.write_files = write_files;
exports.run_cmd = run_cmd;
