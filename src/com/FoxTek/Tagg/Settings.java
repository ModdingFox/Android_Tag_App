package com.FoxTek.Tagg;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.content.Context;

//import com.google.analytics.tracking.android.EasyTracker;



import android.content.DialogInterface;

import java.io.*;
import java.lang.*;

public class Settings extends Activity {
    //need to add new buttons here
	ToggleButton AppAutoStart;
	ToggleButton AutoScanSoundToggle;
	ToggleButton TaggSoundToggle;
	ToggleButton AutoScanDetectToggle;
	Button AutoScanDetectSoundSet;
	Button AutoScanSoundSet;
	Button TaggSoundSet;
	TextView AutoScanDetectLabel;
	TextView AutoScanLabel;
	TextView TaggLabel;
	ToggleButton autoscantoggle;
    EditText scanintersetbox;
    EditText userbox;
    EditText pinbox;
    EditText svripportsetbox;
    Button addsvrbttn;
    Button delsvrbttn;
    Button change_pin;
    ListView svrlistview;
    TextView newpintext;
    EditText setpinbox;

    int selecteditem = -1;
    public ArrayAdapter<String> serverarrayadapter;

    ServersData Servers_to_use = new ServersData();
    settings_obj settings_to_use = new settings_obj();
    fileiostuff file_io_to_use = new fileiostuff();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        AppAutoStart = (ToggleButton) findViewById(R.id.toggleButton3);
        AutoScanSoundToggle = (ToggleButton) findViewById(R.id.toggleButton2);
        TaggSoundToggle = (ToggleButton) findViewById(R.id.toggleButton1);
        AutoScanDetectToggle = (ToggleButton) findViewById(R.id.toggleButton4);
        AutoScanDetectSoundSet = (Button) findViewById(R.id.button3);
        AutoScanSoundSet = (Button) findViewById(R.id.button2);
        TaggSoundSet = (Button) findViewById(R.id.button1);
        AutoScanLabel = (TextView) findViewById(R.id.textView11);
        AutoScanDetectLabel = (TextView) findViewById(R.id.textView13);
        TaggLabel = (TextView) findViewById(R.id.textView10);
        autoscantoggle = (ToggleButton) findViewById(R.id.autoscantoggle);
        scanintersetbox = (EditText) findViewById(R.id.scaninterset);
        userbox = (EditText) findViewById(R.id.setuser);
        pinbox = (EditText) findViewById(R.id.setpin);
        svripportsetbox = (EditText) findViewById(R.id.serversettings);
        addsvrbttn = (Button) findViewById(R.id.addsvr);
        delsvrbttn = (Button) findViewById(R.id.delsvr);
        svrlistview = (ListView) findViewById(R.id.serverslistsettings);
        change_pin = (Button) findViewById(R.id.button);
        newpintext = (TextView) findViewById(R.id.textView);
        setpinbox = (EditText) findViewById(R.id.editText);
        
        svrlistview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int selectedpos,
                                    long arg3) {
                selecteditem = selectedpos;
                userbox.setText(Servers_to_use.GetLink(selectedpos).username.toString());
                pinbox.setText(Servers_to_use.GetLink(selectedpos).pin.toString());
                svripportsetbox.setText(Servers_to_use.GetLink(selectedpos).url.toString() + ":" + Integer.toString(Servers_to_use.GetLink(selectedpos).port).toString());
                newpintext.setVisibility(View.VISIBLE);
                setpinbox.setVisibility(View.VISIBLE);
                change_pin.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Servers_to_use = new ServersData();
        settings_to_use = new settings_obj();
        file_io_to_use = new fileiostuff();

            file_io_to_use.Load_Servers(Servers_to_use, getApplicationContext());
        
			file_io_to_use.Load_Settings(settings_to_use, getApplicationContext());
			
        autoscantoggle.setChecked(settings_to_use.autoscantoggle);
        scanintersetbox.setText(settings_to_use.scantime);
        AppAutoStart.setChecked(settings_to_use.Auto_Start);
        AutoScanSoundToggle.setChecked(settings_to_use.AutoScan_Sound_Enabled);
        TaggSoundToggle.setChecked(settings_to_use.Tagged_Sound_Enabled);
        AutoScanDetectToggle.setChecked(settings_to_use.AutoScan_Hit_Sound_Enabled);
        AutoScanLabel.setText(settings_to_use.AutoScan_Sound);
        TaggLabel.setText(settings_to_use.Tagged_Sound);
        AutoScanDetectLabel.setText(settings_to_use.AutoScan_Hit_Sound);
        
        serverarrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Servers_to_use.serverlist);
        svrlistview.setAdapter(serverarrayadapter);
        Servers_to_use.set_listview_height(svrlistview);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Routine Functions
    public void Set_New_Pin(View view)
    {
        if(setpinbox.getText().length() == 4)
        {
        byte[] temp_pin_bytes = null;
        byte[] byte_data_buffer = new byte[6];

        Servers_to_use.Get_Pid_From_User(selecteditem);
        Servers_to_use.Authenticate_Player(selecteditem);

        for (int i = 0; i < 4; i++) {
            byte_data_buffer[i] = Servers_to_use.GetLink(selecteditem).pid[i];
        }

        try {
            temp_pin_bytes = setpinbox.getText().toString().getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        temp_pin_bytes[0] = (byte) (temp_pin_bytes[0] << 4);
        temp_pin_bytes[2] = (byte) (temp_pin_bytes[2] << 4);
        temp_pin_bytes[1] = (byte) (temp_pin_bytes[1] & 0x0F);
        temp_pin_bytes[3] = (byte) (temp_pin_bytes[3] & (byte) 0x0F);

        byte_data_buffer[4] = (byte) (temp_pin_bytes[0] | temp_pin_bytes[1]);
        byte_data_buffer[5] = (byte) (temp_pin_bytes[2] | temp_pin_bytes[3]);


        if (Servers_to_use.send_data(selecteditem, (byte) 0x08, byte_data_buffer, 6) == false) {
            Toast net_fail = Toast.makeText(getApplicationContext(), "Could Not Set Pin", Toast.LENGTH_LONG);         
            net_fail.show();
            return;
        } else {
            Toast net_success = Toast.makeText(getApplicationContext(), "User Pin Set", Toast.LENGTH_LONG);
            pinbox.setText(setpinbox.getText().toString());
            Server_item Temp_Item = Servers_to_use.GetLink(selecteditem);
            Temp_Item.pin = pinbox.getText().toString();
            Servers_to_use.SetLink(selecteditem, Temp_Item);
            net_success.show();
        }

        change_pin.setVisibility(View.GONE);
        setpinbox.setVisibility(View.GONE);
        newpintext.setVisibility(View.GONE);
        }
        
        applydatatostruct(null);
    }

    public void Add_Server(View view) {
        ViewGroup.LayoutParams lvparams = svrlistview.getLayoutParams();
        Servers_to_use.AddLink();
        svrlistview.setAdapter(serverarrayadapter);
        Servers_to_use.set_listview_height(svrlistview);
        selecteditem = Servers_to_use.get_number_of_links() - 1;

        selecteditem = Servers_to_use.get_number_of_links() - 1;

        Server_item tempitem = Servers_to_use.GetLink(selecteditem);
        String[] data = svripportsetbox.getText().toString().split(":");
        tempitem.url = new String(data[0]);
        tempitem.port = Integer.parseInt(data[1]);
        tempitem.username = userbox.getText().toString();
        tempitem.pin = pinbox.getText().toString();
        Servers_to_use.SetLink(selecteditem, tempitem);
    }

    public void applydatatostruct(View view) {
    	///need to  add support for saveing both server and non-server data
    	stopService(new Intent(getApplicationContext(),TaggService.class));
    	
        settings_to_use.scantime = scanintersetbox.getText().toString();
        settings_to_use.autoscantoggle = autoscantoggle.isChecked();
        settings_to_use.Auto_Start = AppAutoStart.isChecked();
        settings_to_use.AutoScan_Sound_Enabled = AutoScanSoundToggle.isChecked();
        settings_to_use.Tagged_Sound_Enabled = TaggSoundToggle.isChecked();
        settings_to_use.AutoScan_Hit_Sound_Enabled = AutoScanDetectToggle.isChecked();
        
        file_io_to_use.Save_Settings(settings_to_use, getApplicationContext());
            
        Toast.makeText(getApplicationContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
        
        if (Servers_to_use.get_number_of_links() > 0) {
        	Server_item tempitem;
        	for(int i = 0; i < Servers_to_use.get_number_of_links(); i++)
        	{
        		tempitem = Servers_to_use.GetLink(i);
        		
        		//need to add check somewhere to see if server is up in the first place
        		
        		Servers_to_use.Get_Pid_From_User(i);
                Servers_to_use.Authenticate_Player(i);

                if (Servers_to_use.GetLink(i).check_premission_for_operation(5)) {
                    tempitem.authcheck = true;
                    Toast.makeText(getApplicationContext(), "Sign In Success :-D", Toast.LENGTH_SHORT).show();
                } 
                else {
                    tempitem.authcheck = false;
                    Toast.makeText(getApplicationContext(), "Sign In Failed :-[", Toast.LENGTH_SHORT).show();
                }  
                
                try {
                    if (Servers_to_use.send_data(i, (byte) 0x0B, BluetoothAdapter.getDefaultAdapter().getAddress().getBytes("US-ASCII"), BluetoothAdapter.getDefaultAdapter().getAddress().length()) == true) {
                    } else {
                        Toast.makeText(getApplicationContext(), "Could Not Update Bt Mac", Toast.LENGTH_SHORT).show();
                        tempitem.authcheck = false;
                    }
                } catch (UnsupportedEncodingException e) {}
                
                Servers_to_use.SetLink(i, tempitem);
        	}
        	
            file_io_to_use.Save_Servers(Servers_to_use, getApplicationContext());//may need to create file here?
                      
                Toast.makeText(getApplicationContext(), "Servers Saved", Toast.LENGTH_SHORT).show();
        }
        
        svrlistview.setAdapter(serverarrayadapter);
        
        startService(new Intent(getApplicationContext(),TaggService.class));
    }

    public void Remove_Server(View view) {
        if (selecteditem != -1) {
            Servers_to_use.RemoveLink(selecteditem);
            svrlistview.setAdapter(serverarrayadapter);
            Servers_to_use.set_listview_height(svrlistview);
            svrlistview.setSelection(0);
        }
    }
    
    AlertDialog.Builder Alert_Dialog_Builder;
    
    public void Get_AutoScan_Tone(View view)
    {
    	Alert_Dialog_Builder = new AlertDialog.Builder(this);
    	AlertDialog MessageBox;
    	
    	Alert_Dialog_Builder
    	.setTitle("AutoScan Tone")
    	.setMessage("Select Media Type:")
    	.setPositiveButton("Ring Tones", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SelectRingTone(0);
			}
		});
    	Alert_Dialog_Builder.setNegativeButton("Audio File", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SelectMp3(0);
			}
		});
    	
    	MessageBox = Alert_Dialog_Builder.create();
    	MessageBox.show();
    }
    
    public void Get_Tagg_Tone(View view)
    {
    	Alert_Dialog_Builder = new AlertDialog.Builder(this);
    	AlertDialog MessageBox;
    	
    	Alert_Dialog_Builder
    	.setTitle("Tagg Tone")
    	.setMessage("Select Media Type:")
    	.setPositiveButton("Ring Tones", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SelectRingTone(1);
			}
		});
    	Alert_Dialog_Builder.setNegativeButton("Audio File", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SelectMp3(1);
			}
		});
    	
    	MessageBox = Alert_Dialog_Builder.create();
    	MessageBox.show();
    }
    
    public void Get_AutoScan_Detect_Tone(View view)
    {
    	//this function is not linked to anything yet
    	Alert_Dialog_Builder = new AlertDialog.Builder(this);
    	AlertDialog MessageBox;
    	
    	Alert_Dialog_Builder
    	.setTitle("AutoScan Detect")
    	.setMessage("Select Media Type:")
    	.setPositiveButton("Ring Tones", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SelectRingTone(2);
			}
		});
    	Alert_Dialog_Builder.setNegativeButton("Audio File", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SelectMp3(2);
			}
		});
    	
    	MessageBox = Alert_Dialog_Builder.create();
    	MessageBox.show();
    }
    
    public void SelectRingTone(int SoundToSet)
    {
    	Intent Ring_Selector = new Intent(this, RingToneSelector.class);
    	Ring_Selector.putExtra("Ring Location", SoundToSet);
    	startActivity(Ring_Selector);
    }
    
    public void SelectMp3(int SoundToSet)
    {
    	Intent Mp3_Selector = new Intent(this, FileSelector.class);
    	Mp3_Selector.putExtra("File Location", SoundToSet);
    	startActivity(Mp3_Selector);
    }
}