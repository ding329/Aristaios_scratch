package com.example.student.aristaios;

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
import android.widget.Toast;
import android.content.*;

import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.module.MultiChannelTemperature;
import com.mbientlab.metawear.module.Timer;

import org.w3c.dom.Text;

import java.util.List;


public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private MetaWearBoard mwBoard = null;
    private final String MW_MAC_ADDRESS= "D4:C6:12:E8:8A:12";
    private MetaWearBleService.LocalBinder serviceBinder;
//    private Context baseContext = null;
    private CharSequence inputString = "";
    MultiChannelTemperature mcTempModule;
    private final int TIME_DELAY_PERIOD = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class),
                this, Context.BIND_AUTO_CREATE);
  //      baseContext = getApplicationContext();

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

     //   getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // Typecast the binder to the service's LocalBinder class
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

/*        int id = item.getItemId();
mport static com.mbientlab.metawear.MetaWearBoard.ConnectionStateHandler;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
*/
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
          //  Toast toast = Toast.makeText(baseContext, "Metawear connected", Toast.LENGTH_SHORT);
           // toast.show();
            inputString = "Metawear connected";
        }

        @Override
        public void disconnected()
        {
            Log.i("MainActivity", "Connected Lost");
           // Toast toast = Toast.makeText(baseContext, "Metawear Dis-connected", Toast.LENGTH_SHORT);
           // toast.show();
            inputString = "Metawear Disconnected";
        }

        @Override
        public void failure(int status, Throwable error) {
            Log.e("MainActivity", "Error connecting", error);
        }
    };

    public void connectBoard() {
     //if already connected, disconnect and reconnect
        if (mwBoard == null)
        {
            retrieveBoard();
            //      Log.e("MainActivity", stateHandler.toString());
            mwBoard.setConnectionStateHandler(stateHandler);
            mwBoard.connect();
            getTemp();
        }
        else
        {
            inputString = "Metawear Already connected";
        }
            Toast toast = Toast.makeText(getApplicationContext(), inputString, Toast.LENGTH_SHORT);
            toast.show();

    }
    public void disconnectBoard()
    {
        if(mwBoard !=null)
        {
            mwBoard.disconnect();
            mwBoard=null;
        }
        else {
            inputString = "Metawear device was not connected";
        }
            Toast toast = Toast.makeText(getApplicationContext(), inputString, Toast.LENGTH_SHORT);
            toast.show();
    }
    /*
    code taken from mbientlab-project TemperatureTrackerAndroid - temperatureTracker
     */
    public void getTemp()
    {
        try{
            mcTempModule= mwBoard.getModule(MultiChannelTemperature.class);
        } catch (UnsupportedModuleException e)
        {
            Log.e("Thermistor Fragment", e.toString());
          //  return false;
        }

        List<MultiChannelTemperature.Source> tempSources= mcTempModule.getSources();
        MultiChannelTemperature.Source tempSource = tempSources.get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE);
  //      mcTempModule.routeData().fromSource(tempSource).log("log_stream").commit().onComplete(temperatureHandler);
        //from the docs and not the mbientlab git
        mcTempModule.routeData().fromSource(tempSource).log("log_stream").commit().onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {
            @Override
            public void success(RouteManager result) {
                //super.success(result);
        /*        public void process(){
                //does not like msg because it was not defined.  Tried to add Message msg as variable within process, like they did in other examples, but still did not work0
                    Log.i("MainActivity", String.format("Ext thermistor: %.3fC", msg.getData(Float.class)));
                }
        */
            }
        });
        mcTempModule.readTemperature(tempSources.get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE), false);

    }
    /*
       code taken from mbientlab-project TemperatureTrackerAndroid - temperatureTracker
     */
 /*   private final AsyncOperation.CompletionHandler<RouteManager> temperatureHandler = new AsyncOperation.CompletionHandler<RouteManager>()
        @Override
        public void success(RouteManager result){
            try{
                AsyncOperation<Timer.Controller> taskResult = mwBoard.getModule(Timer.class).scheduleTask(new Timer.Task() {
                    @Override
                    public void commands(){
                        mcTempModule.readTemperature(mcTempModule.getSources().get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE));
                    }
                }, TIME_DELAY_PERIOD, false);
              taskResult.onComplete(new AsyncOperation.CompletionHandler<Timer.Controller>(){
                    @Override
                    public void success(Timer.Controller result){
                        result.start();
                    }

              });
            }catch (UnsupportedModuleException e){
                Log.e("Temperature Fragment", e.toString());
            }
        }
   */
}
