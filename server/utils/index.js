/**
 * Created by AbhishekK on 9/13/2016.
 */
var fs = require('fs');
var net = require('net');
var path = require('path');

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
        return String.fromCharCode.apply(null, new Uint16Array(buf));
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
        dirName = "//" + folderNames[0] + "//" + folderNames[1] + "//" + folderNames[2] + "//";
        var tmpFolderName = folderNames[0] + "_" + folderNames[1] + "_" + folderNames[2] + "_";
        dirName = dirName + taskXMLName.replace(new RegExp(tmpFolderName, 'g'), '').replace(new RegExp('_', 'g'), '.').replace(new RegExp('.xml', 'g'), '');

        console.log('dirName: ' + dirName);
        return dirName;

    }
    else {
        return null;
    }
};

exports.portInUse = portInUse;
exports.rmdirAsync = rmdirAsync;
exports.mkdirParent = mkdirParent;
exports.getDirFromXMlName = getDirFromXMlName;

