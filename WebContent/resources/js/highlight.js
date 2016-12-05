define(function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

var CtlHighlightRules = function() {

    // regexp must not have capturing parentheses. Use (?:) instead.
    // regexps are ordered -> the first match is used
    this.$rules = {
        "start" : [
            {
                token : "comment",
                regex : /;[^\n]+/
            } , {
                token : "keyword",
                regex : /make\s[^ \t\n\r;()]+/
            } , {
                token : "keyword",
                regex : /[*/+-]/
            }, {
                token : "keyword",
                regex : /(?:set-param!)|(?:set!)|(?:define-param)|(?:define)|(?:make)|(?:list)|(?:run-until)|(?:display-fluxes)|(?:reset-meep)/,
                next : "name"
            }, {
                token : "variable",
                regex : /(?:vector3)/
            }, {
                token : "variable.parameter",
                regex : "infinity|no-size"
            }, {
                token : "variable.parameter",
                regex : "[+-]?\\d+(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?\\b"
            }, {
                token : "constant.language.boolean",
                regex : "(?:true|false)\\b"
            }, {
                token : "constant.numeric",
                regex : /[^ \t\n\r;()]+/
            }
        ]
    };
    
};

oop.inherits(CtlHighlightRules, TextHighlightRules);

exports.CtlHighlightRules = CtlHighlightRules;
});
