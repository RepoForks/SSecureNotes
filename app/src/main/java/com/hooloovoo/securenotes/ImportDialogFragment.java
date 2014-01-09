package com.hooloovoo.securenotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;

import com.hooloovoo.securenotes.object.DAO;

import java.io.File;

/**
 * Created by angelo on 08/01/14.
 */
public class ImportDialogFragment extends DialogFragment {
    NotesActivity mActivity;

    public ImportDialogFragment(){};

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (NotesActivity) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] fileList = getFileList();
        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_inport)
                .setItems(fileList,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                    mActivity.importNotes(fileList[which]);
                    }
                })
                .setCancelable(true);
        return builder.create();
    }

    /**
     * this method reads file in folder
     * @return
     */
    private String[] getFileList() {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), DAO.FOLDER);
        String[] listFileString = new String[1];
        if(folder.mkdirs()||folder.isDirectory()){
            File[] list = folder.listFiles();
            listFileString = new String[list.length];
            for(int i = 0;i<list.length;i++){
                listFileString[i] = list[i].getName();
            }
        }
        return listFileString;
    }
}
