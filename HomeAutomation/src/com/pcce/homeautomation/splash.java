package com.pcce.homeautomation;


import java.io.IOException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class splash extends Activity{


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
        
        
		
		Thread timer=new Thread(){
			
			public void run(){
				
				try{
					sleep(1000);
				}catch(InterruptedException e){
					e.printStackTrace();
					
				}finally{
					Intent sread=new Intent("com.pcce.homeautomation.DEVICELISTACTIVITY");
					startActivity(sread);
				}
			}
		};
		timer.start();
	}
	@Override
    protected void onPause() {
        super.onPause();
        finish();
    }
	        	
}
