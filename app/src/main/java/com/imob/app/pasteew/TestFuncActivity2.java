package com.imob.app.pasteew;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.badzzz.pasteany.core.api.MsgCreator;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersHandler;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.imob.app.pasteew.utils.FileUtils;
import com.imob.lib.lib_common.Closer;
import com.imob.lib.sslib.peer.Peer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestFuncActivity2 extends AppCompatActivity {

    private ListView knowNameListView;
    private File testFile;

    private List<Set<Peer>> peerList = new ArrayList<>();

    private BaseAdapter knowAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return peerList.size();
        }

        @Override
        public Object getItem(int position) {
            return peerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView infoView = new TextView(TestFuncActivity2.this);
            Set<Peer> item = (Set<Peer>) getItem(position);

            infoView.setText("connections: " + item.size() + "\n" + item.toString());
            return infoView;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_func_2);

        knowNameListView = findViewById(R.id.knownNameListView);

        setup();
        copyTestFileToAppSandboxDirectory();
    }


    private void copyTestFileToAppSandboxDirectory() {
        testFile = new File(getCacheDir(), "a_test_file_name");
        if (!testFile.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream inputStream = null;
                    FileOutputStream fos = null;

                    try {
                        inputStream = getAssets().open("test.img");
                        if (!testFile.exists()) {
                            testFile.createNewFile();
                        }

                        fos = new FileOutputStream(testFile);
                        FileUtils.inputToOutput(inputStream, fos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        Closer.close(fos);
                        Closer.close(inputStream);
                    }
                }
            }).start();

        }
    }


    private void setup() {
        knowNameListView.setAdapter(knowAdapter);

        knowNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Set<Peer> set = peerList.get(position);
                Peer peer = set.iterator().next();
                peer.sendMessage(MsgCreator.createFileMsg(testFile));
            }
        });


        ConnectedPeersHandler.monitorConnectedPeersEvents(new ConnectedPeerEventListenerAdapter() {

            private void notifyAdapter() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        peerList.clear();
                        peerList.addAll(ConnectedPeersManager.getCurrentlyUsedConnectedPeerHandler().getDetailedInfoPeers().values());
                        knowAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onIncomingPeer(ConnectedPeersHandler handler, Peer peer) {
                notifyAdapter();
            }

            @Override
            public void onPeerDropped(ConnectedPeersHandler handler, Peer peer) {
                notifyAdapter();
            }

            @Override
            public void onPeerDetailedInfoGot(ConnectedPeersHandler handler, Peer peer) {
                notifyAdapter();
            }


            @Override
            public void onIncomingFileChunkSaved(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file) {
                super.onIncomingFileChunkSaved(handler, peer, deviceID, msgID, soFar, chunkSize, file);
            }
        });
    }

    private static final String TAG = TestFuncActivity2.class.getName();

    public void testIt(View view) {
    }

}
