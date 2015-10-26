package com.FoxTek.Tagg;

import java.io.IOException;
import java.util.ArrayList;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RingToneSelector extends Activity {
	ListView RingTone_List;
	
	settings_obj settings_to_us = new settings_obj();
	fileiostuff file_io_to_use = new fileiostuff();
	MediaPlayer PreviewMp = new MediaPlayer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ring_tone_selector);
		
		RingTone_List = (ListView) findViewById(R.id.listView1);
		
		Get_RingTones();
		
		RingTone_List.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				play_tone(arg2);
			}
		});
		
		RingTone_List.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Set_RingTone(arg2);
				return false;
			}
		});
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		file_io_to_use.Load_Settings(settings_to_us, getApplicationContext());
		Toast.makeText(getApplicationContext(), "Short Press To Preview. Long Press To Select", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(PreviewMp.isPlaying())
		{
			PreviewMp.stop();
			PreviewMp = new MediaPlayer();
		}
	}
	
	///functional stuff
	ArrayList<String> ringtonenames;
	
	public void Set_RingTone(int selectedfile)
	{
		RingtoneManager ringmgr = new RingtoneManager(getApplicationContext());
		Cursor ringlist;
		
		ringmgr.setType(RingtoneManager.TYPE_ALL);
		ringlist = ringmgr.getCursor();
		
		ringlist.moveToPosition(selectedfile);
		
		Bundle extras = getIntent().getExtras();
		
		switch(extras.getInt("Ring Location"))
		{
		case 0:
			settings_to_us.AutoScan_Sound = ringmgr.getRingtoneUri(ringlist.getPosition()).toString();
			break;
		case 1:
			settings_to_us.Tagged_Sound = ringmgr.getRingtoneUri(ringlist.getPosition()).toString();
			break;
		case 2:
			settings_to_us.AutoScan_Hit_Sound = ringmgr.getRingtoneUri(ringlist.getPosition()).toString();
			break;
		default:
			Toast.makeText(getApplicationContext(), "Error Setting RingTone", Toast.LENGTH_LONG).show();
		}
		
		file_io_to_use.Save_Settings(settings_to_us, getApplicationContext());
		finish();	
	}
	
	public void Get_RingTones()
	{
		RingtoneManager ringmgr = new RingtoneManager(getApplicationContext());
		Cursor ringlist;
		
		ringtonenames = new ArrayList<String>();
		
		ringmgr.setType(RingtoneManager.TYPE_ALL);
		ringlist = ringmgr.getCursor();
		
		while(!ringlist.isAfterLast() && ringlist.moveToNext())
		{
			ringtonenames.add(ringmgr.getRingtone(ringlist.getPosition()).getTitle(getApplicationContext()));
		}
		
		RingTone_List.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ringtonenames));
		
		ringlist.close();
	}
	
	public void play_tone(int selectedfile)
	{		
		RingtoneManager ringmgr = new RingtoneManager(getApplicationContext());
		Cursor ringlist;
		
		ringmgr.setType(RingtoneManager.TYPE_ALL);
		ringlist = ringmgr.getCursor();
		
		ringlist.moveToPosition(selectedfile);
		
		if(PreviewMp.isPlaying())
		{
			PreviewMp.stop();
			PreviewMp = new MediaPlayer();
		}
		
		PreviewMp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		try {
			PreviewMp.setDataSource(getApplicationContext(), ringmgr.getRingtoneUri(ringlist.getPosition()));
		} catch (IllegalArgumentException e) {}
		catch (SecurityException e) {}
		catch (IllegalStateException e) {}
		catch (IOException e) {}
		
		try {
			PreviewMp.prepare();
		} catch (IllegalStateException e) {}
		catch (IOException e) {}
		
		PreviewMp.start();
	}
}
