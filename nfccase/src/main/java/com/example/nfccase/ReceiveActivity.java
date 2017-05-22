package com.example.nfccase;

import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import static android.nfc.NfcAdapter.ACTION_NDEF_DISCOVERED;

/**
 * Created by panqian on 2017/5/18.
 */

public class ReceiveActivity extends AppCompatActivity {
    TextView textView;
    StringBuilder stringBuilder=new StringBuilder();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView=new TextView(this);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        stringBuilder.append("ReceiveActivity  \n" + "接收到的beam信息为：\n");
        setContentView(textView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            Log.d("pan","---------ReceiveActivity$onResume---------getIntent().getAction():  "+getIntent().getAction());
            stringBuilder.append("action:  "+ACTION_NDEF_DISCOVERED+"\n");
            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage) rawMsgs[0];

            stringBuilder.append(new String(msg.getRecords()[0].getPayload()));
            textView.setText(stringBuilder.toString());
        }
    }
}
