package com.android.deskclock;

import android.content.Context;
import android.os.Vibrator;

import com.throughawall.colorpicker.ColorTask;
import com.throughawall.colorpicker.SetColorTask;

import java.io.UnsupportedEncodingException;

/**
 * Created by andrew on 2/16/14.
 */
public class AlarmLightTask extends SetColorTask {
    private static final long[] sVibratePattern = new long[] { 0, 150, 150, 150 };

    public AlarmLightTask(Alarm alarm, Context context) throws UnsupportedEncodingException {
        super(context.getResources().getString(R.string.imp_id),
                alarm.lightColor, alarm.lightTime, context);
    }

    @Override
    protected void onPostExecute(ColorResponse result) {
        if(mError != null){
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(sVibratePattern, 0);
            Log.e("Error activating lights", mError);
        }
    }
}
