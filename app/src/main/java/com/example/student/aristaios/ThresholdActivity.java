package com.example.student.aristaios;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

public class ThresholdActivity extends AppCompatActivity {
    private int sensorLocation=0;
    static final String MAX_TEMP = "text_maxt";
    static final String MIN_TEMP = "text_mint";
    static final String MAX_HUMIDITY = "text_maxh";
    static final String MIN_HUMIDITY = "text_minh";
    static final String BOOL_MAXT = "checkbox_maxt";
    static final String BOOL_MINT = "checkbox_mint";
    static final String BOOL_MAXH = "checkbox_maxH";
    static final String BOOL_MINH = "checkbox_maxH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(savedInstanceState != null)
        {
            Log.i("ThresholdActivity", String.format("saved Instancestate not null"));
            TextView tempView = (TextView) findViewById(R.id.text_maxt);
            tempView.setText(savedInstanceState.getInt(MAX_TEMP));
        }

    }
    //the values in the instance are destroyed on the back button.  Thi is our attempt to resolve it

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        TextView tempView = (TextView) findViewById(R.id.text_maxt);
        final int maxT = Integer.parseInt(tempView.getText().toString());
        Log.i("ThresholdActivity", String.format("MaxT :: %d", maxT));
        savedInstanceState.putInt(MAX_TEMP, maxT);

        tempView = (TextView) findViewById(R.id.text_mint);
        final int minT = Integer.parseInt(tempView.getText().toString());
        savedInstanceState.putInt(MIN_TEMP, minT);

        tempView = (TextView) findViewById(R.id.text_maxh);
        final int maxH = Integer.parseInt(tempView.getText().toString());
        savedInstanceState.putInt(MAX_HUMIDITY, maxH);

        tempView = (TextView) findViewById(R.id.text_minh);
        final int minH = Integer.parseInt(tempView.getText().toString());
        savedInstanceState.putInt(MIN_HUMIDITY, minH);

        if( ((CheckBox) findViewById(R.id.checkbox_maxt)).isChecked() )
        {
            savedInstanceState.putBoolean(BOOL_MAXT, true);
        }
        else
        {
            savedInstanceState.putBoolean(BOOL_MAXT, false);
        }

        if( ((CheckBox) findViewById(R.id.checkbox_mint)).isChecked() )
        {
            savedInstanceState.putBoolean(BOOL_MINT, true);
        }
        else
        {
            savedInstanceState.putBoolean(BOOL_MINT, false);
        }

        if( ((CheckBox) findViewById(R.id.checkbox_maxh)).isChecked() )
        {
            savedInstanceState.putBoolean(BOOL_MAXH, true);
        }
        else
        {
            savedInstanceState.putBoolean(BOOL_MAXH, false);
        }

        if( ((CheckBox) findViewById(R.id.checkbox_minh)).isChecked() )
        {
            savedInstanceState.putBoolean(BOOL_MINH, true);
        }
        else
        {
            savedInstanceState.putBoolean(BOOL_MINH, false);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.i("ThresholdActivity", String.format("OnRestoreIntanse State"));

        super.onRestoreInstanceState(savedInstanceState);
        TextView tempView = (TextView) findViewById(R.id.text_maxt);
        tempView.setText(savedInstanceState.getInt(MAX_TEMP));
        Log.i("ThresholdActivity", String.format("::%s::", tempView.getText() ));
    }

    public void onRadioButtonClicked(View view)
    {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId())
        {
            case R.id.radioWine:
                if(checked)
                {
                    sensorLocation=1;
                    Log.i("ThresholdActivity", String.format("sensor is in the wine celler"));
                    break;
                }
            case R.id.radioCheese:
                if(checked)
                {
                    sensorLocation=2;
                    Log.i("ThresholdActivity", String.format("sensor is in the cheese cave"));
                    break;
                }
            case R.id.radioKitchen:
                if(checked)
                {
                    sensorLocation=3;
                    Log.i("ThresholdActivity", String.format("sensor is in the kitchen"));
                    break;
                }
        }
    }

    public int getSensorLocation()
    {
        return sensorLocation;
    }

    public void backToMain(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
