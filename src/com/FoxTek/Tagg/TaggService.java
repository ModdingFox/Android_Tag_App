package com.FoxTek.Tagg;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.UUID;

/**
 * Created by ModdingFox on 10/30/13.
 */
public class TaggService extends Service {
    final BluetoothAdapter myBTAdapter = BluetoothAdapter.getDefaultAdapter(); 
    
    ServersData Servers_to_use = new ServersData();
    settings_obj settings_to_use = new settings_obj();
    fileiostuff file_io_to_use = new fileiostuff();
    BlueTooth_Com BtObject = new BlueTooth_Com();

    player_list_class Scan_Tag_list = new player_list_class();
    
    Thread BTServer = null;
    
    boolean No_Start = false;//leave the error notification
    
    AlarmManager wakeAlarm;
    BroadcastReceiver EmptyBroadCastReciver;
    PendingIntent IntentToRun;
    
    InetAddress ipstr;
    
    private PowerManager pm;// = (PowerManager) getSystemService(Context.POWER_SERVICE);
    private PowerManager.WakeLock wl;

    //Activity Methods
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FoxTek Partial Wake Lock");
		
		wl.acquire();
        
        if (myBTAdapter == null) {
            showNotification("Error", "No Bluetooth Adapter Detected", 0);
            No_Start = true;
            System.exit(0);
        }
        
			file_io_to_use.Load_Settings(settings_to_use, getApplicationContext());
			
			file_io_to_use.Load_Servers(Servers_to_use, getApplicationContext());
        
        Refresh_Servers_Info();

        BTServer = new Thread(new Runnable() {
            @Override
            public void run()
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    try {
						BtObject.BTServer();
					} catch (InterruptedException e) {
					}
                }
            }
        });
        BTServer.start();
    }

    @Override
    public void onDestroy()
    {
    	BtObject.BTClientCancel();
    	BtObject.BTServerCancel();
    	
    	if(No_Start == false)
    	{
    	mNM.cancel(Notification_Id);
    	}
    	wl.release();
        super.onDestroy();
    }

    //Inter Process Communication

    private final ITagService.Stub mBinder = new ITagService.Stub(){
        //External Interface
    	public boolean It_Status() throws RemoteException
    	{
            return IT_Status_Check(true);
    	}
    	
        public void Refresh_Servers() throws RemoteException {
        	///may need to stop bt server service iff needed
            Refresh_Servers_Info();
        }

        public List<String> Scan() throws RemoteException
        {
            return BtObject.Scan();
            //delay the next autoscan
        }

        public boolean Tag(int selected_player) throws RemoteException
        {
            return BtObject.Tag(selected_player);
        }

		@SuppressWarnings("unchecked")
		@Override
		public List<String> GetAllScan_Results() throws RemoteException {
			ArrayList<String> Results = new ArrayList<String>();
			while(BtObject.BTAutoScan_Running || BtObject.BTScan_Running){}
			
				if(Scan_Tag_list.Links.size() == 0){return null;}
				
				for(int i = 0; i < Scan_Tag_list.Links.size(); i++)
				{
					Results.add(new String(Scan_Tag_list.Links.get(i).user_name));
				}
				
				return (List<String>)Results; 
		}

		@Override
		public void Tag_Request_Send(int selected_player)
				throws RemoteException {
			Servers_to_use.send_data(Scan_Tag_list.Links.get(selected_player).server_number, (byte) 0x04, Scan_Tag_list.Links.get(selected_player).pid, 4);
			
		}
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //Service Routines
    private boolean IT_Status_Check(boolean updatenotification)
    {
        for (int i = 0; i < Servers_to_use.get_number_of_links(); i++) {
            if (Servers_to_use.GetLink(i).check_premission_for_operation(1)) {
            	if(updatenotification){showNotification("Tagg","IT :-P",0);}
                return true;
            }
        }
        if(updatenotification){showNotification("Tagg","Not IT",0);}
        return false;
    }
    
    private NotificationManager mNM;
    int Notification_Id = 1; 
    @SuppressWarnings("deprecation")
	private void showNotification(String Title, String Content, int Notify_select) {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	mNM.cancelAll();
    	
        Notification notification;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(getApplicationContext(), MainActivity.class), 0);
        
        switch(Notify_select)
        {
        case 0:
        	notification = new Notification(R.drawable.ic_launcher, "Tagg", System.currentTimeMillis());
        	notification.setLatestEventInfo(this, Title, Content, contentIntent);
        	break;
        case 1:
        	notification = new Notification(R.drawable.ic_launcher, "Tagg", System.currentTimeMillis());
        	notification.setLatestEventInfo(this, "Tagg", "AutoScan Found A Player", contentIntent);
        	if(settings_to_use.AutoScan_Sound_Enabled)
        	{
        	notification.sound = Uri.parse(settings_to_use.AutoScan_Sound);
        	}
        break;
        case 2:
        	notification = new Notification(R.drawable.ic_launcher, "You Have Been Tagged", System.currentTimeMillis());
            notification.setLatestEventInfo(this, "You Have Been Tagged", "By " + Content, contentIntent);
            if(settings_to_use.Tagged_Sound_Enabled)
            {
        	notification.sound = Uri.parse(settings_to_use.Tagged_Sound);
            }
        break;
        case 3:
        	notification = new Notification(R.drawable.ic_launcher, "Scan Detected", System.currentTimeMillis());
            notification.setLatestEventInfo(this, "Scan Detected", "", contentIntent);
            if(settings_to_use.AutoScan_Hit_Sound_Enabled)
            {
        	notification.sound = Uri.parse(settings_to_use.AutoScan_Hit_Sound);
            }
        	break;
        default:
        	notification = new Notification(R.drawable.ic_launcher, "Tagg", System.currentTimeMillis());
        	notification.setLatestEventInfo(this, "Tagg", "Invalid Notification Selected", contentIntent);
        	break;
        }

        mNM.notify(Notification_Id, notification);
    }
    
    private void Refresh_Servers_Info() {
        showNotification("Tagg","Refreshing Data...",0);

        BtObject.AutoScan_Stop();
  
        for (int i = 0; i < Servers_to_use.get_number_of_links(); i++) {
            Servers_to_use.Get_Pid_From_User(i);
            Servers_to_use.Authenticate_Player(i);
            Servers_to_use.Get_Game_Settings(i);
            
            if (Servers_to_use.GetLink(i).check_premission_for_operation(1)) {
            	Servers_to_use.Get_Player_List(i);
            }
        }  

        if(IT_Status_Check(true))
        {
        	if(settings_to_use.autoscantoggle)
        	{
        		BtObject.Auto_Scan();
        	}
        }
    }

    /*
    double Lat = 0;
    double Lon = 0;
    byte[] Data = new byte[Double.SIZE * 2];

    public void Run_GPS_Updater()
    {
        GpsUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(1000*60);
                } catch (InterruptedException e) {
                }
                LocationManager mylocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                LocationListener mylocationlistener = new MyLocationListener();
                mylocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,1,mylocationlistener);
                mylocation.removeUpdates(mylocationlistener);

                //convert Lat and Lon to byte array

                for(int i = 0; i < Servers_to_use.get_number_of_links(); i++)
                {
                    Servers_to_use.send_data(i, (byte) 0x0D, Data, Double.SIZE * 2);
                }
            }
        });
    }

    private class MyLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location) {
            while(!location.hasAccuracy())
            {
            Lat = location.getLatitude();
            Lon = location.getLongitude();
            }
            }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
    */

    private static final UUID Server_UUID = UUID.fromString("466F7854-656B-4675-7272-79344C696665");
    
    class BlueTooth_Com {
       
    	
    	private final BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        private BluetoothServerSocket mBTSvrSocket;
        private BluetoothSocket mBTSocket;
        private BluetoothDevice RBTDevice;

        private InputStream mIStream = null;
        private OutputStream mOStream = null;

        private static final String NAME = "BTTagg";
        
        Thread BTAutoScan_Thread = null;
        boolean BTAutoScan_Thread_Running = false;
        
        private int Mode = 0;
        private boolean BTServer_Running = false;//mode 0
        public boolean BTAutoScan_Running = false;//mode 1
        public boolean BTScan_Running = false;//mode 2
        private boolean BTTag_Running = false;//mode 3
        
        //Bluetooth Functions
        public String getBtMac() {
            return mBTAdapter.getAddress();
        }

        public void BTServer() throws InterruptedException {
        	while(Mode != 0 || BTAutoScan_Running || BTScan_Running || BTTag_Running){}

        	BTServer_Running = true;
        	
        	BTServerCancel();
        	BTClientCancel();

            RBTDevice = null;

            try {
                mBTSvrSocket = mBTAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, Server_UUID);
            } catch (IOException e) {
                return;
            }

            try {
                mBTSocket = mBTSvrSocket.accept();
                
                if(Mode != 0)
                {
                	BTIODestroy();
                	BTServerCancel();
                	BTClientCancel();
                	BTServer_Running = false;
                	return;
                }
            } catch (IOException e) {
            	BTServer_Running = false;
                return;
            } catch (NullPointerException e1) {
            	BTServer_Running = false;
                return;
            }

            if (mBTSocket != null) {
                if (BTIOSetup() == false) {
                	BTServer_Running = false;
                    return;
                }
            }
            else
            {
            	BTServer_Running = false;
            	return;
            }
            
            byte[] Data = recv_raw(9);
            byte[] Data_Out = new byte[5];
            
            Data_Out[0] = 0x00;
            Data_Out[1] = (byte) 0x00;
            Data_Out[2] = (byte) 0x00;
            Data_Out[3] = (byte) 0x00;
            Data_Out[4] = (byte) 0x00;

            if (Data[0] == (byte) 0x01)
            {
                for (int i = 0; i < Servers_to_use.get_number_of_links(); i++) {
                    try {
                        ipstr = InetAddress.getByName(Servers_to_use.GetLink(i).url);
                    } catch (UnknownHostException e) {
                    }
                    byte[] byteip = ipstr.getAddress();

                    if ((byte) ((byte) 0xFF & byteip[0]) == Data[5] && (byte) ((byte) 0xFF & byteip[1]) == Data[6] && (byte) ((byte) 0xFF & byteip[2]) == Data[7] && (byte) ((byte) 0xFF & byteip[3]) == Data[8]) {
                        Data_Out[0] = 0x01;
                        Data_Out[1] = Servers_to_use.GetLink(i).pid[0];
                        Data_Out[2] = Servers_to_use.GetLink(i).pid[1];
                        Data_Out[3] = Servers_to_use.GetLink(i).pid[2];
                        Data_Out[4] = Servers_to_use.GetLink(i).pid[3];
                        
                        if(Servers_to_use.GetLink(i).ScanDetectAllowed)
                        {
                        	showNotification("Scan Detected", "", 3);
                        }    
                    }
                }
                send_raw(Data_Out, 5);
            } 
            else if (Data[0] == (byte) 0x02)
            {
                for (int i = 0; i < Servers_to_use.get_number_of_links(); i++) {
                    try {
                        ipstr = InetAddress.getByName(Servers_to_use.GetLink(i).url);
                    } catch (UnknownHostException e) {
                    }
                    byte[] byteip = ipstr.getAddress();

                    if ((byte) ((byte) 0xFF & byteip[0]) == Data[5] && (byte) ((byte) 0xFF & byteip[1]) == Data[6] && (byte) ((byte) 0xFF & byteip[2]) == Data[7] && (byte) ((byte) 0xFF & byteip[3]) == Data[8]) {
                        Data_Out[0] = 0x01;
                        Data_Out[1] = Servers_to_use.GetLink(i).pid[0];
                        Data_Out[2] = Servers_to_use.GetLink(i).pid[1];
                        Data_Out[3] = Servers_to_use.GetLink(i).pid[2];
                        Data_Out[4] = Servers_to_use.GetLink(i).pid[3];
                        send_raw(Data_Out, 5);

                        byte[] taggerpid = new byte[4];
                        taggerpid[0] = Data[1];
                        taggerpid[1] = Data[2];
                        taggerpid[2] = Data[3];
                        taggerpid[3] = Data[4];

                        Servers_to_use.send_data(i, (byte) 0x05, taggerpid, 4);
                        Servers_to_use.send_data(i, (byte) 0x0C, taggerpid, 4);

                        try {
                            showNotification("", new String(Servers_to_use.GetLink(i).data_buffer, "US-ASCII"), 2);
                        } catch (UnsupportedEncodingException e) {
                        }
                    }
                }
            }
            
        	BTIODestroy();
        	BTServerCancel();
        	BTClientCancel();
            BTServer_Running = false;
            return;
        }
        
        public void Auto_Scan()
        {
        	if(BTAutoScan_Thread_Running == false)
        	{
        	BTAutoScan_Thread = new Thread(new Runnable() {
    			@Override
    			public void run() {
    				while(!Thread.currentThread().isInterrupted())
    				{
    					try {
    						Thread.currentThread().sleep((Long.parseLong(settings_to_use.scantime) * 60L) * 1000L);
    					} catch (NumberFormatException e1) {
    						BTAutoScan_Thread_Running = false;
    						break;
    					} catch (InterruptedException e1) {
    						BTAutoScan_Thread_Running = false;
    						break;
    					}
    					
    					if(Mode > 1 || BTAutoScan_Running || BTScan_Running || BTTag_Running)
    					{
    	        		break;
    					}
    	        	Mode = 1;
    	        	
    	        	while(BTServer_Running)
    	        	{
    	        		Mode = 1;
    	        		BTServerCancel();
    	        	}
    	        	
    	        	BTAutoScan_Running = true;
    	        	
    	        	showNotification("AutoScan", "Scanning For Players...",0);
    	             
    	             player_list_class Scan_Results = new player_list_class();
    	             player_item_class tempitem;

    	             byte[] Data_Out = new byte[9];
    	             byte[] Data_In;

    	             for (int i = 0; i < Servers_to_use.get_number_of_links() && Mode == 1; i++) {
    	                 if (Servers_to_use.GetLink(i).check_premission_for_operation(1) && Servers_to_use.GetLink(i).AutoScanAllowed) {

    	                     try {
    	                         ipstr = InetAddress.getByName(Servers_to_use.GetLink(i).url);
    	                     } catch (UnknownHostException e) {
    	                     }

    	                     byte[] byteip = ipstr.getAddress();

    	                     Data_Out[0] = (byte) 0x01;
    	                     Data_Out[1] = Servers_to_use.GetLink(i).pid[0];
    	                     Data_Out[2] = Servers_to_use.GetLink(i).pid[1];
    	                     Data_Out[3] = Servers_to_use.GetLink(i).pid[2];
    	                     Data_Out[4] = Servers_to_use.GetLink(i).pid[3];
    	                     Data_Out[5] = (byte) ((byte) 0xFF & byteip[0]);
    	                     Data_Out[6] = (byte) ((byte) 0xFF & byteip[1]);
    	                     Data_Out[7] = (byte) ((byte) 0xFF & byteip[2]);
    	                     Data_Out[8] = (byte) ((byte) 0xFF & byteip[3]);

    	                     for (int i1 = 0; i1 < Servers_to_use.GetLink(i).Players_List.player_count; i1++) {
    	                         if (Servers_to_use.GetLink(i).Players_List.Links.get(i1).mac_address != getBtMac()) {
    	                             
    	                        	 if(BTScan_Running || BTTag_Running || Mode != 1)
    	                        	 {
    	                        		 BTIODestroy();
    	                             	 BTServerCancel();
    	                             	 BTClientCancel();
    	                        		 BTAutoScan_Running = false;
    	                        		 break;
    	                        	 }
    	                        	 
    	                             if (BTClientStartUp(Servers_to_use.GetLink(i).Players_List.Links.get(i1).mac_address) == true) {

    	                                 send_raw(Data_Out, 9);
    	                                 Data_In = recv_raw(5);
    	                                 BTIODestroy();

    	                                 if (Data_In[0] == 0x01) {
    	                                     if (Data_In[1] == Servers_to_use.GetLink(i).Players_List.Links.get(i1).pid[0] && Data_In[2] == Servers_to_use.GetLink(i).Players_List.Links.get(i1).pid[1] && Data_In[3] == Servers_to_use.GetLink(i).Players_List.Links.get(i1).pid[2] && Data_In[4] == Servers_to_use.GetLink(i).Players_List.Links.get(i1).pid[3]) {
    	                                     	tempitem = Servers_to_use.GetLink(i).Players_List.Links.get(i1);
    	                                         tempitem.server_number = i;
    	                                         Scan_Results.Links.add(tempitem);
    	                                         
    	                                         if(Scan_Results.Links.size() == 1)
    	                                         {
    	                                         	showNotification("", "", 1);
    	                                         }
    	                                         else if(Scan_Results.Links.size() == 2)
    	                                         {
    	                                         	showNotification("Tagg", "AutoScan Found Players", 0);
    	                                         }
    	                                     }
    	                                 }
    	                                 if (Data_In[0] == 0x02) {
    	                                 }
    	                             }
    	                         }
    	                     }
    	                 }
    	             }
    	             
    	         	 BTServerCancel();
    	         	 BTClientCancel();
    	             IT_Status_Check(true);
    	             Scan_Tag_list = Scan_Results;
    	             Mode = 0;
    	             BTAutoScan_Running = false;
    				}
    				return;
    			}
    		});
        	BTAutoScan_Thread.start();
        	}
        }
        
        public void AutoScan_Stop()
        {
        	if(BTAutoScan_Thread != null)
        	{
        		if(BTAutoScan_Thread.isAlive())
        		{
        			BTAutoScan_Thread.interrupt();
                	while(BTAutoScan_Thread_Running){}
                	return;
        		}
        	}
        	else
        	{
        		BTAutoScan_Running = false;
        	}
        	return;
        }
        
        public List<String> Scan()
        {
        	if(Mode > 2 || BTScan_Running || BTTag_Running){return null;}
        	Mode  = 2;
        	
        	while(BTServer_Running || BTAutoScan_Running)
        	{
        		Mode = 2;
        		BTServerCancel();
        	}
        	
        	BTScan_Running = true;
        	showNotification("Tagg", "Scanning For Players...",0);
        	
        	 player_list_class Result_Tagg_List = new player_list_class();
        	 List<String> Result = new ArrayList<String>();
             player_item_class tempitem;

             byte[] Data_Out = new byte[9];
             byte[] Data_In;
             
             for (int i = 0; i < Servers_to_use.get_number_of_links(); i++) {
                 if (Servers_to_use.GetLink(i).check_premission_for_operation(1)) {

                     try {
                         ipstr = InetAddress.getByName(Servers_to_use.GetLink(i).url);
                     } catch (UnknownHostException e) {
                     	//bug displayed here for some reson 4 servers are present when only one should be
                     	//Result.add("Scan Could Not Be Started For Server: " + Servers_to_use.GetLink(i).url);
                         //BTScan_Running = false;
                         //BT_Current_State = Return_BTServer;
                         //return Result;
                     }
                     byte[] byteip = ipstr.getAddress();

                     Data_Out[0] = (byte) 0x01;
                     Data_Out[1] = Servers_to_use.GetLink(i).pid[0];
                     Data_Out[2] = Servers_to_use.GetLink(i).pid[1];
                     Data_Out[3] = Servers_to_use.GetLink(i).pid[2];
                     Data_Out[4] = Servers_to_use.GetLink(i).pid[3];
                     Data_Out[5] = (byte) ((byte) 0xFF & byteip[0]);
                     Data_Out[6] = (byte) ((byte) 0xFF & byteip[1]);
                     Data_Out[7] = (byte) ((byte) 0xFF & byteip[2]);
                     Data_Out[8] = (byte) ((byte) 0xFF & byteip[3]);

                     for (int i1 = 0; i1 < Servers_to_use.GetLink(i).Players_List.player_count; i1++) {
                         if (Servers_to_use.GetLink(i).Players_List.Links.get(i1).mac_address != getBtMac() && Servers_to_use.GetLink(i).Players_List.Links.get(i1).mac_address != "FF:FF:FF:FF:FF:FF")
                         {     
     
                        	 if(BTTag_Running || Mode != 2)
                        	 {
                        		 Result.add("Scan Stopped For A Tag");
                        		 BTIODestroy();
                             	 BTServerCancel();
                             	 BTClientCancel();
                        		 BTScan_Running = false;
                        		 return Result;
                        	 }
                        	 
                             if (BTClientStartUp(Servers_to_use.GetLink(i).Players_List.Links.get(i1).mac_address) == true) {
                             	send_raw(Data_Out, 9);
                                 Data_In = recv_raw(5);
                                 BTIODestroy();

                                 if (Data_In[0] == 0x01) {
                                     if (Data_In[1] == Servers_to_use.GetLink(i).Players_List.Links.get(i1).pid[0] && Data_In[2] == Servers_to_use.GetLink(i).Players_List.Links.get(i1).pid[1] && Data_In[3] == Servers_to_use.GetLink(i).Players_List.Links.get(i1).pid[2] && Data_In[4] == Servers_to_use.GetLink(i).Players_List.Links.get(i1).pid[3]) {
                                         tempitem = Servers_to_use.GetLink(i).Players_List.Links.get(i1);
                                         Result.add(new String(Servers_to_use.GetLink(i).Players_List.Links.get(i1).user_name));
                                         tempitem.server_number = i;
                                         Result_Tagg_List.Links.add(tempitem);
                                     }
                                 }
                                 if (Data_In[0] == 0x02) {
                                 }
                             }
                             
                         }
                     }
                 }
             }
            
         	 BTServerCancel();
         	 BTClientCancel();
             Scan_Tag_list = Result_Tagg_List;
             Mode = 0;
             BTScan_Running = false;
             return Result;
        }
        
        public boolean Tag(int selected_player)
        {	
        	if(BTTag_Running){return false;}
        	Mode = 3;
        	
        	while(BTServer_Running || BTAutoScan_Running || BTScan_Running ){
        		Mode = 3;
        		BTServerCancel();
        		}
        	
        	BTTag_Running = true;
        	showNotification("Tagg", "Tagging Player...",0);
                  
                if (selected_player >= 0) {
                    try {
                        ipstr = InetAddress.getByName(Servers_to_use.GetLink(Scan_Tag_list.Links.get(selected_player).server_number).url);
                    } catch (UnknownHostException e) {
                    	BTTag_Running = false;
                        return false;
                        }

                    byte[] byteip = ipstr.getAddress();
                    byte[] Data = new byte[9];

                    Data[0] = 0x02;
                    Data[1] = Servers_to_use.GetLink(Scan_Tag_list.Links.get(selected_player).server_number).pid[0];
                    Data[2] = Servers_to_use.GetLink(Scan_Tag_list.Links.get(selected_player).server_number).pid[1];
                    Data[3] = Servers_to_use.GetLink(Scan_Tag_list.Links.get(selected_player).server_number).pid[2];
                    Data[4] = Servers_to_use.GetLink(Scan_Tag_list.Links.get(selected_player).server_number).pid[3];
                    Data[5] = (byte) ((byte) 0xFF & byteip[0]);
                    Data[6] = (byte) ((byte) 0xFF & byteip[1]);
                    Data[7] = (byte) ((byte) 0xFF & byteip[2]);
                    Data[8] = (byte) ((byte) 0xFF & byteip[3]);
                    
                    for(int i = 0; i < 3; i++)
                    {
                    	if(BTClientStartUp(Scan_Tag_list.Links.get(selected_player).mac_address))
                    	{
                    		break;
                    	}
                    }
                    send_raw(Data, 9);
                    Data = recv_raw(5);
                    BTIODestroy();

                    if (Data[0] == (byte) 0x01 && Data[1] == Scan_Tag_list.Links.get(selected_player).pid[0] && Data[2] == Scan_Tag_list.Links.get(selected_player).pid[1] && Data[3] == Scan_Tag_list.Links.get(selected_player).pid[2] && Data[4] == Scan_Tag_list.Links.get(selected_player).pid[3])
                    {
                    	Mode = 0;
                    	BTTag_Running = false;
                    	return true;
                    } 
                    else 
                    {
                        ///send server cancel on req
                    	Mode = 0;
                    	BTTag_Running = false;
                        return false;
                    }
                }
                
            	BTServerCancel();
            	BTClientCancel();
                Mode = 0;
                BTTag_Running = false;   
                return false;
        }

        public boolean BTClientStartUp(String RemoteMacAddress) {   	
        	BTServerCancel();
            BTClientCancel();

            RBTDevice = null;

            try {
                RBTDevice = mBTAdapter.getRemoteDevice(RemoteMacAddress);
                mBTSocket = RBTDevice.createInsecureRfcommSocketToServiceRecord(Server_UUID);
            } catch (IOException e) {
                return false;
            }

            try {
                mBTAdapter.cancelDiscovery();
                mBTSocket.connect();

                if (BTIOSetup() == false) {
                    BTClientCancel();
                    return false;
                }
                return true;
            } catch (IOException e) {
                BTClientCancel();
                ///BTCancel();
              return false;
            } catch (NullPointerException e) {
                BTClientCancel();
                return false;
            }
        }

        public void BTServerCancel() {
        	
        	try {
                mBTSvrSocket.close();
            } catch (IOException e) {
                //return;
            } catch (NullPointerException e) {
                //return;
            }
        	mBTSvrSocket = null;
            return;
        }

        public void BTClientCancel()
        {
            try {
                mBTSocket.close();
            } catch (IOException e) {
                //return;
            } catch (NullPointerException e) {
                //return;
            }
            mBTSocket = null;
            return;
        }

        public boolean BTIOSetup() {
            try {
                mIStream = mBTSocket.getInputStream();
                mOStream = mBTSocket.getOutputStream();
                return true;
            } catch (IOException e) {
            	BTIODestroy();
                return false;
            }
        }
        
        public void BTIODestroy()
        {
        	if(mIStream != null)
        	{
        		try {
    				mIStream.close();
    			} catch (IOException e) {}
        		mIStream = null;
        	}
        	
        	if(mOStream != null)
        	{
        		try {
    				mOStream.close();
    			} catch (IOException e) {}	
        	}
        }

        public void send_raw(byte[] data_out, int data_size) {
            try {
                mOStream.write(data_out, 0, data_size);
            } catch (IOException e) {
            } catch (NullPointerException e) {
            }
        }

        public byte[] recv_raw(int data_size) {
            byte[] data = new byte[data_size];

            try {
                mIStream.read(data, 0, data_size);
            } catch (IOException e) {
            } catch (NullPointerException e) {
            }

            return data;
        }
    }

}

