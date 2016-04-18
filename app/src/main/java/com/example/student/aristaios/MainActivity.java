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

import org.w3c.dom.Text;

import java.util.List;
import java.util.TimerTask;
//import java.util.Timer;
import android.os.Handler;

import javax.xml.transform.Source;


public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private MetaWearBoard mwBoard = null;
    private final String MW_MAC_ADDRESS= "D4:C6:12:E8:8A:12";
    private static final String TEMP_STREAM = "temp_nrf_stream";
    private static final String HUMIDITY_STREAM ="humidity";
    private MetaWearBleService.LocalBinder serviceBinder;
//    private Context baseContext = null;
    private CharSequence inputString = "";
    private MultiChannelTemperature mcTempModule;
    private final int TIME_DELAY_PERIOD = 60000;
    private Bme280Humidity humidityModule = null;
    float temp=0;
    float humidity =0;
    String tempStr;
    String humidityStr;

    private final RouteManager.MessageHandler loggingMessageHandler = new RouteManager.MessageHandler()
    {
        @Override
        public void process(Message message)
        {
            Log.i("MainActivity", String.format("Inside the message handler!!!"));
            Log.i("MainActivity", String.format("Ext thermistor: %.3fC", message.getData(Float.class)));
            getHumidity();
            final float tempVar = message.getData(Float.class);  //.intValue();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tempView = (TextView) findViewById(R.id.title_temp);
                    tempView.setText(tempVar + "C");
                    tempView = (TextView) findViewById(R.id.title_humidity);
                    tempView.setText(humidity + "%");
                }
            });

 /*           if( ((CheckBox) findViewById(R.id.checkbox_maxt)).isChecked() )
            {
                TextView tempView = (TextView) findViewById(R.id.text_maxt);
                final int maxT = Integer.parseInt(tempView.getText().toString());
                if(tempVar > maxT)
                {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Temp out of Range")
                            .setMessage("The Sensor temp excceds Max Threshold")
                            .setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.cancel();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert).show();
                }
            }
            if( ((CheckBox) findViewById(R.id.checkbox_mint)).isChecked() )
            {

            }
           */
        }
    };
   /*
    private final RouteManager.MessageHandler humidityMessageHandler = new RouteManager.MessageHandler()
    {
        @Override
        public void process(Message message) {
            Log.i("MainActivity", String.format("humidity message hanndler "));
            Log.i("MainActivity", "Humidity percent: " + message.getData(Float.class));
        }
    };
*/
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
           Log.e("MyActivity", String.format("AsyncOperation temperature :: success "));
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
//humidityHandler
    /*
    private final AsyncOperation.CompletionHandler<RouteManager> humidityHandler = new AsyncOperation.CompletionHandler<RouteManager>()
    {
        @Override
        public void success(RouteManager result)
        {
            Log.e("MyActivity", String.format("AsyncOperation humidity :: success "));
            result.subscribe(HUMIDITY_STREAM, humidityMessageHandler);
            try
            {
                Log.e("MyActivity", String.format("inside the try "));
                AsyncOperation<Timer.Controller> taskResult = mwBoard.getModule(Timer.class).scheduleTask(new Timer.Task()
                {
                    @Override
                    public void commands() {
                        Log.e("MyActivity", String.format("AsyncOperation humidity ::commands"));
                        humidityModule.readHumidity(false);
                    }
                }, TIME_DELAY_PERIOD, false);
                if(taskResult == null)
                {
                    Log.e("MyActivity", String.format("taskResult in humidity is null"));
                }
                taskResult.onComplete(new AsyncOperation.CompletionHandler<Timer.Controller>()
                {
                    @Override
                    public void success(Timer.Controller result)
                    {
                        Log.e("MyActivity", String.format("taskResult humidity:: success"));
                        result.start();
                    }
                });
            } catch (UnsupportedModuleException e)
            {
                Log.e("humidity Fragment", e.toString());
            }

        }
        @Override
        public void failure(Throwable error)
        {
            Log.e("AsyncResult", "Error in CompletionHandler", error);
        }
    };
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class),
                this, Context.BIND_AUTO_CREATE);
  //      baseContext = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bind the service when the activity is created
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

    public void retrieveBoard() {
        final BluetoothManager btManager=
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice=
                btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        // Create a MetaWear board object for the Bluetooth Device
        mwBoard=  serviceBinder.getMetaWearBoard(remoteDevice);
    }

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
                    tempView.setText("Connecting");
                }
            });
            getTemp();
   //         getHumidity();
        }

        @Override
        public void disconnected()
        {
            Log.i("MainActivity", "Connected Lost");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tempView = (TextView) findViewById(R.id.title_temp);
                    tempView.setText("Disconnected");
                    tempView = (TextView) findViewById(R.id.title_humidity);
                    tempView.setText("Disconnected");
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
        Log.e("MyActivity", String.format("Before the mcTempModule.routeData"));
        mcTempModule.routeData().fromSource(tempSource).stream("temp_nrf_stream").commit().onComplete(temperatureHandler);  //log("log_stream").commit().onComplete(temperatureHandler);
   // end of test
        if(tempSources == null)
        {
            Log.e("MyActivity", String.format("Stupid null source -3"));
            return;
        }

   // create handler function to do update and then call it in here.  Single call instance
  /*      mcTempModule.routeData().fromSource(tempSources.get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE)).stream("temp_nrf_stream").commit().onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {
            @Override
            public void success(RouteManager result)
            {
                result.subscribe("temp_nrf_stream", new RouteManager.MessageHandler() {
                    @Override
                    public void process(Message message) {
                        Log.i("MainActivity", String.format("Ext thermistor: %.3fC", message.getData(Float.class)));
                        temp = message.getData(Float.class);
                        //             tempStr = getString(R.string.temp_string);
                        //                       Log.i("MainActivity", tempStr);

                    }
                });
                mcTempModule.readTemperature(tempSources.get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE));
             //   handler.sendEmptyMessage(0);
            }
        });
  */

    }

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

  //      humidityModule.routeData().fromSensor(false).stream("humidity").commit().onComplete(humidityHandler);
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

}
