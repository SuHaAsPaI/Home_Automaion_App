package com.pcce.homeautomation;
import java.util.Set;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
  
public class DeviceListActivity extends Activity {
  
    // textview for connection status
    TextView textConnectionStatus;
    ListView pairedListView;
    
    String s;
    //An EXTRA to take the device MAC to the next activity
    public static String EXTRA_DEVICE_ADDRESS;
  
    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
  
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         
        textConnectionStatus = (TextView) findViewById(R.id.connecting);
        textConnectionStatus.setTextSize(40);
         
        // Initialize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
         
        // Find and set up the ListView for paired devices
        pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setOnItemClickListener(mDeviceClickListener);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
         
    }
    
    @Override
    public void onResume()
    {
      super.onResume();
      //It is best to check BT status at onResume in case something has changed while app was paused etc
      checkBTState();
       
      mPairedDevicesArrayAdapter.clear();// clears the array so items aren't duplicated when resuming from onPause
 
      textConnectionStatus.setText(" "); //makes the textview blank
       
      // Get the local Bluetooth adapter
      mBtAdapter = BluetoothAdapter.getDefaultAdapter();
  
      // Get a set of currently paired devices and append to pairedDevices list
      Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
      
      // Add previously paired devices to the array
      if (pairedDevices.size() > 0) {
          findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);//make title viewable
          for (BluetoothDevice device : pairedDevices) {
              mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
          }
      } else {
          mPairedDevicesArrayAdapter.add("no devices paired");
      }
  }
  
    //method to check if the device has Bluetooth and if it is on.
    //Prompts the user to turn it on if it is off
    private void checkBTState()
    {
        // Check device has Bluetooth and that it is turned on
                mBtAdapter=BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if(mBtAdapter==null) {
               Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
               finish();
        } else {
          if (!mBtAdapter.isEnabled()) {
            //Prompt user to turn on Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            }
          else{
        	  View v = null;
			ClosingConnection(v);
          }
          }
        }
     
    // Set up on-click listener for the listview
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
        {
               textConnectionStatus.setText("Connecting...");
               
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            s=info;
  
            // Make an intent to start next activity while taking an extra which is the MAC address.
            Intent i = new Intent(DeviceListActivity.this, Main_code.class);
            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
            ConnectionEstablished(v);
            startActivity(i);
                                             
        }
    };
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") public void ConnectionEstablished(View view) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
    	
        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
            .setContentTitle("Connection Established")
            .setContentText(s).setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

      }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") public void ClosingConnection(View view) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
            .setContentTitle("Connection closed")
            .setContentText("No Device").setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

      }
    
  
}