package com.FoxTek.Tagg;

import android.R.color;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

//import com.google.analytics.tracking.android.EasyTracker;

public class Scan extends Activity {
    TextView status;
    ListView players_listview;
    Button tagg_button;
    Button Scan_Button;

    ITagService mITaggService;
    boolean ITaggServiceIsBound = false;
    
    Context thiscontext = this;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mITaggService = ITagService.Stub.asInterface(iBinder);
            ITaggServiceIsBound = true;      
            
            ///this along with the on resume are the cause of the extra data send and recieved
            try {
    			if (mITaggService.It_Status()) {
    			    status.setText("Status: It!");
    			    Scan_Button.setVisibility(View.VISIBLE);
    			    tagg_button.setVisibility(View.VISIBLE);
    			    tagg_button.setEnabled(false);
    			} else {
    			    status.setText("Status: Not It O_o");
    			}
    		} catch (RemoteException e) {
    			status.setText("Error: Contacting Local Tagg Service");
    		}    
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mITaggService = null;
            ITaggServiceIsBound = false;
        }
    };

    int selecteditem = -1;

    //Activity Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        status = (TextView) findViewById(R.id.serveraddress);
        players_listview = (ListView) findViewById(R.id.listView);
        Scan_Button = (Button) findViewById(R.id.button);
        tagg_button = (Button) findViewById(R.id.button2);

        players_listview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int selectedpos,
                                    long arg3) {
                selecteditem = selectedpos;
                
                for(int i = 0; i < arg0.getCount(); i++)
                {
                	arg0.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                
                arg0.getChildAt(selectedpos).setBackgroundColor(Color.BLUE);
                
                tagg_button.setEnabled(true);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        status.setText("Status: Unkown");
	    Scan_Button.setVisibility(View.GONE);
	    tagg_button.setVisibility(View.GONE);
	    
        getApplicationContext().bindService(new Intent(getApplicationContext(),TaggService.class),mConnection, Context.BIND_AUTO_CREATE);
        
        //EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // EasyTracker.getInstance().activityStop(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status.setText("Status: Unkown");
	    Scan_Button.setVisibility(View.GONE);
	    tagg_button.setVisibility(View.GONE);
    }
    
    Get_AutoScan_List Auto_List = null;
    
    @Override
    protected void onResume() {
        super.onResume();
        
        if(ITaggServiceIsBound)
        {
        try {
        	mITaggService.Refresh_Servers();
			if (mITaggService.It_Status()) {
			    status.setText("Status: It!");
			    Scan_Button.setVisibility(View.VISIBLE);
			    tagg_button.setVisibility(View.VISIBLE);
			    tagg_button.setEnabled(false);
			    
			    if(Auto_List != null){Auto_List.cancel(true);}
			    Auto_List= new Get_AutoScan_List();
			    Auto_List.execute();
			    
			} 
			else {
			    status.setText("Status: Not It O_o");
			}
		} catch (RemoteException e) {
			status.setText("Error: Contacting Local Tagg Service");
		}
        }
    }
    class Get_AutoScan_List extends AsyncTask <Void, Void, Boolean>{
    	ArrayList<String> Results = null;
    	
		@Override
		protected Boolean doInBackground(Void... none) {
			if(ITaggServiceIsBound == true)
			{
				try {
					Results = (ArrayList<String>)mITaggService.GetAllScan_Results();
				} catch (RemoteException e) {
				}
				if(Results == null)
				{
					return false;
				}
			}
			return true;
		}
    	
		protected void onPostExecute(Boolean listhasentries)
		{
			if(listhasentries)
			{
				players_listview.setAdapter(new ArrayAdapter<String>(thiscontext, android.R.layout.simple_list_item_1, Results)); 

                ListAdapter listadapter = players_listview.getAdapter();
                if (listadapter == null) {
                    return;
                }

                int height = 0;
                for (int i = 0; i < listadapter.getCount(); i++) {
                    View listitem = listadapter.getView(i, null, players_listview);
                    listitem.measure(0, 0);
                    height += listitem.getMeasuredHeight();
                }

                ViewGroup.LayoutParams listparams = players_listview.getLayoutParams();
                listparams.height = height + (players_listview.getDividerHeight() * (listadapter.getCount() - 1));
                players_listview.setLayoutParams(listparams);
                players_listview.setVisibility(View.VISIBLE);
			}
			else {
				players_listview.setVisibility(View.GONE);
			}
		}
    }

    //Everything else
    public boolean Service_Check()
    {
    	ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
    	        if (TaggService.class.getName().equals(service.service.getClassName())) {
    	            return true;
    	        }
    	    }
    	    return false;
    }

    private Tag_Task run_Tag;
    public void Tag(View view) {
        if(selecteditem >= 0 && ITaggServiceIsBound)
        {       
           run_Tag = new Tag_Task();
           run_Tag.execute();      
        }
    }
    
    private class Tag_Task extends AsyncTask<Void, Void, Void>
    {
    	boolean Tag_Success = false;
    	int Selected_player;
    	@Override
    	protected void onPreExecute()
    	{
    		Scan_Button.setEnabled(false);
            tagg_button.setEnabled(false);
    	}

		@Override
		protected Void doInBackground(Void... params) {
			 try {
				 	Selected_player = selecteditem;
	                Tag_Success = mITaggService.Tag(selecteditem);
	            } catch (RemoteException e) {
	                Toast.makeText(getApplicationContext(),"Error The Local Tagg Service Could Not Be Contacted.",Toast.LENGTH_LONG);
	            }
			return null;
		}
		
		@Override
		protected void onPostExecute(Void none)
		{
			if(Tag_Success == true)
			{
				try {
					mITaggService.Tag_Request_Send(Selected_player);
					mITaggService.Refresh_Servers();
				} catch (RemoteException e) {
				}
			}
			
			try {
				mITaggService.Refresh_Servers();
			} catch (RemoteException e1) {}
			
			try {
                if (mITaggService.It_Status()) {
                    status.setText("Status: It!");
                    Scan_Button.setVisibility(View.VISIBLE);
                    tagg_button.setVisibility(View.VISIBLE);
                    Scan_Button.setEnabled(true);
                } else {
                    status.setText("Status: Not It O_o");
                    Scan_Button.setVisibility(View.GONE);
                    tagg_button.setVisibility(View.GONE);
                    players_listview.setVisibility(View.GONE);
                }
            } catch (RemoteException e) {
                status.setText("Error: Contacting Local Tagg Service");
            }
		}  	
    }

    private Scan_Task run_scan;
    public void Scan(View view) {
        if(ITaggServiceIsBound)
        {
        	run_scan = new Scan_Task();
        	run_scan.execute();
        }
    }
    
    private class Scan_Task extends AsyncTask<Void, Void, Void>
    {
    	ArrayList<String> ScanRes = new ArrayList<String>();
		@Override
		protected Void doInBackground(Void... params) {
			
            try {
                ScanRes = (ArrayList<String>)mITaggService.Scan();
            } catch (RemoteException e) {
                Toast.makeText(getApplicationContext(),"Error The Local Tagg Service Could Not Be Contacted.",Toast.LENGTH_LONG).show();
            }
            return null;
		}
		
		@Override
		protected void onPreExecute()
		{
			Scan_Button.setEnabled(false);
	        tagg_button.setEnabled(false);
		}
		
		@Override
		protected void onPostExecute(Void none)
		{
			try {
				mITaggService.It_Status();
			} catch (RemoteException e) {}
			
			if(!ScanRes.isEmpty())
            {
                players_listview.setAdapter(new ArrayAdapter<String>(thiscontext, android.R.layout.simple_list_item_1, ScanRes));

                ListAdapter listadapter = players_listview.getAdapter();
                if (listadapter == null) {
                    return;
                }

                int height = 0;
                for (int i = 0; i < listadapter.getCount(); i++) {
                    View listitem = listadapter.getView(i, null, players_listview);
                    listitem.measure(0, 0);
                    height += listitem.getMeasuredHeight();
                }

                ViewGroup.LayoutParams listparams = players_listview.getLayoutParams();
                listparams.height = height + (players_listview.getDividerHeight() * (listadapter.getCount() - 1));
                players_listview.setLayoutParams(listparams);
                players_listview.setVisibility(View.VISIBLE);
            }
			else
			{
				players_listview.setVisibility(View.GONE);
			}
            Scan_Button.setEnabled(true);
		}
    	
    }
}


