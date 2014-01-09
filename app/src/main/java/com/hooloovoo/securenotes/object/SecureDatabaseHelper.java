package com.hooloovoo.securenotes.object;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SecureDatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "notes.db";
	private static final int DB_VERSION = 1;
	//notes table
	public static final String NOTES_TABLE_NAME = "notes";
	public static final String ID_NOTE = "id";
	public static final String TITLE_NOTE = "title";
	public static final String TEXT_NOTE = "note_text";
	public static final String DATA_NOTE = "note_date";
	public static final String IMG_NOTE = "image";
	
	//create note table
	private static final String TABLE_NOTE_CREATE= "create table  " +
			NOTES_TABLE_NAME+" ("+ID_NOTE + " integer primary key , " +
					TITLE_NOTE+" text not null, "+ TEXT_NOTE + " text not null, " +
					DATA_NOTE+" text not null, "+ IMG_NOTE + " blob );";
	
	
	
	
	public SecureDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);		
	}

	

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d("TableCreations",TABLE_NOTE_CREATE);
		database.execSQL(TABLE_NOTE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int old_version, int new_version) {
		Log.w(SecureDatabaseHelper.class.getName(),
		        "Upgrading database from version " + old_version + " to "
		            + new_version + ", which will destroy all old data");
		    database.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
		    onCreate(database);

	}

}
