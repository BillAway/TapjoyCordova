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

//@SuppressLint("NewApi")
public class TapjoyPlugin extends CordovaPlugin implements TJPlacementListener, TJPlacementVideoListener {

    String TAG = "GeolocationPlugin";
    CallbackContext context;

    String [] permissions = { };

    private TJPlacement directPlayPlacement;
    private TJPlacement offerwallPlacement;

    /*
     * TJPlacement callbacks
     */
    @Override
    public void onRequestSuccess(TJPlacement placement) {
        // If content is not available you can note it here and act accordingly as best suited for your app
        Log.i(TAG, "Tapjoy on request success, contentAvailable: " + placement.isContentAvailable());
    }

    @Override
    public void onRequestFailure(TJPlacement placement, TJError error) {
        Log.i(TAG, "Tapjoy send event " + placement.getName() + " failed with error: " + error.message);
    }

    @Override
    public void onContentReady(TJPlacement placement) {
    }

    @Override
    public void onContentShow(TJPlacement placement) {
    }

    @Override
    public void onContentDismiss(TJPlacement placement) {
        Log.i(TAG, "Tapjoy direct play content did disappear");

        //setButtonEnabledInUI(getDirectPlayVideoAd, true);

        // Best Practice: We recommend calling getCurrencyBalance as often as possible so the user's balance is always up-to-date.
        //Tapjoy.getCurrencyBalance(TapjoyEasyApp.this);

        // Begin preloading the next placement after the previous one is dismissed
        directPlayPlacement = Tapjoy.getPlacement("video_unit", this);

        // Set Video Listener to anonymous callback
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

                // Best Practice: We recommend calling getCurrencyBalance as often as possible so the user�s balance is always up-to-date.
                //Tapjoy.getCurrencyBalance(TapjoyEasyApp.this);
            }
        });

        directPlayPlacement.requestContent();
    }

    @Override
    public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
    }

    @Override
    public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
    }

    /**
     * Video listener callbacks
     */
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

        // Best Practice: We recommend calling getCurrencyBalance as often as possible so the user�s balance is always up-to-date.
        //Tapjoy.getCurrencyBalance(TapjoyEasyApp.this);
    }

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
            connectToTapjoy();
        }
        return false;
    }


    private void connectToTapjoy() {
        // OPTIONAL: For custom startup flags.
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");

        // If you are not using Tapjoy Managed currency, you would set your own user ID here.
        //	connectFlags.put(TapjoyConnectFlag.USER_ID, "A_UNIQUE_USER_ID");

        // Connect with the Tapjoy server.  Call this when the application first starts.
        // REPLACE THE SDK KEY WITH YOUR TAPJOY SDK Key.
        String tapjoySDKKey = "u6SfEbh_TA-WMiGqgQ3W8QECyiQIURFEeKm0zbOggubusy-o5ZfXp33sTXaD";

        com.tapjoy.Tapjoy.setGcmSender("34027022155");

        // NOTE: This is the only step required if you're an advertiser.
        com.tapjoy.Tapjoy.connect( this.cordova.getActivity().getApplicationContext(), tapjoySDKKey, connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                TapjoyPlugin.this.onConnectSuccess();
            }

            @Override
            public void onConnectFailure() {
                TapjoyPlugin.this.onConnectFail();
            }
        });
    }
    private void onConnectSuccess() {

        // Start preloading direct play event upon successful connect
        directPlayPlacement = com.tapjoy.Tapjoy.getPlacement("video_unit", this);

        // Set Video Listener to anonymous callback
        directPlayPlacement.setVideoListener(new TJPlacementVideoListener() {
            @Override
            public void onVideoStart(TJPlacement placement) {
                Log.i(TAG, "Video has started has started for: " + placement.getName());
            }

            @Override
            public void onVideoError(TJPlacement placement, String message) {
                Log.i(TAG, "Video error: " + message + " for " + placement.getName());
            }

            @Override
            public void onVideoComplete(TJPlacement placement) {
                Log.i(TAG, "Video has completed for: " + placement.getName());

            }

        });

        directPlayPlacement.requestContent();

        // NOTE:  The get/spend/award currency methods will only work if your virtual currency
        // is managed by Tapjoy.
        //
        // For NON-MANAGED virtual currency, Tapjoy.setUserID(...)
        // must be called after requestTapjoyConnect.

        // Setup listener for Tapjoy currency callbacks
        com.tapjoy.Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener() {
            @Override
            public void onEarnedCurrency(String currencyName, int amount) {
                //earnedCurrency = true;
                //updateTextInUI("You've just earned " + amount + " " + currencyName);
                //showPopupMessage("You've just earned " + amount + " " + currencyName);
            }
        });
    }

    /**
     * Handles a failed connect to Tapjoy
     */
    private void onConnectFail() {
        Log.e(TAG, "Tapjoy connect call failed");
        //updateTextInUI("Tapjoy connect failed!");
    }



    private void showDirectPlayContent() {
        // Check if content is available and if it is ready to show
        if (directPlayPlacement.isContentAvailable()) {
            if (directPlayPlacement.isContentReady()) {
                directPlayPlacement.showContent();
            } else {
                //setButtonEnabledInUI(currentButton, true);
                //updateTextInUI("Direct play video not ready to show");
            }

        } else {
            //setButtonEnabledInUI(currentButton, true);
            //updateTextInUI("No direct play video to show");
        }
    }

    private void callShowOffers() {
        // Construct TJPlacement to show Offers web view from where users can download the latest offers for virtual currency.
        offerwallPlacement = com.tapjoy.Tapjoy.getPlacement("offerwall_unit", new TJPlacementListener() {
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
        offerwallPlacement.setVideoListener(this);
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