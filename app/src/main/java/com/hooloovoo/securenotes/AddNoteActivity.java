package com.hooloovoo.securenotes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.hooloovoo.securenotes.object.Note;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class AddNoteActivity extends Activity {
	private final static int CAMERA_PIC_REQUEST = 1337;
	private final static int PIC_CROP_REQUEST = 1338;

    SharedPreferences mSharedPreferences;
    int seconds;

	private Note newNote;
	EditText titolo;
	EditText text;
	ImageView image;
    ImageButton button;
    LinearLayout listView;
    View mView;
    boolean openListView;
    ValueAnimator mAnimator;

    Bitmap mBitmap;
	String mFileImagePath;
	boolean imgToCompress;
	byte[] imgCompressed;
	Uri outputFileUri;
	
	String mTitolo;
	String mText;

    boolean toDestroy;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_note);
		setNavigationBar();
		setLayout();
        toDestroy = false;
		Log.d("ADDNOTEACTIVITY","Start Add noteactivity");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_add_note, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		
		case R.id.action_add_img:
			Log.d("menu", "action_add_img");
            toDestroy = false;
			startCameraActivity();
			break;
		case R.id.action_save_note:
			Log.d("menu", "action_save_note");
			int esito = (createNote())?RESULT_OK:RESULT_CANCELED;
			finishThisActivity(esito);
			break;
		default:
            return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		//creo file
		final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/"; 
	    File newdir = new File(dir); 
	    newdir.mkdirs();
	    mFileImagePath = dir+"file.jpg";
	        
		if(mBitmap != null) image.setImageBitmap(mBitmap);
		
		if(mTitolo != null) titolo.setText(mTitolo);
		if(mText != null) text.setText(mText);
	}


    @Override
	public void onPause(){
		super.onPause();
		//delete file if exists
    	File f = new File(mFileImagePath);
    	if(f.exists()) f.delete();
    	
    	if(titolo.length()>0){
	    	mTitolo = titolo.getText().toString();
	    	titolo.getText().clear();
    	}
    	if(text.length() > 0){
    		mText = text.getText().toString();
    		text.getText().clear();
    	}

        //if (toDestroy) setTimeFinish();
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {  
			BitmapFactory.Options op = new BitmapFactory.Options();
            toDestroy = true;
			op.inSampleSize = 2;
	    	mBitmap = BitmapFactory.decodeFile(mFileImagePath,op);
            imgToCompress = true;
            //image.setImageBitmap(mBitmap);
            setmBitmapImage(mBitmap,true);


	    }  else if(requestCode == PIC_CROP_REQUEST && resultCode == RESULT_OK){
	    	Bundle extras = data.getExtras();
	    	mBitmap = extras.getParcelable("data");
	    	image.setImageBitmap(mBitmap);
	    	

//
	    }
	}
	
	
	 
	private void setNavigationBar(){
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void setLayout(){
        openListView = true;
		titolo = (EditText) findViewById(R.id.editText_titolo_addnote);
		text = (EditText) findViewById(R.id.editText_text_addnote);
		image = (ImageView) findViewById(R.id.imageView1_addnote);
        listView = (LinearLayout) findViewById(R.id.popup_window);
        mView = (View) findViewById(R.id.trasparent_view);
        mView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);



        button = (ImageButton) findViewById(R.id.imageButton_addnote);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(openListView){

                    listView.startAnimation(getAnimation(0.0f,-1.0f));
                    listView.setVisibility(View.GONE);
                    if(image.getVisibility()==View.VISIBLE){
                        mView.startAnimation(getAnimation(0.0f,-1.0f));
                        mView.setVisibility(View.GONE);
                    }
                    openListView = false;
                }else{
                    listView.startAnimation(getAnimation(-1.0f,0.0f));
                    listView.setVisibility(View.VISIBLE);
                    if(image.getVisibility()==View.VISIBLE) {
                        mView.startAnimation(getAnimation(-1.0f,0.0f));
                        mView.setVisibility(View.VISIBLE);
                    }
                    openListView = true;
                }
            }
        });

		
		Bundle bun;
		mBitmap = null;
        boolean exiImg = false;
		if((bun = getIntent().getExtras())!=null){
			newNote = (Note) bun.getParcelable("noteToUpdate");
			titolo.setText(newNote.getmName());
			text.setText(newNote.getmDesc());

			//creo immagine da byte[]
			if(newNote.getmImage().length != 1){
				imgCompressed = newNote.getmImage();
				Log.d("ImageLeght--3", ""+newNote.getmImage().length);
				//mBitmap = (Bitmap) bun.getParcelable("BitmapToUpdate");  
				/*if (mBitmap == null) */mBitmap = BitmapFactory.decodeByteArray(imgCompressed, 0, imgCompressed.length);
				imgToCompress = false;
                exiImg = true;
				if(mBitmap == null) Log.e("BITMAP LOAD","NULL");
				
			}
			
		}
		setmBitmapImage(mBitmap,exiImg);

		
	}


    private void setmBitmapImage(Bitmap toAdd, boolean visible){
        if(visible){
            image.setVisibility(View.VISIBLE);
            mView.setVisibility(View.VISIBLE);
        }else{
            image.setVisibility(View.GONE);
            mView.setVisibility(View.GONE);
        }
        image.setImageBitmap(toAdd);
    }


    private TranslateAnimation getAnimation(float first,float second){
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, first, Animation.RELATIVE_TO_SELF, second
        );
        animation.setDuration(500);
        return animation;
    }



	
	private boolean createNote(){
		mTitolo = titolo.getText().toString();
		mText = text.getText().toString();
		
		//estraggo img
//		if(mBitmap == null) Log.d("ADDNOTEACTIVITY", "BITMA NULLNULL");
		if(mBitmap != null && imgToCompress){		
			Log.d("IMAGE DRAWABLE", mBitmap.toString());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			
			try {
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
				imgCompressed = stream.toByteArray(); 
				stream.close();
				Log.d("COMPRESSION IMAGE", ""+imgCompressed.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				//stream.close();
			}
		}else if(mBitmap == null){
			imgCompressed = new byte[1];
			Log.d("COMPRESSION IMAGE", "no image");

		}else{
			Log.d("COMPRESSION IMAGE", "gia' compressa");
		}
		
		
		if( (!mTitolo.equals("")) || (!mText.equals("")) || (imgCompressed.length > 1)){
			int id = (newNote==null)? 0 : newNote.getmId();
			newNote = new Note(id, mTitolo,mText, new Date(),imgCompressed.length, imgCompressed);	
			return true;
		}else{
			return false;
		}
	}
	
	
	
	private void finishThisActivity(int esito){
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putParcelable("note", newNote);
		//bundle.putParcelable("bitmapNote", compressBitmap(mBitmap));
		intent.putExtras(bundle);
		setResult(esito,intent);  
		finish();

	}
	
	private Bitmap compressBitmap(Bitmap toCompress){
		Bitmap compressedBitmap;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try{
			toCompress.compress(Bitmap.CompressFormat.JPEG, 70, stream);
			byte[] byteC = stream.toByteArray();
			stream.close();
			compressedBitmap = BitmapFactory.decodeByteArray(byteC, 0, byteC.length);
		}catch(IOException ex){
			ex.printStackTrace();
			compressedBitmap = null;
		}
		return compressedBitmap;
	}
	
	private void startCameraActivity(){
		
	     File newfile = new File(mFileImagePath);
	     try {
	         newfile.createNewFile();
	     } catch (IOException e) {
	    	 Log.e("FILE CREATES", "errore");
	     }       

	     outputFileUri = Uri.fromFile(newfile);
	         

	    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
	    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	}
	
	private void performCrop(){
		try {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			//call the standard crop action intent (the user device may not support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			    //indicate image type and Uri
			cropIntent.setDataAndType(outputFileUri, "image/*");
			    //set crop properties
			cropIntent.putExtra("crop", "true");
			    //indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 4);
			cropIntent.putExtra("aspectY", 3);
			    //indicate output X and Y
			cropIntent.putExtra("outputX", width);
			cropIntent.putExtra("outputY", width);
			    //retrieve data on return
			cropIntent.putExtra("return-data", true);
			    //start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, PIC_CROP_REQUEST);
		}catch(ActivityNotFoundException anfe){
		    //display an error message
		    String errorMessage = "Whoops - your device doesn't support the crop action!";
		    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
		    toast.show();
		}
	}

    public Timer setTimeFinish(){
        final Timer t;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int endSeconds = Integer.parseInt(mSharedPreferences.getString("secondWaitToFinish", "10"));
        Log.d("NOTESACTIVITY", "Second to wait: " + endSeconds);
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if( seconds == endSeconds ){

                            t.cancel();
                            t.purge();
                            seconds = 0;
                            finishAffinity();
                        }
                        seconds += 1;
                    }
                });

            }
        }, 0, 1000);

        return t;



    }


	

}
