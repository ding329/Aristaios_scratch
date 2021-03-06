package com.example.student.aristaios;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    static final String TEMP_CONVERSION = "C_or_F";
    int maxHumidityThresh;
    int minHumidityThresh;

    Context context;

    static final String BOOL_MINT = "checkbox_mint";
    static final String BOOL_MAXH = "checkbox_maxH";
    static final String BOOL_MINH = "checkbox_minH";
    static final String MYPREF = "test";
    int maxTempThresh;
    int minTempThresh;
    static SharedPreferences sharedPref;  //The static might break everything but it made the errors go away
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        sharedPref = context.getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        //if there are any values maintained in sharedPref
        if(sharedPref.getAll().size() >0)
        {
            //update values on the screen
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                 //   Log.i("ThresholdActivity", String.format("saved Instancestate not null :: %d :: ", sharedPref.getInt(MAX_TEMP, 0)));
                    TextView tempView = (TextView) findViewById(R.id.text_maxt);
                    tempView.setText(Integer.toString(sharedPref.getInt(MAX_TEMP, 0)));

                    tempView = (TextView) findViewById(R.id.text_mint);
                    tempView.setText(Integer.toString(sharedPref.getInt(MIN_TEMP, 0)));

                    tempView = (TextView) findViewById(R.id.text_maxh);
                    tempView.setText(Integer.toString(sharedPref.getInt(MAX_HUMIDITY, 0)));

                    tempView = (TextView) findViewById(R.id.text_minh);
                    tempView.setText(Integer.toString(sharedPref.getInt(MIN_HUMIDITY, 0)));

                    if(sharedPref.getInt(BOOL_MAXT,0) ==1) {
                        CheckBox checkbox  =(CheckBox) findViewById(R.id.checkbox_maxt);
                        checkbox.setChecked(true);
                    }
                    if(sharedPref.getInt(BOOL_MINT,0) ==1) {
                        CheckBox checkbox  =(CheckBox) findViewById(R.id.checkbox_mint);
                        checkbox.setChecked(true);
                    }
                    if(sharedPref.getInt(BOOL_MAXH,0) ==1) {
                        CheckBox checkbox  =(CheckBox) findViewById(R.id.checkbox_maxh);
                        checkbox.setChecked(true);
                    }
                    if(sharedPref.getInt(BOOL_MINH,0) ==1) {
                        CheckBox checkbox  =(CheckBox) findViewById(R.id.checkbox_minh);
                        checkbox.setChecked(true);
                    }
                    if(sharedPref.getInt(TEMP_CONVERSION,1)==1)
                    {
                        RadioButton radio = (RadioButton) findViewById(R.id.radioC);
                        radio.setChecked(true);
                    }
                    else if(sharedPref.getInt(TEMP_CONVERSION,1)==2)
                    {
                        RadioButton radio = (RadioButton) findViewById(R.id.radioF);
                        radio.setChecked(true);
                    }
                    else
                    {
                        RadioButton radio = (RadioButton) findViewById(R.id.radioC);
                        radio.setChecked(true);
                    }
                }
            });
        }

    }
    //the values in the instance are destroyed on the back button.  This is our attempt to resolve it

    public void onTempConversionButtonClicked(View view)
    {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId())
        {
            case R.id.radioC:
                if(checked)
                {
                    //TEMP_CONVERSION
                    editor.putInt(TEMP_CONVERSION, 1);
                    editor.commit();
                    break;
                }
            case R.id.radioF:
                if(checked)
                {
                    editor.putInt(TEMP_CONVERSION, 2);
                    editor.commit();
                    break;
                }
        }
    }
/*
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
*/
    public int getSensorLocation()
    {
        return sensorLocation;
    }

    //go back to the main activity and store the current values
    public void backToMain(View view)
    {
        //ensure the values are stored
        setMaxTempThresh((findViewById(R.id.text_maxt)));
        setMinTempThresh(findViewById(R.id.text_mint));
        setMaxHumidityThresh(findViewById(R.id.text_maxh));
        setMinHumidityThresh(findViewById(R.id.text_minh));
        setCheckBoxThresh();
      //  MainActivity.connectBoard();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
//the onclick is triggeered when clicked.  Therefore, if you do not click again via "Next" or touch screen it will not capture the value
    public void setMaxTempThresh(View view)
    {
        Log.i("ThresholdActivity", String.format("In the setMaxTempThresh"));
        TextView tempView = (TextView) findViewById(R.id.text_maxt);
        if(tempView.getText().toString().isEmpty() || tempView.getText().toString()== null ) {
            maxTempThresh=-1;
        }
        else {
            maxTempThresh= Integer.parseInt(tempView.getText().toString());
        }
        editor.putInt(MAX_TEMP, maxTempThresh);
        editor.commit();
    }
    public void setMinTempThresh(View view)
    {
        TextView tempView = (TextView) findViewById(R.id.text_mint);
        if(tempView.getText().toString().isEmpty() || tempView.getText().toString()== null ) {
            minTempThresh=-1;
        }
        else {
            minTempThresh= Integer.parseInt(tempView.getText().toString());
        }

        editor.putInt(MIN_TEMP, minTempThresh);
        editor.commit();
    }
    public void setMaxHumidityThresh(View view)
    {
        TextView tempView = (TextView) findViewById(R.id.text_maxh);
        if(tempView.getText().toString().isEmpty() || tempView.getText().toString()== null ) {
            maxHumidityThresh=-1;
        }
        else {
            maxHumidityThresh= Integer.parseInt(tempView.getText().toString());
        }
        editor.putInt(MAX_HUMIDITY, maxHumidityThresh);
        editor.commit();
    }
    public void setMinHumidityThresh(View view) {
        TextView tempView = (TextView) findViewById(R.id.text_minh);
        if (tempView.getText().toString().isEmpty() || tempView.getText().toString() == null) {
            minHumidityThresh = -1;
        } else {
            minHumidityThresh = Integer.parseInt(tempView.getText().toString());
        }
        editor.putInt(MIN_HUMIDITY, minHumidityThresh);
        editor.commit();
    }
    public void setCheckBoxThresh()
    {
        CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox_maxt);
        if(checkbox.isChecked()) {
            editor.putInt(BOOL_MAXT, 1);
            editor.commit();
        }
        else {
            editor.putInt(BOOL_MAXT, -1);
            editor.commit();
        }

        checkbox = (CheckBox) findViewById(R.id.checkbox_mint);
        if(checkbox.isChecked()) {
            editor.putInt(BOOL_MINT, 1);
            editor.commit();
        }
        else {
            editor.putInt(BOOL_MINT, -1);  //0 is calling a null instance
            editor.commit();
        }

        checkbox = (CheckBox) findViewById(R.id.checkbox_maxh);
        if(checkbox.isChecked()) {
            editor.putInt(BOOL_MAXH, 1);
            editor.commit();
        }
        else {
            editor.putInt(BOOL_MAXH, -1);
            editor.commit();
        }

        checkbox = (CheckBox) findViewById(R.id.checkbox_minh);
        if(checkbox.isChecked()) {
            editor.putInt(BOOL_MINH, 1);
            editor.commit();
        }
        else {
            editor.putInt(BOOL_MINH, -1);
            editor.commit();
        }
    }
    public static int getThreshold(String input)
    {
    //    Log.i("ThresholdActivity", String.format("In getThreshold ::%s::", input));
        int rval=-1;
        try{
            rval = sharedPref.getInt(input, 0);
        } catch (NullPointerException e)
        {
            Log.i("ThresholdActivity", String.format("null pointer exception::%s::", input));
            rval =-1;
        }
        return rval;
    }

}
