package com.pcce.homeautomation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;
 
public class Main_code extends Activity {
 
 Button bSet_Timer, bEnergy;
  ToggleButton tg1,tg2,tgfan;
  SeekBar sfan;
  EditText emin;
  TextView tfnaspeed, txtString, txtStringLength;
  Handler bluetoothIn;
  int progress=0;
  final int handlerState = 0;                        //used to identify handler message
  private BluetoothAdapter btAdapter = null;
  private BluetoothSocket btSocket = null;
  private StringBuilder recDataString = new StringBuilder();
 
  private ConnectedThread mConnectedThread;

  // SPP UUID service - this should work for most devices
  private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
 
  // String for MAC address
  private static String address;
 
@SuppressLint("HandlerLeak") @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.main_code1);
    //ConnectionEstablished(v);
    //Link the buttons and textViews to respective views
    txtString = (TextView) findViewById(R.id.txtString);
    bSet_Timer=(Button)findViewById(R.id.bsettimer);
    bEnergy=(Button)findViewById(R.id.benergy);
    emin=(EditText)findViewById(R.id.editText1);
    tg1=(ToggleButton)findViewById(R.id.toggle1);
    tg2=(ToggleButton)findViewById(R.id.toggle2);
    tgfan=(ToggleButton)findViewById(R.id.togglefan);
    sfan=(SeekBar)findViewById(R.id.seekBar1);
    tfnaspeed=(TextView)findViewById(R.id.tfanspeed);
   // txtStringLength = (TextView) findViewById(R.id.testView1);
 
    bluetoothIn = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == handlerState) {                                     //if message is what we want
                String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                recDataString.append(readMessage);                                      //keep appending to string until ~
                int endOfLineIndex = recDataString.indexOf(".");                    // determine the end-of-line
                if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                    txtString.setText("HUB:" + dataInPrint);
                   // int dataLength = dataInPrint.length();                          //get length of data received
                 //   txtStringLength.setText("String Length = " + String.valueOf(dataLength));
                    recDataString.delete(0, recDataString.length());                    //clear all string data

                    dataInPrint = " ";
                }
            }
        }
    };
 
    btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
    checkBTState(); 
 
  // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
   bSet_Timer.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
          String sk=emin.getText().toString();
    	  mConnectedThread.write(sk);    // Send "0" via Bluetooth
       // Toast.makeText(getBaseContext(), "LED 1", Toast.LENGTH_SHORT).show();
    	
      }
    });
    bEnergy.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
      	  mConnectedThread.write("e");    // Send "0" via Bluetooth
         // Toast.makeText(getBaseContext(), "LED 1", Toast.LENGTH_SHORT).show();
      	
        }
      });
    tg1.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(tg1.isChecked()){
				 mConnectedThread.write("y");
			}
			else{
				 mConnectedThread.write("Y");
			}
			
		}
	});
 tg2.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(tg2.isChecked()){
				 mConnectedThread.write("z");
			}
			else{
				 mConnectedThread.write("Z");
			}
			
		}
	});
 	tgfan.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(tgfan.isChecked()){
				 mConnectedThread.write("f");
			}
			else{
				 mConnectedThread.write("F");
			}
			
		}
 		});
 	sfan.setMax(255);
 	
 	sfan.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		
		
		
        
		public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
			progress = progresValue;
			String sn="w"+Integer.toString(progress);
			tfnaspeed.setText("Fan Speed:"+Integer.toString(progress));
			//mConnectedThread.write(sn);
			
			if((progress>=0)&&(progress<51)){
				mConnectedThread.write("a");
			}
			else if((progress>=51)&&(progress<102)){
				mConnectedThread.write("b");
			}
			else if((progress>=102)&&(progress<153)){
				mConnectedThread.write("c");
			}
			else if((progress>=153)&&(progress<204)){
				mConnectedThread.write("d");
			}
			else if((progress>=204)&&(progress<255)){
				mConnectedThread.write("g");
			}
				 
			}
				         
		    @Override
			 public void onStartTrackingTouch(SeekBar seekBar) {
			}
				         
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			 }
				
		});	
 
    
  }
 
  private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
 
      return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
      //creates secure outgoing connecetion with BT device using UUID
  }
 
  @Override
  public void onResume() {
    super.onResume();
 
    //Get MAC address from DeviceListActivity via intent
    Intent intent = getIntent();
 
    //Get the MAC address from the DeviceListActivty via EXTRA
    address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
 
    //create device and set the MAC address
    BluetoothDevice device = btAdapter.getRemoteDevice(address);
    
    String tag = null;
	Log.d(tag, (device.getName().toString()));
 
    try {
        btSocket = createBluetoothSocket(device);
    } catch (IOException e) {
        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
    }
    // Establish the Bluetooth socket connection.
    try
    {
      btSocket.connect();
    } catch (IOException e) {
      try
      {
        btSocket.close();
      } catch (IOException e2)
      {
        //insert code to deal with this
      }
    }
    mConnectedThread = new ConnectedThread(btSocket);
    mConnectedThread.start();
 
    //I send a character when resuming.beginning transmission to check device is connected
    //If it is not an exception will be thrown in the write method and finish() will be called
    mConnectedThread.write("x");
  }
 
  @Override
  public void onPause()
  {
    super.onPause();
    try
    {
    //Don't leave Bluetooth sockets open when leaving activity
      btSocket.close();
    } catch (IOException e2) {
        //insert code to deal with this
    }
  }
 
 //Checks that the Android device Bluetooth is available and prompts to be turned on if off
  private void checkBTState() {
	  
    if(btAdapter==null) {
        Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
    } else {
      if (btAdapter.isEnabled()) {
      } else {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1);
      }
    }
  }
 
  //create new class for connect thread
  private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
 
        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
 
            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
 
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
 
        public void run() {
            byte[] buffer = new byte[256];
            int bytes; 
 
            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();
 
              }
            }
        }
  
  
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.main, menu);

      return super.onCreateOptionsMenu(menu);
  }

@Override
public boolean onOptionsItemSelected(MenuItem item) {
  // Take appropriate action for each action item click
  switch (item.getItemId()) {
  case R.id.action_settings:
  	Intent sread1=new Intent("com.pcce.homeautomation.MAINACTIVITY");
		startActivity(sread1);
      return true;
  default:
      return super.onOptionsItemSelected(item);
  }
}


}