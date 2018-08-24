# cordova plugin file transfer bluetooth android
plugin for transfer file to device android
# Install
Install: cordova plugin add https://github.com/hectorares/file-transfer-bluetooth-cordova.git

# Example
declare var cordova: any;

var yourPath = '/storage/emulated/0/Download/data.txt';
cordova.plugins.BluetoothFileTransfer.sendFile(this.yourPath, success, failure);
 var success = function(result) {
  alert(JSON.stringify(result, undefined, 2));
 }
 var failure = function(result) {
  alert(JSON.stringify(result, undefined, 2));
 }



