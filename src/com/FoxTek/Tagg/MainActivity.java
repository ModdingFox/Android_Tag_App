package com.FoxTek.Tagg;

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
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutionException;



import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.*;
import android.app.TabActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.content.Intent;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
    //routine stuff

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //check if app requierments are met
        final BluetoothAdapter myBTAdapter = BluetoothAdapter.getDefaultAdapter();
        final ConnectivityManager myconnections = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        myBTAdapter.enable();

        if (myBTAdapter == null) {
            Toast.makeText(this, "No Bluetooth Adapter Found Closeing...", Toast.LENGTH_LONG).show();
            System.exit(0);
        }
        // if(myconnections.getNetworkInfo(0).getState() != NetworkInfo.State.CONNECTED){Toast.makeText(this,"No Network Service Found Closeing...",Toast.LENGTH_LONG).show(); System.exit(0);}

        if(!Service_Check()){startService(new Intent(getApplicationContext(), TaggService.class));}

        TabHost tabHost = getTabHost();

        TabSpec scanspec = tabHost.newTabSpec("Scan");
        scanspec.setIndicator("Scan");
        Intent scanIntent = new Intent(this, Scan.class);
        scanspec.setContent(scanIntent);

        TabSpec ServerAdminspec = tabHost.newTabSpec("Server Admin");
        ServerAdminspec.setIndicator("Server Admin");
        Intent ServerAdminIntent = new Intent(this, ServerAdmin.class);
        ServerAdminspec.setContent(ServerAdminIntent);

        TabSpec settingsspec = tabHost.newTabSpec("Settings");
        settingsspec.setIndicator("Settings");
        Intent settingsIntent = new Intent(this, Settings.class);
        settingsspec.setContent(settingsIntent);

        TabSpec Aboutspec = tabHost.newTabSpec("About");
        Aboutspec.setIndicator("About");
        Intent AboutIntent = new Intent(this, About.class);
        Aboutspec.setContent(AboutIntent);

        tabHost.addTab(scanspec);
        tabHost.addTab(ServerAdminspec);
        tabHost.addTab(settingsspec);
        tabHost.addTab(Aboutspec);
    }

    public boolean Service_Check()
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TaggService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

