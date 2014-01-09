package com.hooloovoo.securenotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by angelo on 08/01/14.
 */
public class ExportDialogFragment extends DialogFragment {
    EditText nomeFileEdt;
    NotesActivity mActvity;

    public ExportDialogFragment(){};

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActvity = (NotesActivity) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.export_dialog_fragment_layout, null);
        nomeFileEdt = (EditText) v.findViewById(R.id.editText_export_notes_fragment);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        nomeFileEdt.setText(generateString(currentDateTimeString)+"_export");
        Log.d("ExportNotes",currentDateTimeString);
        //nomeFileEdt.setHint("ProvaHint");
        builder.setTitle(R.string.action_export)
                .setCancelable(true)
                .setPositiveButton(R.string.export_ok,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //devo esportare note
                        mActvity.exportNotes(nomeFileEdt.getText().toString());
                    }
                })
                .setNegativeButton(R.string.annulla,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setView(v);

        return builder.create();
    }

    private String generateString(String testo){
        String f = testo.replace(" ","_");
        String s = f.replace(":","_");
        String t = s.replace("/","_");
        return t;
    }
}
