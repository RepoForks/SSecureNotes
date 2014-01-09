package com.hooloovoo.securenotes;


import com.hooloovoo.securenotes.object.DAO;
import com.hooloovoo.securenotes.object.Encryptor;
import com.hooloovoo.securenotes.object.PBKDF2Encryptor;
import com.hooloovoo.securenotes.object.SingletonParametersBridge;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordDialogFragment extends DialogFragment {
	private EditText pass1;
	private EditText pass2;
	private boolean settedpassword;
	Encryptor encryptor;
	Context mContenxt;
	String password = null;
	int pass_setted = 0;
	

	public PasswordDialogFragment(){
		
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
		encryptor =new PBKDF2Encryptor();
		// Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View v = inflater.inflate(R.layout.password_alert_dialog_layout, null);
	    pass1 = (EditText) v.findViewById(R.id.editText_pass1_alertdialog_password);
	    pass2 = (EditText) v.findViewById(R.id.editText_pass2_alertdialog_password);
	    
	    pass1.setTypeface(Typeface.DEFAULT);
	    pass2.setTypeface(Typeface.DEFAULT);

	    builder.setTitle(R.string.titolo_alert_dialog_mod_password);
	    pass1.setHint(R.string.hint_old_password);
	    pass2.setHint(R.string.hint_ins_password);

	    
	    builder.setCancelable(false)
               .setPositiveButton(R.string.ok_button, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int id) {
					setPassword();
					
				}
			})
			   .setNegativeButton(R.string.annulla, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					
				}
			})  
			  .setView(v);
		return builder.create();
	}
	

	
	private void setPassword(){
		String passFromPass1 = pass1.getText().toString();
		String passFromPass2 = pass2.getText().toString();

		if(passFromPass1.equals(Encryptor.password)){//ok posso salvare
			//salvo apssword
            if(!passFromPass2.equals("")){
			    savePassword(passFromPass2);
                Toast.makeText(getActivity(),R.string.password_impostata,Toast.LENGTH_LONG).show();
                Encryptor.password = passFromPass2;
                settedpassword = true;
            }else{
                Toast.makeText(getActivity(),R.string.attenzione_password_obbligatoria,Toast.LENGTH_LONG).show();
                settedpassword = false;
            }
		}else{
			Toast.makeText(getActivity(),R.string.password_diverse,Toast.LENGTH_LONG).show();
            settedpassword =false;
        }
        SingletonParametersBridge.getInstance().addParameter("settedpassword",true);
    }
	
	private void savePassword(String password){
		this.password = password;
		this.pass_setted = 1;
		DAO.writeFilePassword(mContenxt, encryptor.encrypt(password,password));
	}

}
