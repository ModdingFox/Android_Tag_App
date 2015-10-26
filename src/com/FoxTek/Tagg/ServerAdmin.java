package com.FoxTek.Tagg;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

//import com.google.analytics.tracking.android.EasyTracker;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


public class ServerAdmin extends Activity {
    ListView server_listbox;
    Spinner spinner;
    EditText edittext1;
    EditText edittext2;
    ToggleButton toggle1;
    ToggleButton toggle2;
    ToggleButton toggle3;
    ToggleButton toggle4;
    ToggleButton toggle5;
    Button button1;
    Button button2;
    Button button3;
    TextView textview;
    TextView textview1;
    TextView textview2;
    TextView textview3;
    TextView textview4;
    TextView textview5;
    TextView textview6;
    TextView textview8;
    TextView textview9;

    ServersData Servers_to_use = new ServersData();
    fileiostuff file_io_to_use = new fileiostuff();

    public ArrayAdapter<String> serverarrayadapter;
    public ArrayAdapter<String> players_textview;

    int selecteditem = -1;

    //Activity Functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_admin);
        
        spinner = (Spinner) findViewById(R.id.spinner);
        edittext1 = (EditText) findViewById(R.id.editText);
        edittext2 = (EditText) findViewById(R.id.editText2);
        toggle1 = (ToggleButton) findViewById(R.id.toggleButton1);
        toggle2 = (ToggleButton) findViewById(R.id.toggleButton2);
        toggle3 = (ToggleButton) findViewById(R.id.toggleButton3);
        toggle4 = (ToggleButton) findViewById(R.id.toggleButton4);
        toggle5 = (ToggleButton) findViewById(R.id.toggleButton5);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        textview = (TextView) findViewById(R.id.textView);
        textview1 = (TextView) findViewById(R.id.textView1);
        textview2 = (TextView) findViewById(R.id.textView2);
        textview3 = (TextView) findViewById(R.id.textView3);
        textview4 = (TextView) findViewById(R.id.textView4);
        textview5 = (TextView) findViewById(R.id.textView5);
        textview6 = (TextView) findViewById(R.id.textView6);
        textview8 = (TextView) findViewById(R.id.textView8);
        textview9 = (TextView) findViewById(R.id.textView9);
        server_listbox = (ListView) findViewById(R.id.serverslistadmin);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textview9.setText("Player Mac: " + Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).mac_address);
                toggle5.setChecked(check_premission_for_operation(0, Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).status_byte[0]));
                toggle1.setChecked(check_premission_for_operation(1, Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).status_byte[0]));
                toggle2.setChecked(check_premission_for_operation(2, Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).status_byte[0]));
                toggle3.setChecked(check_premission_for_operation(3, Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).status_byte[0]));
                toggle4.setChecked(check_premission_for_operation(4, Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).status_byte[0]));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        server_listbox.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int selectedpos,
                                    long arg3) {
                selecteditem = selectedpos;
                Server_item tempitem;


                if(!Servers_to_use.Get_Pid_From_User(selectedpos))
                {
                	return;
                }
                	
                if(!Servers_to_use.Authenticate_Player(selectedpos))
                {
                	return;
                }


                if (Servers_to_use.GetLink(selectedpos).check_premission_for_operation(5)) {
                    tempitem = Servers_to_use.GetLink(selectedpos);
                    tempitem.authcheck = true;
                    Servers_to_use.SetLink(selecteditem, tempitem);

                    Toast connect_success = Toast.makeText(getApplicationContext(), "Auth Success :-D", Toast.LENGTH_SHORT);
                    connect_success.show();
                } else {
                    tempitem = Servers_to_use.GetLink(selectedpos);
                    tempitem.authcheck = false;
                    Servers_to_use.SetLink(selecteditem, tempitem);

                    Toast connect_failed = Toast.makeText(getApplicationContext(), "Auth Failed :-[", Toast.LENGTH_SHORT);
                    connect_failed.show();
                }

                if (Servers_to_use.GetLink(selectedpos).check_premission_for_operation(0) == Servers_to_use.GetLink(selectedpos).check_premission_for_operation(2) == true) {
                    textview.setVisibility(View.VISIBLE);
                    edittext1.setVisibility(View.VISIBLE);
                    button1.setVisibility(View.VISIBLE);
                } else {
                    textview.setVisibility(View.GONE);
                    edittext1.setVisibility(View.GONE);
                    button1.setVisibility(View.GONE);
                }
                if (Servers_to_use.GetLink(selectedpos).check_premission_for_operation(0) == Servers_to_use.GetLink(selectedpos).check_premission_for_operation(3) == true) {
                    button2.setVisibility(View.VISIBLE);
                } else {
                    button2.setVisibility(View.GONE);
                }
                if (Servers_to_use.GetLink(selectedpos).check_premission_for_operation(0) == true && Servers_to_use.GetLink(selectedpos).check_premission_for_operation(4) == true) {
                    textview2.setVisibility(View.VISIBLE);
                    textview3.setVisibility(View.VISIBLE);
                    textview4.setVisibility(View.VISIBLE);
                    textview5.setVisibility(View.VISIBLE);
                    textview6.setVisibility(View.VISIBLE);
                    textview8.setVisibility(View.VISIBLE);
                    edittext2.setVisibility(View.VISIBLE);
                    button3.setVisibility(View.VISIBLE);
                    toggle1.setVisibility(View.VISIBLE);
                    toggle2.setVisibility(View.VISIBLE);
                    toggle3.setVisibility(View.VISIBLE);
                    toggle4.setVisibility(View.VISIBLE);
                    toggle5.setVisibility(View.VISIBLE);
                } else {
                    textview2.setVisibility(View.GONE);
                    textview3.setVisibility(View.GONE);
                    textview4.setVisibility(View.GONE);
                    textview5.setVisibility(View.GONE);
                    textview6.setVisibility(View.GONE);
                    textview8.setVisibility(View.GONE);
                    edittext2.setVisibility(View.GONE);
                    button3.setVisibility(View.GONE);
                    toggle1.setVisibility(View.GONE);
                    toggle2.setVisibility(View.GONE);
                    toggle3.setVisibility(View.GONE);
                    toggle4.setVisibility(View.GONE);
                    toggle5.setVisibility(View.GONE);
                }

                if (Servers_to_use.GetLink(selectedpos).check_premission_for_operation(0) == true && (Servers_to_use.GetLink(selectedpos).check_premission_for_operation(4) == true || Servers_to_use.GetLink(selectedpos).check_premission_for_operation(3) == true)) {
                    textview1.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.VISIBLE);
                    textview9.setVisibility(View.VISIBLE);
                } else {
                    textview1.setVisibility(View.GONE);
                    spinner.setVisibility(View.GONE);
                    textview9.setVisibility(View.GONE);
                }


                Servers_to_use.Get_Player_List(selectedpos);
                
                if(Servers_to_use.GetLink(selectedpos).Players_List.Links.size() > 0)
                {
                	update_player_list(); 	
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        edittext1.setVisibility(View.GONE);
        edittext2.setVisibility(View.GONE);
        textview.setVisibility(View.GONE);
        textview1.setVisibility(View.GONE);
        textview2.setVisibility(View.GONE);
        textview3.setVisibility(View.GONE);
        textview4.setVisibility(View.GONE);
        textview5.setVisibility(View.GONE);
        textview6.setVisibility(View.GONE);
        textview8.setVisibility(View.GONE);
        textview9.setVisibility(View.GONE);
        button1.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
        button3.setVisibility(View.GONE);
        toggle1.setVisibility(View.GONE);
        toggle2.setVisibility(View.GONE);
        toggle3.setVisibility(View.GONE);
        toggle4.setVisibility(View.GONE);
        toggle5.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);

        Servers_to_use = null;
        file_io_to_use = null;

        serverarrayadapter = null;
        players_textview = null;

        selecteditem = -1;
    }

    public void onResume() {
        super.onResume();

        Servers_to_use = new ServersData();
        file_io_to_use = new fileiostuff();

        file_io_to_use.Load_Servers(Servers_to_use, getApplicationContext());

        serverarrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Servers_to_use.serverlist);
        server_listbox.setAdapter(serverarrayadapter);
        Servers_to_use.set_listview_height(server_listbox);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Routine Functions
    private void update_player_list() {
        players_textview = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Servers_to_use.GetLink(selecteditem).Players_List.Players);
        spinner.setAdapter(players_textview);
    }

    private boolean check_premission_for_operation(int check, byte premissions) {
        //takes a bool stating the flags need to check as true
        //checks those valuse against the players premissions
        //returns true if player has the premissions
        //returns false if any fails
        switch (check) {
            case 0:
                if ((premissions & (byte) 0x01) != (byte) 0x01) {
                    return false;
                }
                break;
            case 1:
                if ((premissions & (byte) 0x02) != (byte) 0x02) {
                    return false;
                }
                break;
            case 2:
                if ((premissions & (byte) 0x04) != (byte) 0x04) {
                    return false;
                }
                break;
            case 3:
                if ((premissions & (byte) 0x08) != (byte) 0x08) {
                    return false;
                }
                break;
            case 4:
                if ((premissions & (byte) 0x10) != (byte) 0x10) {
                    return false;
                }
                break;
        }
        return true;
    }

    //Interface Functions
    public void add_user(View view) {
        if (edittext1.getText().length() <= 32) {
            try {
                if (Servers_to_use.send_data(selecteditem, (byte) 0x06, edittext1.getText().toString().getBytes("US-ASCII"), edittext1.getText().length()) == false) {
                    Toast net_fail = Toast.makeText(getApplicationContext(), "Could Not Add User", Toast.LENGTH_LONG);
                    net_fail.show();
                    return;
                } else {
                    Toast net_success = Toast.makeText(getApplicationContext(), "User Added To Game", Toast.LENGTH_LONG);
                    net_success.show();
                }
            } catch (UnsupportedEncodingException e) {
            }

        } else {
            Toast user_to_long = Toast.makeText(getApplicationContext(), "User Name Is Too Long", Toast.LENGTH_SHORT);
            user_to_long.show();
        }
        Servers_to_use.Get_Player_List(selecteditem);
        update_player_list();
    }

    public void remove_user(View view) {
        if (Servers_to_use.send_data(selecteditem, (byte) 0x07, Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).pid, 4) == false) {
            Toast net_fail = Toast.makeText(getApplicationContext(), "Could Not Remove User", Toast.LENGTH_LONG);
            net_fail.show();
            return;
        } else {
            Toast net_success = Toast.makeText(getApplicationContext(), "User Removed From Game", Toast.LENGTH_LONG);
            net_success.show();
        }

        Servers_to_use.Get_Player_List(selecteditem);
        update_player_list();
    }

    public void change_pin(View view) {
        byte[] temp_pin_bytes = null;
        byte[] byte_data_buffer = new byte[6];
        for (int i = 0; i < 4; i++) {
            byte_data_buffer[i] = Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).pid[i];
        }

        try {
            temp_pin_bytes = edittext2.getText().toString().getBytes("US-ASCII");
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
            Toast net_fail = Toast.makeText(getApplicationContext(), "Could Not Set User's Pin", Toast.LENGTH_LONG);
            net_fail.show();
            return;
        } else {
            Toast net_success = Toast.makeText(getApplicationContext(), "User's Pin Set", Toast.LENGTH_LONG);
            net_success.show();
        }
    }

    public void toggle_active(View view) {
        byte[] byte_data_buffer = new byte[5];

        for (int i = 0; i < 4; i++) {
            byte_data_buffer[i] = Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).pid[i];
        }

        byte_data_buffer[4] = 0x00;


        if (Servers_to_use.send_data(selecteditem, (byte) 0x09, byte_data_buffer, 5) == false) {
            Toast net_fail = Toast.makeText(getApplicationContext(), "Could Not Set User's Active Status", Toast.LENGTH_SHORT);
            net_fail.show();
            return;
        } else {
            Toast net_success = Toast.makeText(getApplicationContext(), "User's Active Status Set", Toast.LENGTH_SHORT);
            net_success.show();
        }
    }

    public void toggle_it(View view) {
        byte[] byte_data_buffer = new byte[5];

        for (int i = 0; i < 4; i++) {
            byte_data_buffer[i] = Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).pid[i];
        }

        byte_data_buffer[4] = 0x01;

        if (Servers_to_use.send_data(selecteditem, (byte) 0x09, byte_data_buffer, 5) == false) {
            Toast net_fail = Toast.makeText(getApplicationContext(), "Could Not Set User's It Status", Toast.LENGTH_SHORT);
            net_fail.show();
            return;
        } else {
            Toast net_success = Toast.makeText(getApplicationContext(), "User's It Status Set", Toast.LENGTH_SHORT);
            net_success.show();
        }
    }

    public void toggle_add_players(View view) {
        byte[] byte_data_buffer = new byte[5];

        for (int i = 0; i < 4; i++) {
            byte_data_buffer[i] = Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).pid[i];
        }

        byte_data_buffer[4] = 0x02;

        if (Servers_to_use.send_data(selecteditem, (byte) 0x09, byte_data_buffer, 5) == false) {
            Toast net_fail = Toast.makeText(getApplicationContext(), "Could Not Set User's Add Status", Toast.LENGTH_SHORT);
            net_fail.show();
            return;
        } else {
            Toast net_success = Toast.makeText(getApplicationContext(), "User's Add Status Set", Toast.LENGTH_SHORT);
            net_success.show();
        }
    }

    public void toggle_remove_players(View view) {
        byte[] byte_data_buffer = new byte[5];

        for (int i = 0; i < 4; i++) {
            byte_data_buffer[i] = Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).pid[i];
        }

        byte_data_buffer[4] = 0x03;

        if (Servers_to_use.send_data(selecteditem, (byte) 0x09, byte_data_buffer, 5) == false) {
            Toast net_fail = Toast.makeText(getApplicationContext(), "Could Not Set User's Remove Status", Toast.LENGTH_SHORT);
            net_fail.show();
            return;
        } else {
            Toast net_success = Toast.makeText(getApplicationContext(), "User's Remove Status Set", Toast.LENGTH_SHORT);
            net_success.show();
        }
    }

    public void toggle_admin(View view) {
        byte[] byte_data_buffer = new byte[5];

        for (int i = 0; i < 4; i++) {
            byte_data_buffer[i] = Servers_to_use.GetLink(selecteditem).Players_List.Links.get(spinner.getSelectedItemPosition()).pid[i];
        }

        byte_data_buffer[4] = 0x04;


        if (Servers_to_use.send_data(selecteditem, (byte) 0x09, byte_data_buffer, 5) == false) {
            Toast net_fail = Toast.makeText(getApplicationContext(), "Could Not Set User's Admin Status", Toast.LENGTH_SHORT);
            net_fail.show();
            return;
        } else {
            Toast net_success = Toast.makeText(getApplicationContext(), "User's Admin Status Set", Toast.LENGTH_SHORT);
            net_success.show();
        }
    }
}


