package com.example.student.aristaios;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

public class ThresholdActivity extends AppCompatActivity {
    private int sensorLocation=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

}
