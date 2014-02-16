package com.android.deskclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;

import com.throughawall.colorpicker.ColorPickerFragment;
import java.lang.Override;
import java.lang.String;

/**
 * Created by andrew on 2/4/14.
 */
public class LightPicker extends Activity {
    public static String EXTRA_COLOR = "com.android.deskclock.action.COLOR";
    public static String EXTRA_TIME = "com.android.deskclock.action.TIME";

    private ColorPickerFragment mColorPicker;
    private NumberPicker mMinutePicker;
    private NumberPicker mSecondPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_picker);

        Intent intent = getIntent();

        mColorPicker = (ColorPickerFragment)
                getFragmentManager().findFragmentById(R.id.light_color_picker_fragment);

        if(intent.hasExtra(EXTRA_COLOR)){
            mColorPicker.setColor(intent.getIntExtra(EXTRA_COLOR, 0));
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mMinutePicker = (NumberPicker) findViewById(R.id.minute_picker);
        mMinutePicker.setMinValue(0);
        mMinutePicker.setMaxValue(59);
        mMinutePicker.setFormatter(new TimeFormatter());

        mSecondPicker = (NumberPicker) findViewById(R.id.second_picker);
        mSecondPicker.setMinValue(0);
        mSecondPicker.setMaxValue(59);
        mSecondPicker.setFormatter(new TimeFormatter());

        if(intent.hasExtra(EXTRA_TIME)){
            int timeSec = intent.getIntExtra(EXTRA_TIME, 0) / 1000;

            mSecondPicker.setValue(timeSec % 60);
            mMinutePicker.setValue(timeSec / 60);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.light_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            Intent result = new Intent();
            result.putExtra(EXTRA_COLOR, mColorPicker.getColor());
            result.putExtra(EXTRA_TIME, getTime());
            setResult(RESULT_OK, result);
            finish();
            return true;
        } else if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected int getTime(){
        return (mMinutePicker.getValue() * 60 + mSecondPicker.getValue()) * 1000;
    }

    private static class TimeFormatter implements NumberPicker.Formatter {
        @Override
        public String format(int i) {
            return String.format("%02d", i);
        }
    }
}
