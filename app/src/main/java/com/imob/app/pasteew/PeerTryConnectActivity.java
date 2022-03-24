package com.imob.app.pasteew;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.badzzz.pasteany.core.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PeerTryConnectActivity extends AppCompatActivity {

    private String deviceInfo;
    private String did;

    private TextView deviceInfoView;
    private TextView logsView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.try_connect_peer);

        deviceInfoView = findViewById(R.id.dview);
        logsView = findViewById(R.id.logView);

        deviceInfo = getIntent().getStringExtra("device");
        if (deviceInfo == null) {
            finish();
        } else {
            deviceInfoView.setText(deviceInfo);
            try {
                did = new JSONObject(deviceInfo).getString(Constants.Device.KEY_DEVICEID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (did == null) {
                finish();
            } else {
                doFetchAndConnect();
            }
        }
    }

    public void retry(View view) {
    }

    private void doFetchAndConnect() {


    }
}
