package com.android.deskclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.widget.RemoteViews;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by andrew on 2/16/14.
 */
public class LightAppWidgetProvider extends AppWidgetProvider {
    private static final String BUTTON_INTENT = "com.android.deskclock.action.light_appwidget";
    private static final long[] sVibratePattern = new long[] { 0, 150, 150, 150 };

    private Vibrator mVibrator;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (BUTTON_INTENT.equals(intent.getAction())) {
            startLights(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, getClass());
            intent.setAction(BUTTON_INTENT);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.light_appwidget);
            views.setOnClickPendingIntent(R.id.lightWidgetButton, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void startLights(Context context){
        List<Alarm> alarms = new ArrayList<Alarm>();
        final Cursor cursor = Alarms.getAlarmsCursor(context.getContentResolver());

        // Find all the alarms with light enabled
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        final Alarm a = new Alarm(cursor);
                        if(a.lightEnabled && a.lightColor != 0){
                            if (a.time == 0) {
                                a.time = Alarms.calculateAlarm(a);
                            }
                            alarms.add(a);
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }

        Alarm alarm = Collections.min(alarms, new Comparator<Alarm>() {
            @Override
            public int compare(Alarm alarm, Alarm alarm2) {
                if(alarm.enabled && !alarm2.enabled){
                    return -1;
                } else if(!alarm.enabled && alarm2.enabled){
                    return 1;
                } else {
                    return Long.valueOf(alarm.time).compareTo(alarm2.time);
                }
            }
        });

        if(alarm == null){
            Log.v("No alarm has a light setting");
            mVibrator.vibrate(sVibratePattern, -1);
        } else {
            try {
                (new AlarmLightTask(alarm, context)).execute();
            } catch (UnsupportedEncodingException e) {
                Log.e("Exception instantiating light task.", e);
            }
        }
    }
}
