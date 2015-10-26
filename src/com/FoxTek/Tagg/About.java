package com.FoxTek.Tagg;

import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class About extends Activity {
    ArrayList<String> about = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ListView aboutlist = (ListView) findViewById(R.id.aboutlistview);
        about.add("Tagg Client V 0.5.2 (RC)");
        about.add("Min Required Tagg Server V 0.3.8 (RC)");
        about.add("Included Tagg Server V 0.3.8 (RC)");
        about.add("Max Compatible Server V 0.3.8 (RC)");
        about.add("Coded By:Royce L Whetstine");
        about.add("Email:ModdingFox@gmail.com");
        about.add("----------------------------------");
        about.add("Issues To Deal With:");
        about.add("Tests Show 22% Drain On Battery In 9 Hours");
        about.add("BlueTooth Scans Need To Be Faster");
        about.add("----------------------------------");
        about.add("ToDo:");
        about.add("Adding Players To The Game Using BlueTooth");
        about.add("Optional GPS Tracking For Decreased Scan Times Not Yet Implimented");
        about.add("----------------------------------");
        about.add("Testers:");
        about.add("DarkKnight99 (Pre-Alpha+)");
        about.add("HydroRose (Alpha+)");
        about.add("chadfish (Early-Beta+)");
        about.add("guerrilla (Early-Beta+)");
        about.add("toridelvaux (Late-Beta+)");
        about.add("If you where a tester and are not listed here you need to contact contact me");
        //about.add("bowtieteeban");
        //about.add("jaemealz");
        //about.add("jaydenduvall");
        //about.add("koddieduvall");
        //about.add("tinkerbellluvs");
        about.add("----------------------------------");
        
        aboutlist.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, about));

        aboutlist.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int selectedpos,
                                    long arg3) {
                if(selectedpos == 2)
                {
                    try {
                        File dest = Environment.getExternalStorageDirectory();
                        FileOutputStream outputwriter = new FileOutputStream(new File(dest, "Tagg_Server.exe"));
                        InputStream inputreader = getResources().openRawResource(R.raw.taggconsoleserver);

                        byte[] buffer = new byte[1024];
                        int read = 0;
                        while(read != -1)
                        {
                            try {
                                read = inputreader.read(buffer,0,1024);
                                if(read != -1)
                                {
                                outputwriter.write(buffer,0,read);
                                }
                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(),"Error Creating Server File",Toast.LENGTH_LONG).show();
                            }

                        }

                        try {
                            outputwriter.flush();
                            outputwriter.close();
                            inputreader.close();
                        } catch (IOException e) {
                        }

                        Toast.makeText(getApplicationContext(),"Done! Server Is now on your sdcard",Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                    }
                }
            }
        });
    }
}
