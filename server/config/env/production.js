var config= {
    mongo:{
        "prefix": "mongodb://",
        "dbURL": "ds023674.mlab.com:23674/runnerv2",
        "username": "root",
        "password": "admin"
    },
    api:{
        script: {
            java: "http://localhost:4040/api/scripts/task/",
            xml: "http://localhost:4040/api/scripts/task/"
        }
    }
};

module.exports = exports = config;