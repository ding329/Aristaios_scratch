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
import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.module.MultiChannelTemperature;
import com.mbientlab.metawear.module.MultiChannelTemperature.*;

import com.mbientlab.metawear.module.Timer;

import org.w3c.dom.Text;

import java.util.List;

import javax.xml.transform.Source;


public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private MetaWearBoard mwBoard = null;
    private final String MW_MAC_ADDRESS= "D4:C6:12:E8:8A:12";
    private MetaWearBleService.LocalBinder serviceBinder;
//    private Context baseContext = null;
    private CharSequence inputString = "";
    private MultiChannelTemperature mcTempModule;
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

    public void getTemp()
    {
       try{
           /*
           if the section below is not in try block, I receive an unsupported exception.
            when it is in the try block, mcTempModule is null and will triggle a null exception on the List<> below
            */
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

       java.util.List<MultiChannelTemperature.Source> tempSources = mcTempModule.getSources();

        if(tempSources == null)
        {
            Log.e("MyActivity", String.format("Stupid null source -3"));
            return;
        }

        ExtThermistor extTherm= (ExtThermistor) tempSources.get(MetaWearRChannel.EXT_THERMISTOR);
        extTherm.configure((byte) 0, (byte) 1, false);

       // mcTempModule.readTemperature(tempSources.get(MetaWearRChannel.EXT_THERMISTOR), false);

   // guess my board does not support nrf soc temperature sensor or I dont understand the error
        mcTempModule.routeData().fromSource(tempSources.get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE)).stream("temp_nrf_stream").commit().onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {
            @Override
            public void success(RouteManager result) {
                result.subscribe("temp_nrf_stream", new RouteManager.MessageHandler() {
                    @Override
                    public void process(Message message) {
                        Log.i("MainActivity", String.format("Ext thermistor: %.3fC", message.getData(Float.class)));
                    }
                });
           //     mcTempModule.readTemperature(tempSources.get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE), false);
            }
        });

    }

}
