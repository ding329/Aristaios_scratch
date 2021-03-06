package com.example.student.aristaios;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.content.*;
//import android.os.Message;

import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.module.MultiChannelTemperature;
import com.mbientlab.metawear.module.MultiChannelTemperature.*;
import com.mbientlab.metawear.module.Bme280Humidity;

import com.mbientlab.metawear.module.Timer;
import com.mbientlab.metawear.module.Settings;
import com.mbientlab.metawear.module.Settings.BatteryState;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.TimerTask;
//import java.util.Timer;
import android.os.Handler;

import javax.xml.transform.Source;


public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private MetaWearBoard mwBoard = null;
    private final String MW_MAC_ADDRESS= "D4:C6:12:E8:8A:12";
    private static final String TEMP_STREAM = "temp_nrf_stream";
    private MetaWearBleService.LocalBinder serviceBinder;
    private CharSequence inputString = "";
    private MultiChannelTemperature mcTempModule;
    private final int TIME_DELAY_PERIOD = 60000;
    private Bme280Humidity humidityModule = null;
    float humidity =0;
    static final String MAX_TEMP = "text_maxt";
    static final String MIN_TEMP = "text_mint";
    static final String MAX_HUMIDITY = "text_maxh";
    static final String MIN_HUMIDITY = "text_minh";
    static final String BOOL_MAXT = "checkbox_maxt";
    static final String BOOL_MINT = "checkbox_mint";
    static final String BOOL_MAXH = "checkbox_maxH";
    static final String BOOL_MINH = "checkbox_minH";
    static final String TEMP_CONVERSION = "C_or_F";
    Settings settingModule;
    int varBattery=0;


    private RouteManager.MessageHandler loggingMessageHandler = new RouteManager.MessageHandler()
    {
        @Override
        public void process(Message message)
        {
          //  Log.i("MainActivity", String.format("Inside the message handler!!!"));

           Log.i("MainActivity", String.format("Ext thermistor: %.3fC", message.getData(Float.class)));
            if(mwBoard != null) {
                getHumidity();
                getBattery();
                final int conversation = ThresholdActivity.getThreshold(TEMP_CONVERSION);
                Log.i("MainActivity", String.format("conversation is %d", conversation));
                final float tempVar = message.getData(Float.class);  //.intValue();
                final float tempVarF =(message.getData(Float.class) * 18)/10 +32; //the required final made me do this the stupid way

                try {
                    Time now = new Time();
                    now.setToNow();
                    String output = now.format("%d.%m.%Y %H.%M.%S") + " TEMP::" + tempVarF + " F::  Humidity::" + humidity + "%";
                    Log.i("MainActivity", String.format("Output is now: %s", output));

   /*                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("Aristaios.txt", Context.MODE_APPEND));
                    outputStreamWriter.write(output);
                    outputStreamWriter.close();
   */
                } catch (Exception e) {
                    Log.e("MainActivity", "File write failed:" + e.toString() );
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                  //      Log.i("MainActivity", String.format("Variables should change"));
                        TextView tempView = (TextView) findViewById(R.id.title_temp);
                        if(conversation==1)
                        {
                            tempView.setText(tempVar + " C");
                            checkTemp(tempVar);
                        }
                        else{
                            tempView.setText(tempVarF + " F");
                            Log.i("MainActivity", String.format("Ext thermistor: %.3fF", tempVarF));
                            checkTemp(tempVarF);
                        }
                        checkHumidity();
                        tempView = (TextView) findViewById(R.id.title_humidity);
                        String rval = String.format("%.3f", humidity);
                        tempView.setText(rval + "%");
                        tempView = (TextView) findViewById(R.id.power);
                        tempView.setText("Sensor Power: " + varBattery +"%");
                    }
                });
            }
            else{
                Log.i("MainActivity", String.format("mwBoard is null and process will work"));
            }

        }
    };

    //if the temp values are not between the max and min values then send a pop up.
    public void checkTemp(float temp)
    {
     //   Log.i("MainActivity", String.format("inside checkTemp"));
        if(ThresholdActivity.getThreshold(BOOL_MAXT)==1)
        {
       //     Log.i("MainActivity", String.format("Inside BOOL_MAXT"));  //::%d::%s::", ThresholdActivity.getThreshold(MAX_TEMP),Integer.parseInt(tempView.getText().toString() )));
            if(ThresholdActivity.getThreshold(MAX_TEMP) < temp)
            {
       //         Log.i("MainActivity", String.format("alertbox attempt"));
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Temp out of Range")
                        .setMessage("The Sensor temp excceds Max Threshold")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        }
        if(ThresholdActivity.getThreshold(BOOL_MINT)==1)
        {
         //   Log.i("MainActivity", String.format("Inside BOOL_MINT"));
            if(ThresholdActivity.getThreshold(MIN_TEMP) >  temp)
            {
          //      Log.i("MainActivity", String.format("Inside MINT getThreshold"));
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Temp out of Range")
                    .setMessage("The Sensor temp excceds Min Threshold")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        }
    }
    //if the humidity is not between the max and min in settings send a pop up box.
    public void checkHumidity()
    {
   //     Log.i("MainActivity", String.format("inside checkhumidity"));
        if(ThresholdActivity.getThreshold(BOOL_MAXH)==1)
        {
     //       Log.i("MainActivity", String.format("Inside BOOL_MAXH"));  //::%d::%s::", ThresholdActivity.getThreshold(MAX_TEMP),Integer.parseInt(tempView.getText().toString() )));
            if(ThresholdActivity.getThreshold(MAX_HUMIDITY) < humidity )
            {
       //         Log.i("MainActivity", String.format("alertbox attempt"));
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Humidity out of Range")
                        .setMessage("The Sensor humidity excceds Max Threshold")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        }
        if(ThresholdActivity.getThreshold(BOOL_MINH)==1)
        {
       //     Log.i("MainActivity", String.format("Inside BOOL_MINT"));
            if(ThresholdActivity.getThreshold(MIN_HUMIDITY) >  humidity)
            {
         //       Log.i("MainActivity", String.format("Inside MINH getThreshold"));
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Humidity out of Range")
                        .setMessage("The Sensor humidity excceds Min Threshold")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        }
    }

/*
Code below was 90% derived from temperatureTracker.java file from mbientlab labs git hub repository for TemperatureTrackerAndroid application
 */
    private final AsyncOperation.CompletionHandler<RouteManager> temperatureHandler = new AsyncOperation.CompletionHandler<RouteManager>()
    {
       @Override
        public void success(RouteManager result)
       {
          // result.setLogMessageHandler("mystream", loggingMessageHandler);
           result.subscribe(TEMP_STREAM, loggingMessageHandler);
      //     Log.e("MyActivity", String.format("AsyncOperation temperature :: success "));
           try
           {
               AsyncOperation<Timer.Controller> taskResult = mwBoard.getModule(Timer.class).scheduleTask(new Timer.Task()
               {
                   @Override
                    public void commands()
                   {
                       Log.e("MyActivity", String.format("AsyncOperation::commands"));
                       mcTempModule.readTemperature(mcTempModule.getSources().get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE));
                   }
               }, TIME_DELAY_PERIOD, false);
               taskResult.onComplete(new AsyncOperation.CompletionHandler<Timer.Controller>()
               {
                   @Override
                    public void success(Timer.Controller result)
                   {
                       Log.e("MyActivity", String.format("taskResult :: success"));
                       result.start();
                   }
               });
           } catch (UnsupportedModuleException e)
           {
               Log.e("Temperature Fragment", e.toString());
           }
       }
        @Override
        public void failure(Throwable error)
        {
            Log.e("AsyncResult", "Error in CompletionHandler", error);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class),
                this, Context.BIND_AUTO_CREATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unbind the service when the activity is destroyed
        getApplicationContext().unbindService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // Typecast the binder to the serString.format("Did we capture it right?::%.3fc::", temp));vice's LocalBinder class
        serviceBinder = (MetaWearBleService.LocalBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) { }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId())
        {
            case R.id.action_settings:
                disconnectBoard();  //helps reconnect to the sensor when you come back to main
                Intent intent = new Intent(this, ThresholdActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_connect:
                connectBoard();
                return true;
            case R.id.action_disconnect:
                disconnectBoard();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }
//taken from https://mbientlab.com/androiddocs/latestmetawearboard.html
    public void retrieveBoard() {
        final BluetoothManager btManager=
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice=
                btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        // Create a MetaWear board object for the Bluetooth Device
        mwBoard=  serviceBinder.getMetaWearBoard(remoteDevice);
    }
//parts of this was taken from https://mbientlab.com/androiddocs/latest/metawearboard.html#connection-state
    private final MetaWearBoard.ConnectionStateHandler stateHandler= new MetaWearBoard.ConnectionStateHandler() {
        @Override
        public void connected()
        {
            Log.i("MainActivity", "Connected");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tempView = (TextView) findViewById(R.id.title_temp);
                    tempView.setText("Connecting");
                    tempView = (TextView) findViewById(R.id.title_humidity);
                    tempView.setText(" ");
                }
            });
            getHumidity();
            getTemp();

        }

        @Override
        public void disconnected()
        {
            Log.i("MainActivity", "Connected Lost");
        //    humidity = -1;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tempView = (TextView) findViewById(R.id.title_temp);
                    tempView.setText("Disconnected");
                    tempView = (TextView) findViewById(R.id.title_humidity);
                    tempView.setText(" ");
                }
            });
        } //disconnected

        @Override
        public void failure(int status, Throwable error) {
            Log.e("MainActivity", "Error connecting", error);
        }
    };
        /*
        Could not call the other functions that should take palce after connected due to threads and race conditions.  Had to move to metawear connected
        function above.  Also caused issue with Toast statements in the functions above
         */
    //http://mbientlab.com/androidddocs/latest
    public void connectBoard() {
     //if already connected, disconnect and reconnect
        if (mwBoard == null)
        {
            retrieveBoard();
            //      Log.e("MainActivity", stateHandler.toString());
            mwBoard.setConnectionStateHandler(stateHandler);
            mwBoard.connect();
         }

    }
    //http://mbientlab.com/androidddocs/latest
    public void disconnectBoard()
    {
        if(mwBoard !=null)
        {
            mwBoard.disconnect();
            mwBoard=null;
        }

    }
// read in the temp
    public void getTemp()
    {
       try{
            mcTempModule = mwBoard.getModule(MultiChannelTemperature.class);
        } catch (UnsupportedModuleException e){
            Log.e("Stupid = TempModule -1", e.toString());
            Log.i("MainActivity", String.format("2nd attempt at try alert -1"));
            return;
        }

        if(mcTempModule == null)
        {
            Log.e("MyActivity", String.format("Stupid null TempModule -2"));
            return;
        }

       final List<MultiChannelTemperature.Source> tempSources = mcTempModule.getSources();
   // test
        MultiChannelTemperature.Source tempSource = tempSources.get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE);
    //    Log.e("MyActivity", String.format("Before the mcTempModule.routeData"));
        mcTempModule.routeData().fromSource(tempSource).stream("temp_nrf_stream").commit().onComplete(temperatureHandler);  //log("log_stream").commit().onComplete(temperatureHandler);
   // end of test
        if(tempSources == null)
        {
            Log.e("MyActivity", String.format("Stupid null source -3"));
            return;
        }

    }
//read in the humidity values
    public void getHumidity()
    {
        try{
            humidityModule= mwBoard.getModule(Bme280Humidity.class);
        } catch (UnsupportedModuleException e){
            Log.e("Stupid = HumMod -1", e.toString());
            Log.i("MainActivity", String.format("2nd attempt at humidity alert -1"));
            return;
        }
        if(humidityModule == null)
        {
            Log.e("MyActivity", String.format("Stupid null Humidity Module -2"));
            return;
        }

        //single call instance
     humidityModule.routeData().fromSensor(false).stream("humidity").commit().onComplete(new AsyncOperation.CompletionHandler<RouteManager>()
        {
            @Override
                public void success(RouteManager result)
                {
                    result.subscribe("humidity", new RouteManager.MessageHandler()
                    {
                        @Override
                            public void process(Message msg)
                            {
                                Log.i("MainActivity", "Humidity percent: " + msg.getData(Float.class));
                                humidity = msg.getData(Float.class);
                            }
                    });
                    humidityModule.readHumidity(false);
                }
        });

    }
//get the battery charge left
    public void getBattery()
    {
        try{
            settingModule = mwBoard.getModule(Settings.class);
        } catch (UnsupportedModuleException e){
            Log.i("MainActivity", String.format("getBattery unsupported module"));
            return;
        }

        settingModule.routeData().fromBattery().stream("battery_state").commit().onComplete(new AsyncOperation.CompletionHandler<RouteManager>()
        {
            @Override
            public void success(RouteManager result)
            {
                result.subscribe("battery_state", new RouteManager.MessageHandler() {
                    @Override
                    public void process(Message message) {
                        Log.i("MainActivity", "Battery state: " + message.getData(BatteryState.class).charge());
                        varBattery = message.getData(BatteryState.class).charge();
                    }
                });
            }
        });
        settingModule.readBatteryState();

    }

}
