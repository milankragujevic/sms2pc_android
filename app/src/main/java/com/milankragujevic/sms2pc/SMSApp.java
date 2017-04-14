package com.milankragujevic.sms2pc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Milan on 14.4.2017.
 */

public class SMSApp extends BroadcastReceiver {
    private static final String LOG_TAG = "SMSApp";
    /* package */
    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public void onReceive(Context context, Intent intent){
        if (intent.getAction().equals(ACTION)){
            Bundle bundle = intent.getExtras();
            String deviceId = DeviceID.get(context);
            if (bundle != null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                for (SmsMessage message : messages){
                    String strFrom = message.getDisplayOriginatingAddress();
                    String strMsg = message.getDisplayMessageBody();
                    Long numTime = message.getTimestampMillis();
                    String strTime = numTime.toString();
                    Toast.makeText(context, strFrom + ": " + strMsg + " at " + strTime, Toast.LENGTH_LONG).show();
                    AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                    RequestParams params = new RequestParams();
                    params.put("strFrom", strFrom);
                    params.put("strMsg", strMsg);
                    params.put("strTime", strTime);
                    params.put("deviceId", deviceId);
                    Log.d(LOG_TAG, "Received SMS message \"" + strMsg + "\" from \"" + strFrom + "\" at time \"" + strTime + "\"");
                    client.post("https://milankragujevic.com/sms2pc/receiver.php?api_key=gJBXV6vBOd", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            Log.d(LOG_TAG, "Starting request to API. ");
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            Log.d(LOG_TAG, "Received OK response from API. ");
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            Log.d(LOG_TAG, "Received ERROR response from API. ");
                        }
                        @Override
                        public void onRetry(int retryNo) {
                            Log.d(LOG_TAG, "Retrying request to API. ");
                        }
                    });
                }
            }
        }
    }
}
