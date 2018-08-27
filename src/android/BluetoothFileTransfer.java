package btFileTransferIndigo;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import java.io.File;
import java.util.List;

/**
 * This class echoes a string called from JavaScript.
 */
public class BluetoothFileTransfer extends CordovaPlugin {
  private static final int DISCOVER_DURATION = 300;
  private static final int REQUEST_BLU = 1;
  static String path;
  CallbackContext callbackContextBt;
  BluetoothAdapter btAdatper = BluetoothAdapter.getDefaultAdapter();

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("sendFile")) {
      String message = args.getString(0);
      this.sendFile(message, callbackContext);
      return true;
    }
    return false;
  }

  private void sendFile(String pathFile, CallbackContext callbackContext) {
    path = pathFile;
    callbackContextBt = callbackContext;
    if (pathFile != null && pathFile.length() > 0) {
         if (btAdatper == null) {
            Toast.makeText(cordova.getActivity().getWindow().getContext(), "Device not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            enableBluetooth();
        }
    } else {
      callbackContextBt.error("Expected one non-empty string argument.");
    }
  }

  //exit to application---------------------------------------------------------------------------
  public void exit(View V) {
    btAdatper.disable();
    Toast.makeText(cordova.getActivity().getWindow().getContext(),"*** Now Bluetooth is off... Thanks. ***",Toast.LENGTH_LONG).show();
    cordova.getActivity().finish(); 
  }

public void enableBluetooth() {
    Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
    cordova.setActivityResultCallback(this);
    cordova.getActivity().startActivityForResult(discoveryIntent, REQUEST_BLU);
}

//Override method for sending data via bluetooth availability--------------------------
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.setType("*/*");
        File file = new File(path);

        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        PackageManager pm = cordova.getActivity().getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(i, 0);
        if (list.size() > 0) {
            String packageName = null;
            String className = null;
            boolean found = false;

            for (ResolveInfo info : list) {
                packageName = info.activityInfo.packageName;
                if (packageName.equals("com.android.bluetooth")) {
                    className = info.activityInfo.name;
                    found = true;
                    break;
                }
            }
            //CHECK BLUETOOTH available or not------------------------------------------------
            if (!found) {
                Toast.makeText(cordova.getActivity().getWindow().getContext(), "Bluetooth not been found", Toast.LENGTH_LONG).show();
                callbackContextBt.error("Bluetooth not been found.");

            } else {
                i.setClassName(packageName, className);
                cordova.getActivity().startActivity(i);
                callbackContextBt.success("success");
            }
        }
    } else {
        Toast.makeText(cordova.getActivity().getWindow().getContext(), "Bluetooth is cancelled", Toast.LENGTH_LONG).show();
        callbackContextBt.error("Bluetooth is cancelled.");
    }
}

}
