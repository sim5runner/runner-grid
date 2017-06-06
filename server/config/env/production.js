var config= {
    mongo:{
        "prefix": "mongodb://",
        "dbURL": "ds023674.mlab.com:23674/runnerv2",
        "username": "root",
        "password": "admin"
    },
    api:{
        script: {
            java: "http://service-scriptor.herokuapp.com/api/scripts/task/",
            xml: "http://service-scriptor.herokuapp.com/api/scripts/task/"
        }
    }
};

module.exports = exports = config;