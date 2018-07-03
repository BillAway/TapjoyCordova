
var exec = require('cordova/exec');

function TapjoyBA() { }

TapjoyBA.prototype.open = function(appId, callbackContext) {
    callbackContext = callbackContext || {};
    exec(callbackContext.success || null, callbackContext.error || null, 'TapjoyBA', 'open', [appId]);
};

//var tapjoyba = new TapjoyBA();
//module.exports = tapjoyba;
