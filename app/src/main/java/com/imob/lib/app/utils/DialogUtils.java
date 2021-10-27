package com.imob.lib.app.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public class DialogUtils {

    public interface OnDialogInputListener {
        void onInputContent(String content);
    }

    public static void createInputDialog(Activity activity, String title, OnDialogInputListener inputListener) {
        EditText editText = new EditText(activity);
        new AlertDialog.Builder(activity).setTitle(title).setView(editText).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputListener.onInputContent(editText.getText().toString());
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputListener.onInputContent(null);
            }
        }).create().show();
    }
}
