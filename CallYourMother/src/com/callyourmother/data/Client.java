package com.callyourmother.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.*;

public class Client extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "callyourmother.db";
	private static final int DATABASE_VERSION = 1;
	private Context mContext;
	
	public Client(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.beginTransaction();

			//read create definition from assets
			AssetManager assetManager = mContext.getAssets();
			InputStream inputStream = assetManager.open(String.format("%1$s", File.separator, DATABASE_VERSION), AssetManager.ACCESS_BUFFER);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		    StringBuilder sql = new StringBuilder();
		    String line;
		    while ((line = reader.readLine()) != null) {
		        sql.append(line);
		    }			
			
			//run create database SQL
		    db.execSQL(sql.toString());
			
			db.setTransactionSuccessful();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			db.close();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
