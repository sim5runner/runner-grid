/**
 * Created by AbhishekK
 */
'use strict';

const router = require('express').Router();
var Xpath     = require('./../models/app.server.models.xpath');

/**
 *
 * @param req
 * @param res
 * @param next
 * sample:
 *
 {"app_type": "word",
    "tags": ["123.t1","234.t1"],
    "xpath": {
        "key": "home_button",
        "value": "//[name=home]"
    }
    }
 *
 */

function arrayUnique(array) {
    var a = array.concat();
    for(var i=0; i<a.length; ++i) {
        for(var j=i+1; j<a.length; ++j) {
            if(a[i] === a[j])
                a.splice(j--, 1);
        }
    }

    return a;
}

exports.addXpath = function (req, res) {

    Xpath.find({$and: [
            {'app_type': req.body.app_type},
            {'xpath.key': req.body.xpath.key}
        ]}
        , function(err, data) {
        if (err) {
            res.json({
                "errors": {
                    "errorMessage": err,
                    "errorCode": "PROCESSING_ERROR"
                }
            });
        }
        if(data.length) {   // if xpath exist

            if (data[0].xpath.value === req.body.xpath.value) {   // only add tag

                req.body.tags = arrayUnique(data[0].tags.concat(req.body.tags));
                Xpath.findOneAndUpdate({$and: [
                        {'app_type': req.body.app_type},
                        {'xpath.key': req.body.xpath.key}
                    ]}
                    , {$set: {"tags" : req.body.tags}}, function(err, doc){
                        if (err) {
                            res.json({
                                "errors": {
                                    "errorMessage": err,
                                    "errorCode": "PROCESSING_ERROR"
                                }
                            });
                        }
                        res.json(doc);
                    });

            } else {    // if new xpath value for existing key - return error
                res.json({ "errors": {
                    "errorMessage": "Xpath already exists in database",
                    "errorCode": "EXISTS_IN_DB"
                } });
            }
        } else {    // create new xpath
            var xpath = new Xpath(req.body);
            xpath.save(function(err, xpathData) {
                if (err) {
                    res.json({
                        "errors": {
                            "errorMessage": err,
                            "errorCode": "PROCESSING_ERROR"
                        }
                    });
                }
                res.json(xpathData);
            });
        }
    });
};

exports.getXpaths = function (req, res) {

    Xpath.find(function(err, xpathData) {
        if (err) {
            res.json({
                "errors": {
                    "errorMessage": err,
                    "errorCode": "PROCESSING_ERROR"
                }
            });
        }
        res.json(xpathData);
    });

};

exports.getApplicationXpaths = function (req, res) {
    Xpath.find({'app_type': req.params.app_type},function(err, xpathList) {
        if (err) {
            res.json({
                "errors": {
                    "errorMessage": err,
                    "errorCode": "PROCESSING_ERROR"
                }
            });
        }
        res.json(xpathList);
    });
};

exports.getApplicationXpathValue = function (req, res) {

    Xpath.find({$and: [
        {'app_type': req.params.app_type},
        {'xpath.key': req.params.xpath_key}
    ]},function(err, xpathList) {
        if (err) {
            res.json({
                "errors": {
                    "errorMessage": err,
                    "errorCode": "PROCESSING_ERROR"
                }
            });
        }
        res.json(xpathList);
    });

};

exports.updateApplicationXpath = function (req, res) {

};
