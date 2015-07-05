package com.android.deskclock.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.widget.RemoteViews;

import com.android.deskclock.AlarmLightTask;
import com.android.deskclock.LogUtils;
import com.android.deskclock.R;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.provider.ClockContract;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by andrew on 2/16/14.
 */
public class LightAppWidget extends AppWidgetProvider {
    private static final String BUTTON_INTENT = "com.android.deskclock.action.light_appwidget";
    private static final long[] sVibratePattern = new long[]{0, 150, 150, 150, 150, 150};

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (BUTTON_INTENT.equals(intent.getAction())) {
            startLights(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, getClass());
            intent.setAction(BUTTON_INTENT);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.light_appwidget);
            views.setOnClickPendingIntent(R.id.lightWidgetButton, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void startLights(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        String selection = ClockContract.AlarmsColumns.LIGHT_ENABLED + " != 0 AND " +
                ClockContract.AlarmsColumns.LIGHT_ENABLED + " != 0";

        List<Alarm> alarms = Alarm.getAlarms(context.getContentResolver(), selection);

        if (alarms.size() < 1) {
            LogUtils.v("No alarm has a light setting");
            vibrator.vibrate(sVibratePattern, -1);
            return;
        }

        final Calendar currTime = Calendar.getInstance();

        Alarm alarm = Collections.min(alarms, new Comparator<Alarm>() {
            @Override
            public int compare(Alarm alarm, Alarm alarm2) {
                if (alarm.enabled && !alarm2.enabled) {
                    return -1;
                } else if (!alarm.enabled && alarm2.enabled) {
                    return 1;
                } else {
                    return alarm.createInstanceAfter(currTime).getAlarmTime().compareTo(
                            alarm2.createInstanceAfter(currTime).getAlarmTime()
                    );
                }
            }
        });

        try {
            (new AlarmLightTask(alarm, context)).execute();
        } catch (UnsupportedEncodingException e) {
            LogUtils.e("Exception instantiating light task.", e);
            vibrator.vibrate(sVibratePattern, -1);
        } catch (IllegalStateException e) {
            LogUtils.e("Exception instantiating light task.", e);
            vibrator.vibrate(sVibratePattern, -1);
        }
    }
}
