package ui;

import utils.Utils;
import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.view.WindowManager;

public class HiveToPcActivity extends Activity {

   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
  
		
		 

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
	 

		Utils.publishEvent("Hi... i'm here.",false);

	}

	@Override
	protected void onResume() {
		
	 
		
		super.onResume();
	}
	
	

	 

}
