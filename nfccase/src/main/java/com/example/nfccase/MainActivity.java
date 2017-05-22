package com.example.nfccase;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button openNfc;
    EditText input;
    NfcAdapter nfcAdapter;
    DateFormat dateFormat=SimpleDateFormat.getDateTimeInstance();
    PopupWindow popupWindow;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openNfc=(Button)findViewById(R.id.openNfc);
        input=(EditText)findViewById(R.id.edit);

        nfcAdapter=NfcAdapter.getDefaultAdapter(this);
        openNfc.setOnClickListener(this);
        createPopWindow();
        createDialog();
    }


    @Override
    protected void onResume() {
        super.onResume();}

    @Override
    public void onClick(View v) {
        if (v==openNfc){
            if (null==nfcAdapter){    //手机无nfc功能
                Toast.makeText(this,"手机不支持NFC功能",Toast.LENGTH_LONG).show();
                openNfc.setEnabled(false);
            }else if (!nfcAdapter.isEnabled()){    //未开通nfc
//                Toast.makeText(this,"请开通NFC功能",Toast.LENGTH_LONG).show();
                if (null!=alertDialog){
                    alertDialog.show();
                }
            }else {
                Toast.makeText(this,"请按提示操作",Toast.LENGTH_LONG).show();
                nfcAdapter.setNdefPushMessageCallback(callback,MainActivity.this);
                nfcAdapter.setOnNdefPushCompleteCallback(completeCallback,MainActivity.this);
                if (null!=popupWindow){
                    popupWindow.showAtLocation(getCurrentFocus(),Gravity.BOTTOM,0,0);
                }
            }
        }else if (v==input){

        }
    }
    private void createPopWindow(){
        FrameLayout content=new FrameLayout(this);
        TextView tip=new TextView(this);
        tip.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
        tip.setTextColor(Color.WHITE);
        tip.setText("请把手机靠近以触发分享...\n loading...");
        FrameLayout.LayoutParams childLayoutParam=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        childLayoutParam.gravity= Gravity.CENTER;
        content.addView(tip,childLayoutParam);
        popupWindow=new PopupWindow(content, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C0000000")));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Toast.makeText(MainActivity.this,"dismiss",Toast.LENGTH_LONG).show();
                nfcAdapter.setNdefPushMessageCallback(null,MainActivity.this);
            }
        });
    }
    //去打开 “nfc功能”对话框
    private void createDialog(){
        alertDialog=new AlertDialog.Builder(this)
                .setTitle("NFC功能")
                .setMessage("可在设置-->NFC设置选择打开")
                .setPositiveButton("去也", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setAction(Settings.ACTION_NFC_SETTINGS);
                        startActivity(intent);
                    }
                }).setNegativeButton("不了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
    }


    NfcAdapter.CreateNdefMessageCallback callback=new NfcAdapter.CreateNdefMessageCallback(){
        @Override
        public NdefMessage createNdefMessage(NfcEvent event) {
            Log.d("pan","---------createNdefMessage---------NfcEvent:  "+event);
//            Toast.makeText(MainActivity.this,"createNdefMessage",Toast.LENGTH_SHORT).show();
            StringBuilder builder=new StringBuilder(input.getText())
                    .append("\n beam time: "+dateFormat.format(new Date()));
            NdefMessage msg=new NdefMessage(
                    new NdefRecord[]{
                            NdefRecord.createUri("openjd://virtual?params={\"category\":\"jump\",\"des\":\"worthbuy_list\",\"other\":\"中文\"}")
//                            NdefRecord.createUri("http://example.com")
//                            NdefRecord.createMime("application/vnd.com.example.android.beam",builder.toString().getBytes())
//                            , NdefRecord.createApplicationRecord("com.example.peertopeer")
//                            , NdefRecord.createApplicationRecord("com.jingdong.app.mall")
                    }
            );

            return msg;
        }
    };
    NfcAdapter.OnNdefPushCompleteCallback completeCallback=new NfcAdapter.OnNdefPushCompleteCallback() {
        @Override
        public void onNdefPushComplete(NfcEvent event) {
            Log.d("pan","---------onNdefPushComplete---------NfcEvent:  "+event);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null!=popupWindow){
                        popupWindow.dismiss();
                        Toast.makeText(MainActivity.this,"分享结束",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    };
}
