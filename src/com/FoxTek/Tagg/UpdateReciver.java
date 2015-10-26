package com.FoxTek.Tagg;

import java.io.FileNotFoundException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ModdingFox on 10/30/13.
 */
public class UpdateReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService((new Intent(context.getApplicationContext(), TaggService.class)));
        
    	fileiostuff file_io_to_use = new fileiostuff();
    	settings_obj settings_to_use = new settings_obj();

    	file_io_to_use.Load_Settings(settings_to_use, context.getApplicationContext());
    	
    	if(settings_to_use.Auto_Start)
    	{  
        context.startService((new Intent(context.getApplicationContext(), TaggService.class)));
    	}
    }
}
