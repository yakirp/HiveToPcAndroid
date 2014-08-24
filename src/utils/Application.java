package utils;

import android.content.Context;

public class Application extends android.app.Application {

	

private static Context appContext;
    
 
	@Override
	public void onCreate() {
	 
	    setContext(this.getApplicationContext());
		super.onCreate();
	}

	public static Context getContext() {
		return appContext;
	}

	public static void setContext(Context context) {
		appContext = context;
	}

	
	
}
