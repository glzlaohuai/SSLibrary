package com.imob.app.pasteew;

import android.content.Intent;
import android.os.Bundle;

import com.imob.app.pasteew.utils.SPWrapper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);

        route();
    }

    private void route() {
        if (SPWrapper.hasSetServiceName()) {

        } else {
            //has no serviceName set, goto setServiceName activityt
            Intent intent = new Intent(this, SetServiceNameActivity.class);
            startActivity(intent);
        }
    }


}
