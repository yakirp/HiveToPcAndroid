package services;

import java.util.List;

import utils.Application;
import utils.Utils;

import com.hivetopc.R;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class HiveAccessibilityService extends AccessibilityService {

	private final AccessibilityServiceInfo info = new AccessibilityServiceInfo();
	private static final String TAG = "HiveAccessibilityService";
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (android.os.Build.VERSION.SDK_INT > 19) {
    		return;
    	}
		final int eventType = event.getEventType();
		if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
			final String sourcePackageName = (String)event.getPackageName();
			Parcelable parcelable = event.getParcelableData();
			
			if (parcelable instanceof Notification) {
				//Status bar Notification
				Notification notification = (Notification) parcelable; 
                Log.e(TAG, "Notification -> notification.tickerText :: " + notification.tickerText);
                
                List<CharSequence> messages = event.getText();
                if (messages.size() > 0) {
                	final String notificationMessage = (String) messages.get(0);
                	Log.v(TAG, "Captured notification message [" + notificationMessage + "] for source [" + sourcePackageName + "]");                   
                    
                    if (Utils.isUserRequestForNotifications()) {
                		if (sourcePackageName.equals("android"))
                			return;
                    	String applicationName = Utils.getApplicationNameForPackageName(sourcePackageName);
                    	Log.d("Hive", applicationName + " notification");
                    	if (notificationMessage != null) {
                    		Log.d("Hive", notificationMessage);
                    		if (Utils.isUserRequestForNotificationData()) {
                    			Utils.publishStatusBarNotification(applicationName, notificationMessage);
                    		} else {
                    			Utils.publishStatusBarNotification(applicationName, "Notification");
                    		}
                    	}
                	}
                } else {
                	Log.e(TAG, "Notification Message is empty. Can not broadcast"); 
                }
			}
		}

	}

	@Override
	public void onInterrupt() {

	}
	
	@Override
	public void onServiceConnected() {
		if (android.os.Build.VERSION.SDK_INT > 19) {
    		return;
    	}
		// Set the type of events that this service wants to listen to.
		// Others won't be passed to this service
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
		
		// If you only want this service to work with specific applications, set their
        // package names here.  Otherwise, when the service is activated, it will listen
        // to events from all applications.
        //info.packageNames = new String[]
                //{"com.appone.totest.accessibility", "com.apptwo.totest.accessibility"};
		
		// Set the type of feedback your service will provide.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        } else {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        } 
        
        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated.  This service *is*
        // application-specific, so the flag isn't necessary.  If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.
 
        // info.flags = AccessibilityServiceInfo.DEFAULT;
        
        info.notificationTimeout = 100;
        
        this.setServiceInfo(info);
	}

	 /**
     * Check if Accessibility Service is enabled. 
     *  
     * @param mContext
     * @return <code>true</code> if Accessibility Service is ON, otherwise <code>false</code>
     */
    public static boolean isAccessibilitySettingsOn() {
    	
        int accessibilityEnabled = 0;
        final String service = "com.hivetopc/com.hivetopc.services.HiveAccessibilityService";
         
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    Application.getContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                            + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
 
        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
            		Application.getContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue); 
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                     
                    Log.v(TAG, "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILIY IS DISABLED***");
        }
                 
        return accessibilityFound;      
    }

}
