package com.imob.lib.app.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public class DialogUtils {

    public interface OnDialogInputListener {
        void onInputContent(String content);
    }

    public interface OnListDialogSelectListener {
        void onSelected(int index);
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

    public static void createListDialog(Activity activity, String[] list, OnListDialogSelectListener listener) {
        new AlertDialog.Builder(activity).setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onSelected(which);
            }
        }).create().show();
    }


}
