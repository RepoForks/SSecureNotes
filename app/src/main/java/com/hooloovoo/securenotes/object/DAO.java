package com.hooloovoo.securenotes.object;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class DAO {
	private static DAO instance = null;
	final static String PASSWORDFILE = "passwordfile";
    public final static String FOLDER = "/SecureNotesDatabase/";
	 private SQLiteDatabase database;
	 private SecureDatabaseHelper dbHelper;
	 private String[] notes_columns = {SecureDatabaseHelper.ID_NOTE,SecureDatabaseHelper.TITLE_NOTE,
			 SecureDatabaseHelper.TEXT_NOTE,SecureDatabaseHelper.DATA_NOTE, SecureDatabaseHelper.IMG_NOTE}; 
	  
	private DAO(Context context) {
		dbHelper = new SecureDatabaseHelper(context);
	}
	
	public static DAO getInstance(Context context){
		if(instance == null) instance = new DAO(context);
		return instance;
	}
	
	public void openDB() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	public void closeDB() {
	    dbHelper.close();
	}
	
	public List<Note> getAllNotes(){
		List<Note> notes = new ArrayList<Note>();
		
		Cursor cursor = database.query(SecureDatabaseHelper.NOTES_TABLE_NAME, notes_columns, 
				null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Note nota = cursorToNote(cursor);
			notes.add(nota);
			cursor.moveToNext();
		}
		cursor.close();
		return notes;
	}
	
	public long addNoteToDB(Note note, boolean toReinsert){
		ContentValues value = new ContentValues();
		if(toReinsert) value.put(SecureDatabaseHelper.ID_NOTE, note.getmId());
		value.put(SecureDatabaseHelper.TITLE_NOTE, note.getmName());
		value.put(SecureDatabaseHelper.TEXT_NOTE, note.getmDesc());
		value.put(SecureDatabaseHelper.DATA_NOTE, note.getmDataString());
		value.put(SecureDatabaseHelper.IMG_NOTE, note.getmImage());
		return database.insert(SecureDatabaseHelper.NOTES_TABLE_NAME, null, value);
	}
	
	public int updateNoteToDB(Note note){
		ContentValues value = new ContentValues();
		value.put(SecureDatabaseHelper.TITLE_NOTE, note.getmName());
		value.put(SecureDatabaseHelper.TEXT_NOTE, note.getmDesc());
		value.put(SecureDatabaseHelper.DATA_NOTE, note.getmDataString());
		value.put(SecureDatabaseHelper.IMG_NOTE, note.getmImage());
		return database.update(SecureDatabaseHelper.NOTES_TABLE_NAME,
				value,"id = "+ note.getmId(),null);
	}
	
	public int removeFromDB(Note toRemove){
		return database.delete(SecureDatabaseHelper.NOTES_TABLE_NAME, "id = " + toRemove.getmId(), null);
	}

	public static void writeFilePassword(Context context, byte[] mHashdata) {

		/*String ddd = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+ System.getProperty("line.separator")
					+"<password>" + System.getProperty("line.separator")
					+"<value>"+hashdata+"</value>"+ System.getProperty("line.separator")
					+"<setted>1</setted>" +  System.getProperty("line.separator")
					+"</password>";*/
				
		try {
			FileOutputStream fOut = context.openFileOutput(PASSWORDFILE, Context.MODE_PRIVATE);
			//OutputStreamWriter out = new OutputStreamWriter(fOut);
			fOut.write(mHashdata);
            Log.d("WRITED FILE",mHashdata.toString());
		    fOut.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException ioex){
			ioex.printStackTrace();
		}
		
	}

    public static void writeFilePassword(Context context, String mHashdata) {

		/*String ddd = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+ System.getProperty("line.separator")
					+"<password>" + System.getProperty("line.separator")
					+"<value>"+hashdata+"</value>"+ System.getProperty("line.separator")
					+"<setted>1</setted>" +  System.getProperty("line.separator")
					+"</password>";*/

        try {
            FileOutputStream fOut = context.openFileOutput(PASSWORDFILE, Context.MODE_PRIVATE);
            OutputStreamWriter out = new OutputStreamWriter(fOut);
            out.write(mHashdata);
            Log.d("WRITED FILE",mHashdata);
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException ioex){
            ioex.printStackTrace();
        }

    }
	/*
	public static String[] readFilePassword(Context context){
		String password[]= new String[2];
		XMLParser parser = new XMLParser(context);
		try {
			password = parser.parsePassword(PASSWORDFILE);
			//Log.d("PasswordTrovata",password);
		} catch (XmlPullParserException e) {
			Log.e("ParseXMLMAIN","XMLParse EX");
			e.printStackTrace();
		}catch (FileNotFoundException fnex){
			Log.e("ParseXMLMAIN","FileNotFound EX");			
		}catch (IOException e) {
			Log.e("ParseXMLMAIN", "IO EX");
			e.printStackTrace();
		}catch(Exception ex){
			
		}
		return password;
	}*/

    public static String readFilePassword(Context context,float originale){
        byte[] bytes = null;
        try {

            FileInputStream fIn = context.openFileInput(PASSWORDFILE);
            int size = DAO.fileLenght(fIn);
            bytes = new byte[size];
            InputStreamReader in = new InputStreamReader(fIn);
            fIn.read(bytes,0,size);
            fIn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new String(bytes);

    }

    public static String readFilePassword(Context context){
        byte[] bytes = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {

            FileInputStream fIn = context.openFileInput(PASSWORDFILE);
            br = new BufferedReader(new InputStreamReader(fIn));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();

    }




    static public byte[] readFilePassword(Context context, int i){


        byte[] bytes = null;
        try {

            FileInputStream fIn = context.openFileInput(PASSWORDFILE);
            int size = DAO.fileLenght(fIn);
            bytes = new byte[size];
            fIn.read(bytes,0,size);
            fIn.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }

    public String exportDB(Context context, String filename) throws IOException{
        //path into export db
        final String dirIntoExport = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ FOLDER;

        File root = Environment.getExternalStorageDirectory();
        Log.d("DAO","External storage: "+root);
        File dir = new File(root.getAbsolutePath(),FOLDER);
        dir.mkdir();
        //file to store
        File fileToStore = new File(dir,filename);

        //creiamo xml serializer
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        xmlSerializer.setOutput(writer);
        //start document
        xmlSerializer.startDocument("UTF-8",true);
        for(Note note:this.getAllNotes()){
            //tag <note>
            xmlSerializer.startTag("","note");
            xmlSerializer.attribute("","id",""+note.getmId());

            // open tag: <name>
            xmlSerializer.startTag("", "name");
            xmlSerializer.text(note.getmName());
            // close tag: </name>
            xmlSerializer.endTag("", "name");

            // open tag: <desc>
            xmlSerializer.startTag("", "desc");
            xmlSerializer.text(note.getmDesc());
            // close tag: </desc>
            xmlSerializer.endTag("", "desc");

            // open tag: <data>
            xmlSerializer.startTag("", "data");
            xmlSerializer.text(note.getmDataString());
            // close tag: </data>
            xmlSerializer.endTag("", "data");

            // open tag: <img>
            xmlSerializer.startTag("", "img");
            if(note.getImageLen()>1){
                xmlSerializer.text(new String(note.getmImage()));
            }else{
                xmlSerializer.text("nonono");
            }
            // close tag: </img>
            xmlSerializer.endTag("", "img");

            //close tag </note>
            xmlSerializer.endTag("","note");
        }
        xmlSerializer.endDocument();

        //scrivo il file
        FileOutputStream fOut = new FileOutputStream(fileToStore);
        OutputStreamWriter out = new OutputStreamWriter(fOut);
        out.write(writer.toString());
        out.close();

        return dir.toString();

    }

    public ArrayList<Note> importNotesFromFile(Context context, String filename) throws XmlPullParserException,FileNotFoundException,IOException,ParseException{
        XMLParser parser = new XMLParser(context);
        ArrayList<Note> dataToImport =  parser.parseDB(filename);
        return dataToImport;
    }

    public static boolean isExternalStorageReadable(){
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        return mExternalStorageAvailable && mExternalStorageWriteable;
    }

    static private int fileLenght(FileInputStream fIn) throws IOException {
        int count = 0;
        while(fIn.read() != -1) count++;
        return count;
    }
	private Note cursorToNote(Cursor cursor){
		Note nota;
		try {
			nota = new Note(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getBlob(4).length,cursor.getBlob(4));
		} catch (ParseException e) {
			nota= new Note(0, "error", "error", new Date(),1,new byte[1]);
			e.printStackTrace();
		}
		return nota;
	}

}
