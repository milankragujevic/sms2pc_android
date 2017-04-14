package com.milankragujevic.sms2pc;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by Milan on 14.4.2017.
 */

class DeviceID {
    public static String get(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
