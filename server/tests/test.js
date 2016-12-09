/**
 * Created by AbhishekK on 12/7/2016.
 */

var util = require('../utils/');

var assert = require('assert');
xdescribe('Util Fn Debugging', function() {
    describe('Util', function() {
        it('should write a file', function() {
            assert.equal(-1, [1,2,3].indexOf(4));

            var _a = [
                {
                    path: __dirname+'\\test.txt',
                    data: 'test'
                },
                {
                    path: __dirname+'\\ss\\ss/dd/test2.txt',
                    data: 'test2'
                }
                ,
                {
                    path: __dirname+'\\ss\\ss/dd/test2b.txt',
                    data: 'test2b'
                }
            ]
            util.write_files(_a)
                .then(function (data) {
                    // Do something with value4
                    console.log(data);

                })
                .catch(function (err) {
                    // Handle any error from all above steps
                    // Do something with value4
                    console.log('failed');
                    console.log(err);

                })
                .done();
        });

        xit('should create dir', function() {
               util.create_dir(__dirname + '/test99/ee/rr/tt/d/ee/e/e')
                   .then(function (data) {
                       // Do something with value4
                       console.log('success');
                       console.log(data);

                   })
                   .catch(function (err) {
                       // Handle any error from all above steps
                       // Do something with value4
                       console.log('failed');
                       console.log(err);

                   })
                   .done();
        });

        xit('should run command', function() {

            var cmd = [
                {
                    command: 'date',  // cmd command to run
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
                    command: 'ls',  // cmd command to run
                    options:{
                        encoding: 'utf8',
                        timeout: 0,
                        maxBuffer: 200*1024,
                        killSignal: 'SIGTERM',
                        cwd: null,
                        env: null
                    }
                }
            ];


            util.run_cmd(cmd)
                .then(function (data) {
                    // Do something with value4
                    console.log('success');
                    console.log(data);

                })
                .catch(function (err) {
                    // Handle any error from all above steps
                    // Do something with value4
                    console.log('failed');
                    console.log(err);

                })
                .done();

        });
    });
});