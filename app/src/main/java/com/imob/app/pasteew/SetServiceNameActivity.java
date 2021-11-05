package com.imob.app.pasteew;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.imob.app.pasteew.utils.SPWrapper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SetServiceNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_service_name);

        EditText editText = findViewById(R.id.editText);
        findViewById(R.id.setup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceName = editText.getText().toString().trim();

                if (!TextUtils.isEmpty(serviceName)) {
                    SPWrapper.setServiceName(serviceName);
                    Intent intent = new Intent(SetServiceNameActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });


    }
}
