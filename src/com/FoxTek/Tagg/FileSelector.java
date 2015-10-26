package com.FoxTek.Tagg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FileSelector extends Activity {

	ListView FileListView;
	
	settings_obj settings_to_us = new settings_obj();
	fileiostuff file_io_to_use = new fileiostuff();
	MediaPlayer PreviewMp = new MediaPlayer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_select);
		
		FileListView = (ListView) findViewById(R.id.listView1);
		
		if(Environment.MEDIA_MOUNTED.equals(state))
		{
			Locations.add(new File(Environment.getExternalStorageDirectory().toString()));
			List_Files();
		}
		else
		{
			
		}
		
		FileListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FileNames));
		
		FileListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				play_mp3(arg2);
			}
		});
		
		FileListView.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				SelectFile(arg2);
				return false;
			}
		});
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		file_io_to_use.Load_Settings(settings_to_us, getApplicationContext());
		Toast.makeText(getApplicationContext(), "Short Press To Preview/Select Directory. Long Press To Select/Select Directory", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		if(PreviewMp.isPlaying())
		{
			PreviewMp.stop();
		}
	}
	
	//procedure stuff
		private String state = Environment.getExternalStorageState();
		
		private ArrayList<File> Locations = new ArrayList<File>();
		private File[] Current_Files;
		
		private ArrayList<File> Filtered_Files = new ArrayList<File>();
		private ArrayList<String> FileNames = new ArrayList<String>();
		
	public void play_mp3(int selectedfile)
	{		
		if(PreviewMp.isPlaying())
		{
			PreviewMp.stop();
			PreviewMp = new MediaPlayer();
		}
		
		if(Filtered_Files.get(selectedfile) == null || Filtered_Files.get(selectedfile).isDirectory())
		{
			SelectFile(selectedfile);
			return;
		}
		
		PreviewMp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		try {
			PreviewMp.setDataSource(Filtered_Files.get(selectedfile).getPath());
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
	
	public void SelectFile(int selectedfile)
	{
		if(selectedfile == 0 && (Locations.size() - 1) > 0)
		{
			Locations.remove(Locations.size() - 1);
		}
		else if(Filtered_Files.get(selectedfile).isFile())
		{
			Bundle extras = getIntent().getExtras();
			
			switch(extras.getInt("File Location"))
			{
			case 0:
				settings_to_us.AutoScan_Sound = Filtered_Files.get(selectedfile).getPath();
				break;
			case 1:
				settings_to_us.Tagged_Sound = Filtered_Files.get(selectedfile).getPath();
				break;
			case 2:
				settings_to_us.AutoScan_Hit_Sound = Filtered_Files.get(selectedfile).getPath();
				break;
			default:
				Toast.makeText(getApplicationContext(), "Error Setting File Location", Toast.LENGTH_LONG).show();
				break;
			}
			
			file_io_to_use.Save_Settings(settings_to_us, getApplicationContext());
			finish();		
		}
		else if(Filtered_Files.get(selectedfile).isDirectory())
		{
			Locations.add(Filtered_Files.get(selectedfile));
		}
		
		List_Files();
	}
	
	public void List_Files()
	{
		Filtered_Files.clear();
		FileNames.clear();
		
		Current_Files = Locations.get(Locations.size() - 1).listFiles();
		
		if((Locations.size() - 1) > 0)
		{
			Filtered_Files.add(null);
			FileNames.add("<- Go Back <-");
		}
		
		for(File inFile : Current_Files)
		{	
			if(inFile.isFile())
			{
				if(inFile.getName().contains(".mp3"))
				{
					Filtered_Files.add(inFile);
					FileNames.add(inFile.getName());
				}
			}
			else
			{
				Filtered_Files.add(inFile);
				FileNames.add(inFile.getName());
			}
		}
		
		Current_Files = null;
		
		FileListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FileNames));
	}
}
