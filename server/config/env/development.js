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
    },
    builder : {
        results : {
            server: "http://sims-builder-s3.herokuapp.com",
            api: "/api/skilltest/tasks/{task-id}/test-status", // ?step={step-number>}
            token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU5NWYzMWJjODI4ZWU0MzYxNDI1NDE0ZiIsInVzZXJWZXJzaW9uIjowLCJ0eXBlIjoxLCJpYXQiOjE0OTk0MTA5ODAsImV4cCI6MTgxNDc3MDk4MH0.ewVIP398aCVirDzs0BpwVxp2Z94phmFOjNtQFlnjLGM"
        }
    }
};

module.exports = exports = config;