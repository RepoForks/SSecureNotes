package com.hooloovoo.securenotes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.ActionItemTarget;
import com.espian.showcaseview.targets.ViewTarget;
import com.hooloovoo.securenotes.object.Note;
import com.hooloovoo.securenotes.object.PasswordPreference;
import com.hooloovoo.securenotes.object.SingletonParametersBridge;
import com.hooloovoo.securenotes.widget.UndoBarController;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class AddNoteActivity extends Activity implements UndoBarController.UndoListener{
	private final static int CAMERA_PIC_REQUEST = 1337;
	private final static int PIC_CROP_REQUEST = 1338;

    SharedPreferences mSharedPreferences;
    int seconds;

	private Note newNote;
	EditText titolo;
	EditText text;
	ImageView image;
    ImageButton button;
    ImageButton deleteImg;
    LinearLayout listView;
    View mView;
    TextView data;
    boolean openListView;

    UndoBarController mUndoBarController;

    Bitmap mBitmap;
	String mFileImagePath;
	boolean imgChanged;
	byte[] imgCompressed;
	Uri outputFileUri;
	
	String mTitolo;
	String mText;
    String mData;

    //this boolean say whether onPause is caused from camera Activity or not
    boolean cameraApp = false;
    boolean sameApp = false;
    boolean layout_setted = false;

    Typeface font;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        //Inzialize font
        font = Typeface.createFromAsset(getAssets(), "fonts/EarlyGameBoy.ttf");
		//setNavigationBar();

        if(imgCompressed == null) imgCompressed = new byte[1];
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
            cameraApp = true;
			startCameraActivity();
			break;
		case R.id.action_save_note:
			Log.d("menu", "action_save_note");
            sameApp = true;
			int esito = (createNote())?RESULT_OK:RESULT_CANCELED;
            deleteFileImage();
			finishThisActivity(esito);
			break;
        case android.R.id.home:
            sameApp = true;
            NavUtils.navigateUpFromSameTask(this);
            break;
		default:
            return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
        sameApp = true;
	}
	
	@Override
	public void onResume(){
		super.onResume();

        if(!layout_setted){
            PasswordPreference preference = new PasswordPreference(getApplicationContext());
            if(preference.isAppLocked()){
                setContentView(R.layout.locked_app_layout);
                Typeface font = Typeface.createFromAsset(getAssets(), "fonts/EarlyGameBoy.ttf");
                ((TextView)findViewById(R.id.app_locked_title_dialog)).setTypeface(font);
                ((Button)findViewById(R.id.button_unlock_app)).setTypeface(font);
                ((TextView)findViewById(R.id.editText_password_locked_app)).setTypeface(font);
                setLockedLayout();
            }else{
                setContentView(R.layout.activity_add_note);
                setLayout();
                populateLayout();
                restoreSituation();
            }
        }
	}


    @Override
	public void onPause(){
		super.onPause();
		//delete file if exists

    	
    	if(titolo.length()>0){
	    	mTitolo = titolo.getText().toString();
	    	titolo.getText().clear();
    	}
    	if(text.length() > 0){
    		mText = text.getText().toString();
    		text.getText().clear();
    	}

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean toLock = mSharedPreferences.getBoolean("lockapponpause",false);
        if(!cameraApp && !sameApp && toLock){
            //lockAPP
            Log.d("NOTEACTIVITY", "lockApp");
            PasswordPreference preference = new PasswordPreference(getApplicationContext());
            preference.setLockedPassword(true);
        }
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
        cameraApp = false;
        Log.d("ADDNOTEACTIVITY", "onActivityResult");
        setContentView(R.layout.activity_add_note);
        setLayout();
        restoreSituation();
        layout_setted = true;
		if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            Log.d("ADDNOTEACTIVITY", "onActivityResult");
			BitmapFactory.Options op = new BitmapFactory.Options();
            cameraApp = false;
			op.inSampleSize = 2;
	    	mBitmap = BitmapFactory.decodeFile(mFileImagePath,op);
            imgCompressed = compressBitmap(mBitmap);
            //image.setImageBitmap(mBitmap);
            setmBitmapImage(mBitmap,true);
            //if bitmap is changed
            imgChanged = true;
            try{
                SingletonParametersBridge.getInstance().addParameter("nota:"+newNote.getmId(),Boolean.valueOf(imgChanged));
            }catch(NullPointerException ex){//do nothing
            }


	    }  else if(requestCode == PIC_CROP_REQUEST && resultCode == RESULT_OK){
	    	Bundle extras = data.getExtras();
	    	mBitmap = extras.getParcelable("data");
	    	image.setImageBitmap(mBitmap);
	    	

//
	    }
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("titolo",mTitolo);
        outState.putCharSequence("text",mText);
        outState.putByteArray("bitmap", imgCompressed);
        outState.putBoolean("tocompress",imgChanged);
        outState.putCharSequence("data",mData);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("ADDNOTEACTIVITY", "onRestoreInstanceState");
        if(savedInstanceState != null){
            setContentView(R.layout.activity_add_note);
            setLayout();
            PasswordPreference preference = new PasswordPreference(getApplicationContext());
            preference.setLockedPassword(false);
            mTitolo = savedInstanceState.getString("titolo","");
            mText = savedInstanceState.getString("text","");
            mData = savedInstanceState.getString("data","");

            if(imgCompressed.length==1) imgCompressed = savedInstanceState.getByteArray("bitmap");
            imgChanged = savedInstanceState.getBoolean("tocompress", false);
            new ReloadImageTask().execute();


        }else{
            imgCompressed = new byte[1];
        }

    }

    @Override
    public void onUndo(Parcelable token) {
        //mBitmap = (Bitmap) token;

        image.setVisibility(View.VISIBLE);
        deleteImg.setVisibility(View.VISIBLE);
        imgCompressed = compressBitmap(mBitmap);
    }




    private void deleteFileImage(){
        File f = new File(mFileImagePath);
        if(f.exists()) f.delete();
    }

    private void setNavigationBar(){
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}

    private void restoreSituation(){
        //creo file
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();
        mFileImagePath = dir+"file.jpg";

        sameApp = false;

        //mBitmap = (Bitmap) SingletonParametersBridge.getInstance().getParameter("bitmaptornc");
        if(mBitmap != null) setmBitmapImage(mBitmap, true);

        if(mTitolo != null && !mTitolo.equals("") ) titolo.setText(mTitolo);
        if(mText != null && !mText.equals("")) text.setText(mText);
        if(mData != null && !mData.equals("")) data.setText(mData);
    }



	private void setLayout(){
        openListView = true;
		titolo = (EditText) findViewById(R.id.editText_titolo_addnote);
		text = (EditText) findViewById(R.id.editText_text_addnote);
         image = (ImageView) findViewById(R.id.imageView1_addnote);
         mView = findViewById(R.id.trasparent_view);
        listView = (LinearLayout) findViewById(R.id.popup_window);
        data = (TextView) findViewById(R.id.textView_data_addnote);

        mView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        setWidgetLayout();



        button = (ImageButton) findViewById(R.id.imageButton_addnote);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hints_value = sharedPref.getBoolean("hints_value", true); 
        if(hints_value){
            ViewTarget ac = new ViewTarget(button);
            ShowcaseView.insertShowcaseView(ac, this, R.string.tips_title_hidden_note, R.string.tips_hide_note);
            sharedPref.edit().putBoolean("hints_value", false).commit();
        }



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(openListView){

                    listView.startAnimation(getAnimation(0.0f,-1.0f));
                    listView.setVisibility(View.GONE);
                    if(image.getVisibility()==View.VISIBLE){
                        mView.startAnimation(getAnimation(0.0f,-1.0f));
                        mView.setVisibility(View.GONE);
                        //img button to delete image
                        deleteImg.setVisibility(View.VISIBLE);
                    }
                    button.setImageResource(R.drawable.ic_expand);
                    openListView = false;
                }else{
                    listView.startAnimation(getAnimation(-1.0f,0.0f));
                    listView.setVisibility(View.VISIBLE);
                    if(image.getVisibility()==View.VISIBLE) {
                        mView.startAnimation(getAnimation(-1.0f,0.0f));
                        mView.setVisibility(View.VISIBLE);
                        //img button to delete image
                        deleteImg.setVisibility(View.GONE);
                    }
                    button.setImageResource(R.drawable.ic_collapse);
                    openListView = true;
                }
            }
        });


        deleteImg = (ImageButton) findViewById(R.id.imageButton_delete_img);
        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancello foto
                setmBitmapImage(mBitmap,false);
                imgCompressed = new byte[1];
                deleteImg.setVisibility(View.GONE);
                mUndoBarController.showUndoBar(false, getString(R.string.img_deleted), mBitmap);
            }
        });

        deleteImg.setVisibility(View.GONE);
		
	}

    private void setLockedLayout(){
        Button button = (Button) findViewById(R.id.button_unlock_app);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/EarlyGameBoy.ttf");
        ((TextView)findViewById(R.id.app_locked_title_dialog)).setTypeface(font);
        ((Button)findViewById(R.id.button_unlock_app)).setTypeface(font);
        ((TextView)findViewById(R.id.editText_password_locked_app)).setTypeface(font);
        final EditText editText = (EditText) findViewById(R.id.editText_password_locked_app);
        final PasswordPreference preference = new PasswordPreference(getApplicationContext());
        final String pass = preference.getPassword();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pass.equals(editText.getText().toString().trim())){
                    preference.setLockedPassword(false);
                    setContentView(R.layout.activity_add_note);
                    setLayout();
                    restoreSituation();
                }else{
                    Toast.makeText(getApplicationContext(),R.string.esito_no,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setWidgetLayout(){
        mUndoBarController = new UndoBarController(findViewById(R.id.undobar), this);
    }

    /**
     * this method inserts into textview and imageview info passed by possible note
     */
    private void populateLayout(){
        Bundle bun;
        mBitmap = null;
        boolean exiImg = false;
        if((bun = getIntent().getExtras())!=null){
            setTitle(getString(R.string.title_activity_add_note_mod));
            newNote = (Note) bun.getParcelable("noteToUpdate");
            titolo.setText(newNote.getmName());
            text.setText(newNote.getmDesc());
            mData = getDateLastModification();

            //creo immagine da byte[]
            if(newNote.getmImage().length != 1){
                imgCompressed = newNote.getmImage();
                Log.d("ImageLeght--3", ""+newNote.getmImage().length);
               new ReloadImageTask().execute();
                exiImg = true;
                if(mBitmap == null) Log.e("BITMAP LOAD","NULL");

            }

        }
        setmBitmapImage(mBitmap,exiImg);
    }

    private void setmBitmapImage(Bitmap toAdd, boolean visible){
        //if(image == null) image = (ImageView) findViewById(R.id.imageView1_addnote);
        //if(mView == null) mView = findViewById(R.id.trasparent_view);
        if(visible){
            if(image == null) Log.d("NULL","imageview");
            image.setVisibility(View.VISIBLE);
            mView.setVisibility(View.VISIBLE);
        }else{
            image.setVisibility(View.GONE);
            mView.setVisibility(View.GONE);

        }
        image.setImageBitmap(toAdd);
    }

    private String getDateLastModification(){
        String toReturn = getString(R.string.element_data_modified)+": ";
        String sdataNote = newNote.getmDataString();
        String scurrentDate = DateFormat.getDateInstance().format(new Date());

        Date nota = newNote.getmDate();
        Date current = new Date();
        //diifference in day
        long diff = (current.getTime() - nota.getTime())/(24 * 60 * 60 * 1000);
        if(diff < 1){
            toReturn += getString(R.string.today_mod);
        }else if(diff<2){
            toReturn+= getString(R.string.yesterday_mod);
        }else{
            toReturn+=sdataNote;
        }

        Log.d("DATA CORRENTE", scurrentDate);
        return toReturn;
    }


    /**
     * thi method return a animation top to bottom
     * @param first
     * @param second
     * @return
     */
    private TranslateAnimation getAnimation(float first,float second){
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, first, Animation.RELATIVE_TO_SELF, second
        );
        animation.setDuration(500);
        return animation;
    }


    /**
     * this method create note!
     * @return
     */
	
	private boolean createNote(){
		mTitolo = titolo.getText().toString();
		mText = text.getText().toString();

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
	
	private byte[] compressBitmap(Bitmap toCompress){
		Bitmap compressedBitmap;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] byteC;
		try{
            //qualità della foto 45 --- 50
			toCompress.compress(Bitmap.CompressFormat.JPEG, 45, stream);
			byteC = stream.toByteArray();

		}catch(Exception ex){
			ex.printStackTrace();
			byteC = new byte[1];
		}
		return byteC;
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


    private class ReloadImageTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                mBitmap = BitmapFactory.decodeByteArray(imgCompressed,0,imgCompressed.length);
            }catch (NullPointerException ex){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setmBitmapImage(mBitmap,true);

        }
    }

    private void chooseLayout(){
        PasswordPreference preference = new PasswordPreference(getApplicationContext());
        if(preference.isAppLocked()){
            setContentView(R.layout.locked_app_layout);
            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/EarlyGameBoy.ttf");
            ((TextView)findViewById(R.id.app_locked_title_dialog)).setTypeface(font);
            ((Button)findViewById(R.id.button_unlock_app)).setTypeface(font);
            ((TextView)findViewById(R.id.editText_password_locked_app)).setTypeface(font);
            setLockedLayout();
        }else{
            setContentView(R.layout.activity_add_note);
            setLayout();
            //populateLayout();
            //restoreSituation();
        }
    }


	

}
