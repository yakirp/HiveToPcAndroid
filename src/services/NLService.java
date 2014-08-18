package services;

import utils.Utils;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

@SuppressLint("NewApi")
public class NLService extends NotificationListenerService {
	
	private String TAG = this.getClass().getSimpleName();
    
    @Override
    public void onCreate() { 
        super.onCreate();    
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
	
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) { 
    	Log.d(TAG, "NLService: onNotificationPosted");
    	if (android.os.Build.VERSION.SDK_INT < 19) {
    		Log.d(TAG, "onNotificationPosted cancel because of api level");
    		return;
    	}
    	if (Utils.isUserRequestForNotifications()) {
    		Notification mNotification=sbn.getNotification();
            if (mNotification!=null){ 
            	if (sbn.getPackageName().equals("android")) 
            		return;
            	String applicationName = Utils.getApplicationNameForPackageName(sbn.getPackageName());
            	Log.d(TAG, "Notification from: " + applicationName);
            	if (mNotification.tickerText != null) {
            		Log.d(TAG, "TickerText: " + mNotification.tickerText.toString());
            		if (Utils.isUserRequestForNotificationData()) {
            			Log.d(TAG, "Publishing with notification data");
            			Utils.publishEvent(applicationName + ": " + mNotification.tickerText.toString(), true);
            		}
            		else {
            			Log.d(TAG, "Publishing without notification data");
        				Utils.publishEvent(applicationName + " notification", true);
        			}
            	} else {
            		Utils.publishEvent("Notification from: " + applicationName, true);
            	}
            }
    	}
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    	
    }
}
