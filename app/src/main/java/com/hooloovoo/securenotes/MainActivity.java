package com.hooloovoo.securenotes;


import java.util.Timer;
import java.util.TimerTask;


import com.hooloovoo.securenotes.object.DAO;
import com.hooloovoo.securenotes.object.Encryptor;
import com.hooloovoo.securenotes.object.PBKDF2Encryptor;
import com.hooloovoo.securenotes.object.PKCS12Encryptor;
import com.hooloovoo.securenotes.object.SingletonParametersBridge;


import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	int sec = 0;
	int tentativi = 0;
	String bytePassword;
	boolean settedpass;
	boolean startedDialog = false;
	
	//password gia' messa
	Button accedi;
	TextView txv;
	//password mai messa
	Button signin;
	EditText nuovaPass;
	EditText reNuovaPass;
    //Encryptor
    Encryptor mEncryptor ;
	//SecureCypher cypher;

    //Custom font used to font view
    Typeface font;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("SucureNotes","Avvio Applicazione");
        setEncryptor();

        //Inzialize font
        font = Typeface.createFromAsset(getAssets(), "fonts/EarlyGameBoy.ttf");

		bytePassword = DAO.readFilePassword(this);

        setContentView(R.layout.splash_screen_layout);
        getActionBar().hide();

        int secondsDelayed = 5;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                loadTypeLayout();
            }
        }, secondsDelayed * 1000);



		
		
	}
	
	@Override
	public void onResume(){
		super.onResume();

	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// devo mettere le info nel menu
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    private void loadTypeLayout(){
        getActionBar().show();
        if(bytePassword == null) {
            //password mai messa
            setContentView(R.layout.activity_main_signin);
//			startPasswordDialog();
            setNuovaSessioneLayout();
            //creo DB
            DAO.getInstance(getApplicationContext());

        }else{
            settedpass = true;
            Log.d("Password", bytePassword);
            setContentView(R.layout.activity_main);
            setLayout();
        }
    }
	
	private void setLayout(){

        TextView txt = (TextView)findViewById(R.id.textView_appname2);
        txt.setTypeface(font);

		final EditText edt = (EditText) findViewById(R.id.editText_password);
		 txv = (TextView) findViewById(R.id.textView_esito);
        txv = (TextView) findViewById(R.id.textView_esito);
		edt.setTypeface(font);
		
		accedi = (Button) findViewById(R.id.button_accedi);
        accedi.setTypeface(font);
		accedi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                String decrypted_password = null;
                String input_password = "";

				try{
                    input_password = edt.getText().toString().trim();

					Log.d("Password", "input password: "+ input_password);
					//Log.d("provaporva PASSWORD", cypher.encrypt( input_password).toString());
					decrypted_password = mEncryptor.decrypt(bytePassword, input_password);
					
					Log.d("Password", "encrypted password: "+decrypted_password);
				}catch(NullPointerException nex){
                    Toast.makeText(getApplicationContext(), R.string.empty_input_field, Toast.LENGTH_LONG).show();
                    nex.printStackTrace();
                }
                catch(Exception ex){
                    //Toast.makeText(getApplicationContext(), "Errore inspiegabile", Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
					Log.e("PasswordDetecting", "Errore nella rilevazione password");
				}
				
				if(input_password.equals(decrypted_password) ){
					txv.setText(R.string.esito_ok);
					txv.setTextColor(Color.GREEN);
					tentativi = 0;
                    Encryptor.password = input_password;
					startNoteActivity();
				}else{
					txv.setText(R.string.esito_no);
					txv.setTextColor(Color.RED);
					edt.setText(""); 
					tentativi +=1;
                    Encryptor.password = null;
					Log.d("tentativi", String.valueOf(tentativi));
					if(tentativi == 5){
						Toast.makeText(getApplicationContext(), 
								getString(R.string.no_more_input), Toast.LENGTH_LONG).show();
						freezeActivity(); 
						tentativi = 0;
						setTimePause();
					}
					
					
				}
			}
		});
	}
	
	private void setNuovaSessioneLayout(){
        TextView txt = (TextView)findViewById(R.id.textView_appname2);
        txt.setTypeface(font);

		txv = (TextView) findViewById(R.id.textView_esito_signin);
        txv.setTypeface(font);
		nuovaPass = (EditText) findViewById(R.id.editText_ins_password);
        nuovaPass.setTypeface(font);
		reNuovaPass = (EditText) findViewById(R.id.editText_reins_password);
        nuovaPass.setTypeface(font);
		signin = (Button) findViewById(R.id.button_signin);
        signin.setTypeface(font);
		signin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//creo nuova password
				if(controlInput()){
					//salvo password
                    String toStore  = nuovaPass.getText().toString().trim();
                    String toStoreCripted = mEncryptor.encrypt(toStore, toStore);
                    Encryptor.password = toStore;
                    Log.d("PASSWORD TO WRITE", toStoreCripted);
					DAO.writeFilePassword(getApplicationContext(),  toStoreCripted);
					startNoteActivity();
				}else{
					txv.setText(R.string.password_diverse);
					txv.setTextColor(Color.RED);
				}
				
			}
		});
	}

    private boolean setEncryptor(){
        /*SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int choice = Integer.parseInt(mSharedPreferences.getString("encryptionAlgorithms","0"));
        //int choice = mSharedPreferences.getInt("encryptionAlgorithms",0);
        boolean todo = false;

        if(choice == 0){
            mEncryptor = new PBKDF2Encryptor();
        }else{
            mEncryptor = new PKCS12Encryptor();
        }

        if(Encryptor.algorithm == -1){
            Encryptor.algorithm = choice;
            todo= false;
        }else if(Encryptor.algorithm != choice){

            todo= true;
        }*/

        mEncryptor = new PBKDF2Encryptor();
        SingletonParametersBridge.getInstance().addParameter("encrypt",mEncryptor);
        return true;
    }
	
	private boolean controlInput(){
		String uno = nuovaPass.getText().toString();
		String due = reNuovaPass.getText().toString();
		return uno.equals(due);
	}
	
	private void startNoteActivity(){
		Intent intent = new Intent(this,NotesActivity.class);
		startActivity(intent);
	}
	
//	private void startPasswordActivity(){
//		Intent intent = new Intent(this,PasswordActivity.class);
//		intent.putExtra("situation", true); 
//		startActivity(intent);
//	}
	

	
	private void freezeActivity(){
		accedi.setEnabled(false);
		accedi.setText(R.string.disabled);
		txv.setText(R.string.freeze_activity);
		txv.setTextColor(Color.RED);
	}
	
	private void enabledActivity(){
		accedi.setEnabled(true);
		accedi.setText(R.string.accedi);
		txv.setText("");
		
	}
	
	private void setTimePause(){
		final Timer t;
		
		t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						sec += 1;
						if( sec == 30 ){
							enabledActivity();
							t.cancel();
							t.purge();
							sec = 0;
						}
						
					}
				});
				
			}
		}, 0, 1000);
	
	}

    /*
    private void provaSicurezza()  {
        Log.d("PROVA SICUREZZA","#############");
        byte[] cripted = null;
        cypher.setCypher("angelo");
        try {
            cripted = cypher.encrypt("angelo");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("PROVA SICUREZZA", "encrypted: "+cripted);
        try {
            Log.d("PROVA SICUREZZA", "decrypted: "+ cypher.decrypt(cripted));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("PROVA SICUREZZA","#############");
    }*/

}
