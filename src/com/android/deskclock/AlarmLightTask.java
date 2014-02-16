package com.android.deskclock;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Vibrator;

import com.throughawall.colorpicker.ColorTask;
import com.throughawall.colorpicker.SetColorTask;

import java.io.UnsupportedEncodingException;

/**
 * Created by andrew on 2/16/14.
 */
public class AlarmLightTask extends SetColorTask {
    private static final long[] sVibratePattern = new long[] { 0, 150, 150, 150, 150, 150 };

    public AlarmLightTask(Alarm alarm, Context context) throws UnsupportedEncodingException, IllegalStateException {
        super(context.getResources().getString(R.string.imp_id),
                alarm.lightColor, alarm.lightTime, context);


        if(!canLight()){
            throw new IllegalStateException("Cannot use light alarm");
        }

    }

    @Override
    protected void onPostExecute(ColorResponse result) {
        if(mError != null){
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(sVibratePattern, 0);
            Log.e("Error activating lights", mError);
        }
    }

    private boolean canLight(){
        WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        String bssid = mContext.getResources().getString(R.string.imp_bssid);

        if(wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
            for(ScanResult result : wifi.getScanResults()){
                if(result.BSSID.equals(bssid)){
                    return true;
                }
            }
            Log.v(String.format("Cannot use light alarm because %s is not a visible network", bssid));
        } else {
            Log.v(String.format("Cannot use light alarm because wifi is in state: %s", wifi.getWifiState()));
        }

        return false;
    }
}
