package com.hooloovoo.securenotes.object;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class XMLParser {
	XmlPullParserFactory pullParserFactory;
	XmlPullParser parser;
	
	Context  mContext;
	
	public XMLParser(Context context) {
		mContext = context;
		try{
			pullParserFactory = XmlPullParserFactory.newInstance();
			parser = pullParserFactory.newPullParser();

	            
		}catch(XmlPullParserException pex){
			Log.e("ParseXML", "Errore XMLException");
			
		}
	}
	
	public String[] parsePassword(String file) throws XmlPullParserException,FileNotFoundException,IOException{

	    InputStream in_s = mContext.openFileInput(file);
	    
	    /////////////////////////////////////////////////////
	    String ret = "";
	    InputStreamReader inputStreamReader = new InputStreamReader(in_s);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString = "";
        StringBuilder stringBuilder = new StringBuilder();
         
        while ( (receiveString = bufferedReader.readLine()) != null ) {
            stringBuilder.append(receiveString);
        }
         
        
        ret = stringBuilder.toString();
        Log.d("FILE FILE", ret);
        ///////////////////////////////////////////////////////
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        
//        parser.setInput(in_s, null);
        parser.setInput(new StringReader(ret));
		int eventType = parser.getEventType();
		String password[] = new String[2];
		while(eventType != XmlPullParser.END_DOCUMENT){
			String tag = null;
			switch (eventType){
			case XmlPullParser.START_DOCUMENT:
				
				break;
			case XmlPullParser.START_TAG:
				tag = parser.getName();
				if(tag != null) Log.d("ParseXML", tag); 
				if(tag.equals( "password")){
				Log.d("ParseXML", "Password rilevata");
				}else if(tag.equals("value")){
					password[0] = parser.nextText();
				}else{
					password[1] = parser.nextText();
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			}
			eventType = parser.next();
		}
				
		return password;
	}

    public ArrayList<Note> parseDB(String file) throws XmlPullParserException,FileNotFoundException,IOException,ParseException{
        //definisco inputstream
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath(),DAO.FOLDER);
        File fileToImport = new File(dir,file);
        FileInputStream fIn = new FileInputStream(fileToImport);

        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(fIn,null);

        ArrayList<Note> dataToImport = new ArrayList<Note>();

        //campi utili per la nota
        String nome = "";
        String desc = "";
        String data = "";
        String img;
        byte[] imgByte = new byte[1];

        int eventType = parser.getEventType();
        while (eventType!= XmlPullParser.END_DOCUMENT){
            String tag = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    tag = parser.getName();
                    if(tag != null) Log.d("XMLPARSER",tag);
                    if(tag.equals("note")){
                        Log.d("XMLPARSER", "nuovanota");
                    }else if(tag.equals("name")){
                        nome = parser.nextText();
                    }else if (tag.equals("desc")){
                        desc = parser.nextText();
                    }else if (tag.equals("data")){
                        data = parser.nextText();
                    }else if(tag.equals("img")){
                        if((img = parser.nextText()).equals("nonono")){
                            imgByte = new byte[1];
                        }else{
                            imgByte = img.getBytes();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tag = parser.getName();
                    if(tag != null) Log.d("XMLPARSER",tag);
                    if(tag.equals("note")){
                        //creiamo nuova nota
                        dataToImport.add(new Note(0,nome,desc,data,imgByte.length,imgByte));
                    }
                    break;


            }
            eventType = parser.next();

        }

        return dataToImport;


    }

}
