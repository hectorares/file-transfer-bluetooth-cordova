var exec = require('cordova/exec');

exports.sendFile = function (arg0, success, error) {
    exec(success, error, 'BluetoothFileTransfer', 'sendFile', [arg0]);
};


