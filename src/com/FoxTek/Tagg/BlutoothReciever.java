package com.FoxTek.Tagg;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BlutoothReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF)
		{
			ServersData Servers_to_use = new ServersData();
		    fileiostuff file_io_to_use = new fileiostuff();
		    
		    file_io_to_use.Load_Servers(Servers_to_use, context.getApplicationContext());
		    for(int i =0; i < Servers_to_use.get_number_of_links(); i++)
		    {
		    	Servers_to_use.Get_Pid_From_User(i);
		    	Servers_to_use.Authenticate_Player(i);
		    	Servers_to_use.Get_Game_Settings(i);
		    	
		    	if(Servers_to_use.GetLink(i).TagBackControl)
		    	{
		    		Servers_to_use.send_data(i, (byte)0x0E, null, 0);
		    	}
		    }
		}
	}

}
