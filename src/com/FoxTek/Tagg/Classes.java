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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

class fileiostuff {
	boolean IO_Locked = false;
    Server_item tempitem = null;

    void fileiostuff() {
    }
    
    private static int Settings_File = 0;
    private static int Servers_File = 1;
    
    private void update_files(String old_version, int file_to_use)
    {
    	return;
    }
    
    public void Save_Settings(settings_obj settings,  Context appcontext)
    {
         try {
        	 BufferedWriter outputwriter = new BufferedWriter(new OutputStreamWriter(appcontext.openFileOutput("TagSettings", Context.MODE_PRIVATE)));
        	 
        	 outputwriter.write("Settings V0.2");
        	 outputwriter.newLine();
        	 
        	 //write app auto start
        	 if (settings.Auto_Start == true) {
                 outputwriter.write(0x02);
             } else{
                 outputwriter.write(0x01);
             }
             outputwriter.newLine();
             
        	 //write tagged sound status
             if (settings.Tagged_Sound_Enabled == true) {
                 outputwriter.write(0x02);
             } else{
                 outputwriter.write(0x01);
             }
             outputwriter.newLine();
             
        	 //write tagged sound location
             outputwriter.write(settings.Tagged_Sound);
             outputwriter.newLine();
             
        	 //write autoscan sound status
             if (settings.AutoScan_Sound_Enabled == true) {
                 outputwriter.write(0x02);
             } else{
                 outputwriter.write(0x01);
             }
             outputwriter.newLine();
             
        	 //write autoscan sound location
             outputwriter.write(settings.AutoScan_Sound);
             outputwriter.newLine();
             
             //write autoscan status
             if (settings.autoscantoggle == true) {
                 outputwriter.write(0x02);
             } else{
                 outputwriter.write(0x01);
             }
             outputwriter.newLine();

             //write autoscan time
             outputwriter.write(settings.scantime);
             outputwriter.newLine();
             
             
             //write autoscan_detect location
             outputwriter.write(settings.AutoScan_Hit_Sound);
             outputwriter.newLine();
             
             //write autoscan_detect enabled
             if (settings.AutoScan_Hit_Sound_Enabled == true) {
                 outputwriter.write(0x02);
             } else{
                 outputwriter.write(0x01);
             }
             
             outputwriter.flush();
             outputwriter.close();
         } catch (IOException e) {}
    }
    
    public void Load_Settings(settings_obj settings, Context appcontext)
    {
    	try {
            BufferedReader inputreader = new BufferedReader(new InputStreamReader(appcontext.openFileInput("TagSettings")));
            
            String DataVersionCheck = inputreader.readLine();
            
            //settings_obj.update_files(DataVersionCheck, Settings_File);
            
          //read app auto start
            if (inputreader.readLine().getBytes()[0] == 0x01) {
                settings.Auto_Start = false;
            } 
            else {
                settings.Auto_Start = true;
            }
            
       	 //read tagged sound status
            if (inputreader.readLine().getBytes()[0] == 0x01) {
                settings.Tagged_Sound_Enabled = false;
            } else {
                settings.Tagged_Sound_Enabled = true;
            }
            
       	 //read tagged sound location
            settings.Tagged_Sound = inputreader.readLine();
       	 
       	 //read autoscan sound status
            if (inputreader.readLine().getBytes()[0] == 0x01) {
                settings.AutoScan_Sound_Enabled = false;
            } else {
                settings.AutoScan_Sound_Enabled = true;
            }
            
       	 //read autoscan sound location
            settings.AutoScan_Sound = inputreader.readLine();
            
            //read autoscan status
            if (inputreader.readLine().getBytes()[0] == 0x01) {
                settings.autoscantoggle = false;
            } else {
                settings.autoscantoggle = true;
            }

            //read autoscan time
            settings.scantime = inputreader.readLine();
            
            //read autoscan detect location
            settings.AutoScan_Hit_Sound = inputreader.readLine();
            
            //read auto scan detect enabled
            if (inputreader.readLine().getBytes()[0] == 0x01) {
                settings.AutoScan_Hit_Sound_Enabled = false;
            } else {
                settings.AutoScan_Hit_Sound_Enabled = true;
            }
            
            inputreader.close();    
    	} catch (FileNotFoundException e) {
    		settings_obj newdata = new settings_obj();
    		Save_Settings(newdata, appcontext);} 
    	catch (IOException e) {}         
    }

    public void Save_Servers(ServersData server_data, Context appcontext) {
        int signedin = 0;

        try {
            BufferedWriter outputwriter = new BufferedWriter(new OutputStreamWriter(appcontext.openFileOutput("TagServers", Context.MODE_PRIVATE)));
            
        	outputwriter.write("Servers V0.1");
       	 	outputwriter.newLine();
        	
            //write number of servers
            for (int i = 0; i < server_data.get_number_of_links(); i++) {
                tempitem = server_data.GetLink(i);
                if (tempitem.authcheck == true) {
                    signedin++;
                }
            }
            outputwriter.write(Integer.toString(signedin));
            outputwriter.newLine();
            for (int i = 0; i < server_data.get_number_of_links(); i++) {
                tempitem = server_data.GetLink(i);
                //write url
                if (tempitem.authcheck == true) {
                    outputwriter.write(tempitem.url);
                    outputwriter.newLine();
                    //write port
                    outputwriter.write(Integer.toString(tempitem.port));//port temp fake
                    outputwriter.newLine();
                    //write username
                    outputwriter.write(tempitem.username);
                    outputwriter.newLine();
                    //write pin
                    outputwriter.write(tempitem.pin);
                    outputwriter.newLine();
                    //move to next server
                }
            }
            outputwriter.flush();
            outputwriter.close();
        } catch (IOException e) {}
    }

    public void Load_Servers(ServersData server_data, Context appcontext) {
        int numberofserversinfile;
       
        server_data.Set_Context(appcontext);
        
        try {   	
        	BufferedReader inputreader = new BufferedReader(new InputStreamReader(appcontext.openFileInput("TagServers")));
        	
        	String DataVersionCheck = inputreader.readLine();
        	
            //read number of servers
        	String numberofserversstring = inputreader.readLine();
            numberofserversinfile = Integer.parseInt(numberofserversstring);

            for (int i = 0; i < numberofserversinfile; i++) {
                server_data.AddLink();
            }

            for (int i = 0; i < numberofserversinfile; i++) {
                tempitem = server_data.GetLink(i);

                tempitem.authcheck = true;

                tempitem.url = inputreader.readLine();

                tempitem.port = Integer.parseInt(inputreader.readLine());

                tempitem.username = inputreader.readLine();

                tempitem.pin = inputreader.readLine();

                server_data.SetLink(i, tempitem);

                //move to next server
            }
            inputreader.close();   
        } catch (FileNotFoundException e) {File createfile = new File("TagServers");}
        catch (IOException e) {}
    }

}

class settings_obj {
	public boolean Auto_Start;
    
    public String Tagged_Sound;
    public boolean Tagged_Sound_Enabled;
    
    public String AutoScan_Sound;
    public boolean AutoScan_Sound_Enabled;
    
    public String AutoScan_Hit_Sound;//not yet implimented
    public boolean AutoScan_Hit_Sound_Enabled;// not yet implimented;
    
    public boolean autoscantoggle;
    public String scantime;

    public settings_obj() {
		Auto_Start = true;
		Tagged_Sound = "None Set";
		Tagged_Sound_Enabled = false;
		AutoScan_Sound = "None Set";
		AutoScan_Sound_Enabled = false;
		AutoScan_Hit_Sound = "None Set";
		AutoScan_Hit_Sound_Enabled = false;
		autoscantoggle = true;
		scantime = "5";
	}
}



class networkstuff extends AsyncTask<Server_item, Void, Boolean>{
    private Socket connectionsocket;
    private OutputStream socketout;
    private InputStream socketin;

    public Boolean doInBackground(Server_item... Server_Item_input) {
        InetAddress svraddress = null;
        try {
            svraddress = InetAddress.getByName(Server_Item_input[0].url);
        } catch (UnknownHostException e) {
            return false;
            //e.printStackTrace();
        }
        
        
        
        try {
            connectionsocket = new Socket();
            connectionsocket.connect(new InetSocketAddress(svraddress, Server_Item_input[0].port), 5000);
            connectionsocket.setSoTimeout(5000);
        } catch (IOException e) {
            return false;
            //e.printStackTrace();
        }

        try {
            socketin = connectionsocket.getInputStream();
            socketout = connectionsocket.getOutputStream();
        } catch (IOException e) {
            return false;
            //e.printStackTrace();
        }

        ////run procedure for item
        OutputStream Send_Data = socketout;
        try {
            Send_Data.write(Server_Item_input[0].data_buffer, 0, Server_Item_input[0].data_buffer_size);
        } catch (IOException e) {
            return false;
            //e.printStackTrace();
        }
        // this is a test procedure

        InputStream Recv_Data = socketin;
        int players_to_get = 0;
        try {
            switch (Server_Item_input[0].data_buffer[0]) {
                case 1:
                    Recv_Data.read(Server_Item_input[0].pid, 0, 4);
                    break;
                case 2:
                    Recv_Data.read(Server_Item_input[0].status_byte, 0, 1);
                    //get_status_byte();
                    break;
                case 3:
                    if (Server_Item_input[0].check_premission_for_operation(4) == true || Server_Item_input[0].check_premission_for_operation(3) == true || Server_Item_input[0].check_premission_for_operation(1) == true) {
                        Recv_Data.read(Server_Item_input[0].data_buffer, 0, 4);

                        players_to_get = ((Server_Item_input[0].data_buffer[3] & 0xff) << 24) + ((Server_Item_input[0].data_buffer[2] & 0xff) << 16) + ((Server_Item_input[0].data_buffer[1] & 0xff) << 8) + (Server_Item_input[0].data_buffer[0] & 0xff);

                        Server_Item_input[0].data_buffer_size = players_to_get;
                        Server_Item_input[0].data_buffer = new byte[54 * players_to_get];
                        Recv_Data.read(Server_Item_input[0].data_buffer, 0, 54 * players_to_get);

                        Server_Item_input[0].Players_List.add_players(Server_Item_input[0].data_buffer, Server_Item_input[0].data_buffer_size);
                    } else {
                        return false;
                    }
                    break;
                case 0x0C:
                    Recv_Data.read(Server_Item_input[0].data_buffer, 0, 33);
                    break;
                case 0x0F:
                	Recv_Data.read(Server_Item_input[0].data_buffer,0,1);
                	break;
                default:
                    Recv_Data.read(Server_Item_input[0].data_buffer, 0, 4);
                    if (((Server_Item_input[0].data_buffer[3] & 0xff) << 24) + ((Server_Item_input[0].data_buffer[2] & 0xff) << 16) + ((Server_Item_input[0].data_buffer[1] & 0xff) << 8) + (Server_Item_input[0].data_buffer[0] & 0xff) >= 0) {
                        try {
                            Send_Data.flush();
                            Send_Data.close();
                            connectionsocket.close();
                        } catch (IOException e) {
                            return false;
                        }
                        return true;
                    }
                    return false;
            }
        } catch (IOException e) {
            return false;
            //e.printStackTrace();
        }

        try {
            Send_Data.flush();
            Send_Data.close();
            connectionsocket.close();
        } catch (IOException e) {
            return false;
            //e.printStackTrace();
        }
        return true;
    }
}

class ServersData {
    private ArrayList<Server_item> Links = new ArrayList<Server_item>();
    Context AppContext;
    
    private int numberoflinks = 0;

    public ArrayList<String> serverlist = new ArrayList<String>();

    networkstuff network_obj;

    public ServersData() {
    }
    
    public void Set_Context(Context appcontextin)
    {
    	AppContext = appcontextin;
    }

    public boolean send_data(int selected_server, byte command, byte[] data_args, int data_arg_size) {
        network_obj = new networkstuff();

        Server_item tempitem = Links.get(selected_server);
        tempitem.prep_data(command, data_args, data_arg_size);
        Links.set(selected_server, tempitem);

        try {
        	if(network_obj.execute(Links.get(selected_server)).get())
        	{
        		return true;
        	}
        	Toast.makeText(AppContext, "Error Connecting To Server @ " + tempitem.url + ":" + Integer.toString(tempitem.port), Toast.LENGTH_LONG).show();
			return false;
		} catch (InterruptedException e) {
			Toast.makeText(AppContext, "Error Connecting To Server @ " + tempitem.url + ":" + Integer.toString(tempitem.port), Toast.LENGTH_LONG).show();
			return false;
		} catch (ExecutionException e) {
			Toast.makeText(AppContext, "Error Connecting To Server @ " + tempitem.url + ":" + Integer.toString(tempitem.port), Toast.LENGTH_LONG).show();
			return false;
		}
    }

    public boolean Get_Pid_From_User(int selected_server) {
        return send_data(selected_server, (byte) 0x01, null, 0);
    }

    public boolean Authenticate_Player(int selected_server) {
        return send_data(selected_server, (byte) 0x02, null, 0);
    }
    
    public void Get_Game_Settings(int selected_server){
    	Server_item Temp_Item = Links.get(selected_server);
    	if(send_data(selected_server, (byte)0x0F, null, 0))
    	{
    		if((Temp_Item.data_buffer[0] & (byte)0x01) == (byte)0x01)
    		{
    			Temp_Item.AutoScanAllowed = true;
    		}
    		if((Temp_Item.data_buffer[0] & (byte)0x02) == (byte)0x02)
    		{
    			Temp_Item.ScanDetectAllowed = true;
    		}
    		if((Temp_Item.data_buffer[0] & (byte)0x04) == (byte)0x04)
    		{
    			Temp_Item.TagBackControl = true;
    		}
    		if((Temp_Item.data_buffer[0] & (byte)0x08) == (byte)0x08)
    		{
    			Temp_Item.UseGPSforplayerlist = true;
    		}
    	}
    	Links.set(selected_server, Temp_Item);
    }

    public boolean Get_Player_List(int selected_server) {
        Server_item tempitem = null;
        if (send_data(selected_server, (byte)0x03, null, 0) == false) {
            return false;
        }

        try {
            tempitem = GetLink(selected_server);
            tempitem.Players_List = new player_list_class();
            tempitem.Players_List.add_players(GetLink(selected_server).data_buffer, GetLink(selected_server).data_buffer_size);
            SetLink(selected_server, tempitem);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        return true;
    }

    public void AddLink() {
        Links.add(new Server_item());
        serverlist.add(Links.get(numberoflinks).url + ":" + Integer.toString(Links.get(numberoflinks).port));
        numberoflinks++;
    }

    public int RemoveLink(int idtoremove) {
        if (idtoremove < numberoflinks && 0 <= idtoremove) {
            Links.remove(idtoremove);
            serverlist.remove(idtoremove);
            numberoflinks--;
        } else {
            return -1;
        }
        return 0;
    }

    public Server_item GetLink(int id) {
        if (id < numberoflinks) {
            return Links.get(id);
        }
        return null;
    }

    public void SetLink(int id, Server_item input) {
        if (id < numberoflinks) {
            Links.set(id, input);
        }
        serverlist.remove(id);
        serverlist.add(id, Links.get(id).url + ":" + Integer.toString(Links.get(id).port));
    }

    public int get_number_of_links() {
        return numberoflinks;
    }

    public void set_listview_height(ListView listview) {
        ListAdapter listadapter = listview.getAdapter();
        if (listadapter == null) {
            return;
        }

        int height = 0;
        for (int i = 0; i < listadapter.getCount(); i++) {
            View listitem = listadapter.getView(i, null, listview);
            listitem.measure(0, 0);
            height += listitem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams listparams = listview.getLayoutParams();
        listparams.height = height + (listview.getDividerHeight() * (listadapter.getCount() - 1));
        listview.setLayoutParams(listparams);
    }
}

class Server_item {
    boolean authcheck;

    public String url;
    public int port;
    public String username;
    public String pin;
    public byte[] status_byte = new byte[1];
    public byte[] pid = new byte[4];
    
	boolean AutoScanAllowed;
	boolean ScanDetectAllowed;
	boolean TagBackControl;
	boolean UseGPSforplayerlist;
    
    public player_list_class Players_List;

    private byte[] user_buffer;
    private byte[] pin_buffer = new byte[2];

    int data_buffer_size = 0;
    byte[] data_buffer;

    public Server_item() {
        authcheck = false;
        url = "Enter Address";
        port = 132;
        username = "UserName";
        pin = "0123";
        status_byte[0] = (byte) 0xFF;
        Players_List = new player_list_class();
        
        AutoScanAllowed = false;
        ScanDetectAllowed = false;
        TagBackControl = false;
        UseGPSforplayerlist = false;
    }

    public boolean prep_data(byte command, byte[] data_args, int data_arg_size) {
        byte[] data_args_bytes = null;


        switch (command) {
            case 0x01:
                get_pid();
                break;
            case 0x04:
                data_buffer_size = 11;
                data_buffer = new byte[11];
                data_buffer[0] = command;
                data_buffer[1] = pid[0];
                data_buffer[2] = pid[1];
                data_buffer[3] = pid[2];
                data_buffer[4] = pid[3];
                data_buffer[5] = pin_buffer[0];
                data_buffer[6] = pin_buffer[1];
                data_buffer[7] = data_args[0];
                data_buffer[8] = data_args[1];
                data_buffer[9] = data_args[2];
                data_buffer[10] = data_args[3];

                break;
            case 0x05:
                data_buffer_size = 11;
                data_buffer = new byte[11];
                data_buffer[0] = command;
                data_buffer[1] = pid[0];
                data_buffer[2] = pid[1];
                data_buffer[3] = pid[2];
                data_buffer[4] = pid[3];
                data_buffer[5] = pin_buffer[0];
                data_buffer[6] = pin_buffer[1];
                data_buffer[7] = data_args[0];
                data_buffer[8] = data_args[1];
                data_buffer[9] = data_args[2];
                data_buffer[10] = data_args[3];
                break;
            //confirm_tag();;
            // break;
            case 0x06:
                data_buffer_size = 11;
                data_buffer = new byte[39];
                data_buffer_size = 39;
                for (int i = 0; i < 39; i++) {
                    data_buffer[i] = 0x00;
                }
                data_buffer[0] = command;
                data_buffer[1] = pid[0];
                data_buffer[2] = pid[1];
                data_buffer[3] = pid[2];
                data_buffer[4] = pid[3];
                data_buffer[5] = pin_buffer[0];
                data_buffer[6] = pin_buffer[1];

                for (int i = 0; i < data_arg_size; i++) {
                    data_buffer[i + 7] = data_args[i];
                }
                break;
            //case 0x09:
            //set_status(0);
            // break;
            // case 0x0A:
            //set_status(1);
            // break;
            //case 0x0B:
            //set_status(2);
            // break;
            case 0x0C:
                data_buffer = new byte[33];
                data_buffer[0] = command;
                data_buffer[1] = pid[0];
                data_buffer[2] = pid[1];
                data_buffer[3] = pid[2];
                data_buffer[4] = pid[3];
                data_buffer[5] = pin_buffer[0];
                data_buffer[6] = pin_buffer[1];
                for (int i = 0; i < data_arg_size; i++) {
                    data_buffer[7 + i] = data_args[i];
                }
                break;
            //case 0x0D:
            // set_status(4);
            // break;
            default:
                data_buffer = new byte[7 + data_arg_size];
                data_buffer_size = 7 + data_arg_size;
                for (int i = 0; i < data_arg_size; i++) {
                    data_buffer[i] = 0x00;
                }

                data_buffer[0] = command;
                data_buffer[1] = pid[0];
                data_buffer[2] = pid[1];
                data_buffer[3] = pid[2];
                data_buffer[4] = pid[3];
                data_buffer[5] = pin_buffer[0];
                data_buffer[6] = pin_buffer[1];
                for (int i = 0; i < data_arg_size; i++) {
                    data_buffer[7 + i] = data_args[i];
                }
                break;
        }
        return true;
    }

    private void get_pid() {
        data_buffer = new byte[33];
        data_buffer_size = 33;

        try {
            user_buffer = username.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] temp_pin_bytes = new byte[4];
        try {
            temp_pin_bytes = pin.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        temp_pin_bytes[0] = (byte) (temp_pin_bytes[0] << 4);
        temp_pin_bytes[2] = (byte) (temp_pin_bytes[2] << 4);
        temp_pin_bytes[1] = (byte) (temp_pin_bytes[1] & 0x0F);
        temp_pin_bytes[3] = (byte) (temp_pin_bytes[3] & (byte) 0x0F);

        pin_buffer[0] = (byte) (temp_pin_bytes[0] | temp_pin_bytes[1]);
        pin_buffer[1] = (byte) (temp_pin_bytes[2] | temp_pin_bytes[3]);

        data_buffer[0] = 0x01;
        for (int i = 0; i < username.length() && i <= 32; i++) {
            data_buffer[i + 1] = user_buffer[i];
        }
    }

    public boolean check_premission_for_operation(int check) {
        //takes a bool stating the flags need to check as true
        //checks those valuse against the players premissions
        //returns true if player has the premissions
        //returns false if any fails
        switch (check) {
            case 0:
                if ((status_byte[0] & (byte) 0x01) != (byte) 0x01) {
                    return false;
                }//active
                break;
            case 1:
                if ((status_byte[0] & (byte) 0x02) != (byte) 0x02) {
                    return false;
                }//it
                break;
            case 2:
                if ((status_byte[0] & (byte) 0x04) != (byte) 0x04) {
                    return false;
                }//add players
                break;
            case 3:
                if ((status_byte[0] & (byte) 0x08) != (byte) 0x08) {
                    return false;
                }//remove players
                break;
            case 4:
                if ((status_byte[0] & (byte) 0x10) != (byte) 0x10) {
                    return false;
                }//admin
                break;
            case 5:
                if ((status_byte[0] & (byte) 0x40) == (byte) 0x40) {
                    return false;
                }//logged in if 0x40 not set
        }
        return true;
    }
}

class player_list_class {
    ArrayList<player_item_class> Links;
    String[] Players;
    int player_count;

    player_list_class() {
        Links = new ArrayList<player_item_class>();
        player_count = 0;
    }

    public void add_players(byte[] data, int player_count_in) throws UnsupportedEncodingException {
        player_item_class tempitem;
        for (int i = 0; i < player_count_in; i++) {
            tempitem = new player_item_class();

            tempitem.pid[0] = data[0 + (i * 54)];
            tempitem.pid[1] = data[1 + (i * 54)];
            tempitem.pid[2] = data[2 + (i * 54)];
            tempitem.pid[3] = data[3 + (i * 54)];

            for (int i1 = 0; i1 < 32; i1++) {
                if (data[i1 + 4 + (i * 54)] != -51) {
                    tempitem.user_name[i1] = data[i1 + 4 + (i * 54)];
                } else {
                    tempitem.user_name[i1] = 0x00;
                }
            }

            tempitem.status_byte[0] = data[36 + (i * 54)];
            byte[] mac_temp = new byte[17];
            for (int imac = 0; imac < 17; imac++) {
                mac_temp[imac] = data[(37 + imac) + (i * 54)];
            }

            tempitem.mac_address = new String(mac_temp, "US-ASCII");

            Links.add(tempitem);
        }
        player_count = player_count_in;
        Players = new String[player_count];

        for (int i = 0; i < player_count; i++) {
            Players[i] = new String(Links.get(i).user_name, "US-ASCII");
        }
    }
}

class player_item_class {
    byte[] pid;
    byte[] user_name;
    byte[] status_byte;
    String mac_address;

    public String url;
    public int port;
    public int server_number;
    public String username;
    public String pin;

    private byte[] pin_buffer = new byte[2];

    player_item_class() {
        pid = new byte[4];
        user_name = new byte[32];
        status_byte = new byte[1];
    }
}

/*
class BlueTooth_Com {
	private static final UUID Server_UUID = UUID.fromString("466F7854-656B-4675-7272-79344C696665");
	
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
    
    InetAddress ipstr;
    
    public BlueTooth_Com() {
		
	}

    //Bluetooth Functions
    public String getBtMac() {
        return mBTAdapter.getAddress();
    }

    public void BTServer(ServersData Servers_to_use) throws InterruptedException {
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
                }
            }

            send_raw(Data_Out, 5);
            showNotification("Scan Detected", "", 3);
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
    
    public void Auto_Scan(ServersData Servers_to_use, settings_obj settings_to_use)
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
	                 if (Servers_to_use.GetLink(i).check_premission_for_operation(1)) {

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
    
    public List<String> Scan(ServersData Servers_to_use)
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
    
    public boolean Tag(ServersData Servers_to_use, int selected_player)
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
}*/

