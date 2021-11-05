package com.imob.app.pasteew;

import android.os.Bundle;

import com.imob.app.pasteew.utils.ServiceRegister;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        registerService();
    }


    private void registerService() {
        ServiceRegister.startServiceRegisterStuff();
    }
}
