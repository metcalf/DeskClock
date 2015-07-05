package com.android.deskclock;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Vibrator;

import com.android.deskclock.provider.AlarmInstance;
import com.android.deskclock.provider.Alarm;
import com.throughawall.colorpicker.ColorTask;
import com.throughawall.colorpicker.SetColorTask;

import java.io.UnsupportedEncodingException;

/**
 * Created by andrew on 2/16/14.
 */
public class AlarmLightTask extends SetColorTask {
    private static final long[] sVibratePattern = new long[] { 0, 150, 150, 150, 150, 150 };

    public AlarmLightTask(String impId, int color, int time, Context context) throws UnsupportedEncodingException, IllegalStateException {
        super(impId, color, time, context);

      if(!canLight()){
        throw new IllegalStateException("Cannot use light alarm");
      }
    }

    public AlarmLightTask(AlarmInstance instance, Context context) throws UnsupportedEncodingException, IllegalStateException {
        super(context.getResources().getString(R.string.imp_id),
                instance.mLightColor, instance.mLightTime, context);
    }

    public AlarmLightTask(Alarm alarm, Context context) throws UnsupportedEncodingException, IllegalStateException {
        super(context.getResources().getString(R.string.imp_id),
                alarm.lightColor, alarm.lightTime, context);
    }

    @Override
    protected void onPostExecute(ColorResponse result) {
        if(mError != null){
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(sVibratePattern, 0);
            LogUtils.e("Error activating lights", mError);
        }
    }

    private boolean canLight(){
        WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        String ssid = mContext.getResources().getString(R.string.imp_ssid_substr);

        if(wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
            for(ScanResult result : wifi.getScanResults()){
                if(result.SSID.contains(ssid)){
                    return true;
                }
            }
            LogUtils.v(String.format("Cannot use light alarm because %s is not a substring of a visible network", ssid));
        } else {
            LogUtils.v(String.format("Cannot use light alarm because wifi is in state: %s", wifi.getWifiState()));
        }

        return false;
    }
}
