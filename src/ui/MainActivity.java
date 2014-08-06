package ui;
 
 
import com.example.hivetopc.R;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
                
public class MainActivity extends Activity  {
          
	private NotificationManager mNotifyManager;

	@Override  
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String syncConnPref = sharedPref.getString("hive", "");
		
		
		// Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
		
        Intent dismissIntent = new Intent(this, MainActivity.class);
        dismissIntent.setAction("stop");
        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);
        
        // Constructs the Builder object.
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("sdfdsfds")
                .setContentText("rrrrrrrrr")
                .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                /*
                 * Sets the big view "big text" style and supplies the
                 * text (the user's reminder message) that will be displayed
                 * in the detail area of the expanded notification.
                 * These calls are ignored by the support library for
                 * pre-4.1 devices.
                 */
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("454353454"))
                .addAction (R.drawable.ic_launcher,"Stop Monitoring", piDismiss);
          
        
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        Notification n = builder.build();
          
        mNotifyManager.notify(11, n);
              
                
         
      
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, final String number) {
                  String currentPhoneState = null;
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                    	System.err.println("444444444444444444444444444444");
                        currentPhoneState = "Device is ringing. Call from " + number ;
                        runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								  publishEvent("Device is ringing. Call from " + number );
								
							}
						});
                       
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        currentPhoneState = "Device call state is currently Off Hook";
                        publishEvent(currentPhoneState);
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        currentPhoneState = "Device call state is currently Idle";
                        publishEvent(currentPhoneState);
                        break;  
                }
               
            }
        };  
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        
        publishEvent("Hi... i'm here."); 
        
	}

	
	public void  publishEvent(String event) {
		System.err.println("44455555555555555555555555555555555555");
		Pubnub pubnub = new Pubnub("demo", "demo");
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String imgSett = prefs.getString("channel", "1");
		
		
		 pubnub.publish(imgSett, event	, new Callback() {
  
			@Override
			public void connectCallback(String arg0, Object arg1) {
				System.err.println("publish to: "+arg1.toString());
				super.connectCallback(arg0, arg1);
			}

			@Override
			public void successCallback(String arg0, Object arg1, String arg2) {
				System.err.println("publish to: "+arg0);
				super.successCallback(arg0, arg1, arg2);
			}
		});
		
	}
 

}
