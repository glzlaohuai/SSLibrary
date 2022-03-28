package com.imob.app.pasteew;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.NsdServiceInfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PeerTryConnectActivity extends AppCompatActivity {

    private static final int TIMEOUT = 1000 * 5;

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


    private void appendLog(String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logsView.append("\n" + log);
            }
        });
    }

    public void retry(View view) {
        doFetchAndConnect();
    }

    private void doFetchAndConnect() {
        NsdServiceInfoUtils.tryToFetchServiceInfoByID(did, TIMEOUT, new NsdServiceInfoUtils.IPositivelyNsdServiceInfoFetchListener() {
            @Override
            public void onTimeout(String deviceID, long timeout) {
                appendLog("fetch nsd timeout after: " + timeout);
            }

            @Override
            public void onFetched(String did, String ip, int port) {
                appendLog("got device info: ip: " + ip + ", port: " + port);
            }

        });
    }

    public void clearLogs(View view) {
        logsView.setText("");
    }
}
