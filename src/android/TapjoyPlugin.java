package org.apache.cordova.tapjoy;

/**
 * Created by DC on 5/24/2018.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;


import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJPlacementVideoListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;
import com.tapjoy.TapjoyLog;

import java.util.Hashtable;

import android.app.Application;


class MyApp extends Application {
    //private static MyApp instance;
    private static Context mContext;

    public static MyApp getInstance() {
        return null;
    }

    public static Context getContext() {
        //  return instance.getApplicationContext();
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  instance = this;
        mContext = getApplicationContext();
    }
}
//@SuppressLint("NewApi")
public class TapjoyPlugin extends CordovaPlugin {
    //implements TJPlacementListener, TJPlacementVideoListener 

    String TAG = "GeolocationPlugin";
    CallbackContext context;

    String [] permissions = { };
    
    String userid="";

    private TJPlacement directPlayPlacement;
    private TJPlacement offerwallPlacement;

  

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        //LOG.d(TAG, "We are entering execute");
        context = callbackContext;
        if(action.equals("getPermission"))
        {
            if(hasPermisssion())
            {
                PluginResult r = new PluginResult(PluginResult.Status.OK);
                context.sendPluginResult(r);
                return true;
            }
            else {
                PermissionHelper.requestPermissions(this, 0, permissions);
            }
            return true;
        }else if (action.equals("inittapjoy")){            
            userid=args.optString(0,"");
            connectToTapjoy();
            return true;
        }
        else if(action.equals("callShowOffers")){
            callShowOffers();
            return true;
        }
        else if(action.equals("showDirectPlayContent")){
            showDirectPlayContent();
            return true;
        }
        return false;
    }


    private void connectToTapjoy() {
        com.tapjoy.Tapjoy.setDebugEnabled(true);
        
        // OPTIONAL: For custom startup flags.
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");

        // If you are not using Tapjoy Managed currency, you would set your own user ID here.
        if (userid!=""){
            connectFlags.put(TapjoyConnectFlag.USER_ID, userid);        
        }

        // Connect with the Tapjoy server.  Call this when the application first starts.
        // REPLACE THE SDK KEY WITH YOUR TAPJOY SDK Key.
        String tapjoySDKKey = "lGE8RIvpRbuGf-IcXZTjFAECqkSxNjuRiCS1oRMHRpmaK0PH9MAs7u7q4iRO";

        com.tapjoy.Tapjoy.setGcmSender("34027022155");
        // NOTE: This is the only step required if you're an advertiser.
        com.tapjoy.Tapjoy.setActivity(this.cordova.getActivity());
        Context context=this.cordova.getActivity().getApplicationContext();//MyApp.getContext();//
        if(context==null){

            Log.d("CORDOVA CONTEXT: ","IS NULL");
        }else{

            Log.d("CORDOVA CONTEXT: ","NOT NULL");
        }

        com.tapjoy.Tapjoy.connect(context, tapjoySDKKey, connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                //TapjoyPlugin.this.onConnectSuccess();
            }

            @Override
            public void onConnectFailure() {
                //TapjoyPlugin.this.onConnectFail();
            }
        });

    }
//    private void onConnectSuccess() {

//        // Start preloading direct play event upon successful connect
//        directPlayPlacement = com.tapjoy.Tapjoy.getPlacement("Video Placement", this);

//        // Set Video Listener to anonymous callback
//        directPlayPlacement.setVideoListener(new TJPlacementVideoListener() {
//            @Override
//            public void onVideoStart(TJPlacement placement) {
//                Log.i(TAG, "Video has started has started for: " + placement.getName());
//            }

//            @Override
//            public void onVideoError(TJPlacement placement, String message) {
//                Log.i(TAG, "Video error: " + message + " for " + placement.getName());
//            }

//            @Override
//            public void onVideoComplete(TJPlacement placement) {
//                Log.i(TAG, "Video has completed for: " + placement.getName());

//            }

//        });

//        directPlayPlacement.requestContent();

////        // NOTE:  The get/spend/award currency methods will only work if your virtual currency
////        // is managed by Tapjoy.
////        //
////        // For NON-MANAGED virtual currency, Tapjoy.setUserID(...)
////        // must be called after requestTapjoyConnect.

////        // Setup listener for Tapjoy currency callbacks
////        com.tapjoy.Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener() {
////            @Override
////            public void onEarnedCurrency(String currencyName, int amount) {
////                //earnedCurrency = true;
////                //updateTextInUI("You've just earned " + amount + " " + currencyName);
////                //showPopupMessage("You've just earned " + amount + " " + currencyName);
////            }
////        });
//    }

//    /**
//     * Handles a failed connect to Tapjoy
//     */
//    private void onConnectFail() {
//        Log.e(TAG, "Tapjoy connect call failed");
//        //updateTextInUI("Tapjoy connect failed!");
//    } 



    private void showDirectPlayContent() {
//        // Check if content is available and if it is ready to show
//        if (directPlayPlacement.isContentAvailable()) {
//            if (directPlayPlacement.isContentReady()) {
//                directPlayPlacement.showContent();
//            } else {
//                //setButtonEnabledInUI(currentButton, true);
//                //updateTextInUI("Direct play video not ready to show");
//            }

//        } else {
//            //setButtonEnabledInUI(currentButton, true);
//            //updateTextInUI("No direct play video to show");
//        }

            
        //directPlayPlacement = Tapjoy.getPlacement("Video Placement", this);
        directPlayPlacement = Tapjoy.getPlacement("Video Placement", new TJPlacementListener() {
            @Override
            public void onRequestSuccess(TJPlacement placement) {
                TapjoyLog.i(TAG, "onRequestSuccess for placement " + placement.getName());
                //updateTextInUI("onRequestSuccess for placement " + placement.getName());

                if (!placement.isContentAvailable()) {
                    //updateTextInUI("No Offerwall content available");
                }

                //setButtonEnabledInUI(currentButton, true);
            }

            @Override
            public void onRequestFailure(TJPlacement placement, TJError error) {
                TapjoyLog.i(TAG, "onRequestFailure for placement " + placement.getName());
                //setButtonEnabledInUI(currentButton, true);
                //updateTextInUI("Offerwall error: " + error.message);
            }

            @Override
            public void onContentReady(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentReady for placement " + placement.getName());

                //updateTextInUI("Offerwall request success");
                placement.showContent();
            }

            @Override
            public void onContentShow(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentShow for placement " + placement.getName());
            }

            @Override
            public void onContentDismiss(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentDismiss for placement " + placement.getName());
            }

            @Override
            public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
            }

            @Override
            public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
            }
        });
        
        directPlayPlacement.setVideoListener(new TJPlacementVideoListener() {            
            @Override
            public void onVideoStart(TJPlacement placement) {
                Log.i(TAG, "Video has started has started for: " + placement.getName());
            }

            @Override
            public void onVideoError(TJPlacement placement, String errorMessage) {
                Log.i(TAG, "Video error: " + errorMessage +  " for " + placement.getName());
            }

            @Override
            public void onVideoComplete(TJPlacement placement) {
                Log.i(TAG, "Video has completed for: " + placement.getName());                
            }
        } );
         

        //directPlayPlacement.setVideoListener(this);
        directPlayPlacement.requestContent();

    }

    private void callShowOffers() {
        // Construct TJPlacement to show Offers web view from where users can download the latest offers for virtual currency.
        offerwallPlacement = com.tapjoy.Tapjoy.getPlacement("Offerwall Placement", new TJPlacementListener() {
            @Override
            public void onRequestSuccess(TJPlacement placement) {
                //updateTextInUI("onRequestSuccess for placement " + placement.getName());

                if (!placement.isContentAvailable()) {
                    //updateTextInUI("No Offerwall content available");
                }

                //setButtonEnabledInUI(currentButton, true);
            }

            @Override
            public void onRequestFailure(TJPlacement placement, TJError error) {
                TapjoyLog.i(TAG, "onRequestFailure for placement " + placement.getName());
                //setButtonEnabledInUI(currentButton, true);
                //updateTextInUI("Offerwall error: " + error.message);
            }

            @Override
            public void onContentReady(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentReady for placement " + placement.getName());

                //updateTextInUI("Offerwall request success");
                placement.showContent();
            }

            @Override
            public void onContentShow(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentShow for placement " + placement.getName());
            }

            @Override
            public void onContentDismiss(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentDismiss for placement " + placement.getName());
            }

            @Override
            public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
            }

            @Override
            public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
            }
        });

        // Add this class as a video listener
        //offerwallPlacement.setVideoListener(this);
        offerwallPlacement.requestContent();
    }




    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException
    {
        PluginResult result;
        //This is important if we're using Cordova without using Cordova, but we have the geolocation plugin installed
        if(context != null) {
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_DENIED) {
                    LOG.d(TAG, "Permission Denied!");
                    result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
                    context.sendPluginResult(result);
                    return;
                }

            }
            result = new PluginResult(PluginResult.Status.OK);
            context.sendPluginResult(result);
        }
    }

    public boolean hasPermisssion() {
        for(String p : permissions)
        {
            if(!PermissionHelper.hasPermission(this, p))
            {
                return false;
            }
        }
        return true;
    }

    /*
     * We override this so that we can access the permissions variable, which no longer exists in
     * the parent class, since we can't initialize it reliably in the constructor!
     */

    public void requestPermissions(int requestCode)
    {
        PermissionHelper.requestPermissions(this, requestCode, permissions);
    }


}
