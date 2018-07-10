package com.appg.entry_registration;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.RelativeLayout;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import db.DBHelper;
import static com.appg.entry_registration.ConstClass.DB_USER_TABLE_NAME;
import static com.appg.entry_registration.LoginActivity.userInfo;

public class SettingActivity extends AppCompatActivity {
    ImageButton imgBack;
    RelativeLayout layoutBack;
    WebView webView;
    WebSettings webSettings;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        dbHelper =  new DBHelper(getApplicationContext(), DB_USER_TABLE_NAME, null, 1); // version이 0 이면 안됨.
        dbHelper.testDB();

        webView = findViewById(R.id.manageUserWebView);


        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layoutBack = findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // 언제 호출되는 메소드..?
    public void btnConnectedKeyRegistration(){ // QR code
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }


    public class MyJavascriptInterface {
        @JavascriptInterface
        public void getHtml(String html) { //위 자바스크립트가 호출되면 여기로 html이 반환됨
            System.out.println(html);
        }
    }

    //QRCode 인증 후 OTP 생성 프로그램 설치
    //QRCode는 AVN에 보이는거 찍으면 되는데.. 만약에 가진 차량이 2개가 있다면 entry 등록절차가 어떻게 되는건가?
    // 한번 등록하기만 하면 다른 차량에 대해서도 권한이 생기는건가?
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == 0) {

            if(resultCode == Activity.RESULT_OK)
            {
                final Intent tempData = data;

                android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(SettingActivity.this);
                alt_bld.setMessage("Connected Key OTP 생성 프로그램 설치를 시작합니다.").setCancelable(
                        false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                String result = tempData.getStringExtra("SCAN_RESULT");

                                // 사용자 추가
                                if (dbHelper.insert(result,toSHA256(result))){
                                    android.app.AlertDialog.Builder alt_bld2 = new android.app.AlertDialog.Builder(SettingActivity.this);
                                    alt_bld2.setMessage("프로그램 설치가 완료되어 Connected Key 서비스 이용이 가능합니다.")
                                            .setCancelable(false)
                                            .setPositiveButton("확인",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });
                                    android.app.AlertDialog alert = alt_bld2.create();
                                    alert.setTitle("");
                                    alert.show();
                                } else {
                                    android.app.AlertDialog.Builder alt_bld2 = new android.app.AlertDialog.Builder(SettingActivity.this);
                                    alt_bld2.setMessage("Connected Key OTP 생성 프로그램이 이미 설치되어 있습니다.")
                                            .setCancelable(false)
                                            .setPositiveButton("확인",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });
                                    android.app.AlertDialog alert = alt_bld2.create();
                                    alert.setTitle("");
                                    alert.show();
                                }
                                // 사용자 추가 끝
                            }
                        });
                android.app.AlertDialog alert = alt_bld.create();
                alert.setTitle("");
                alert.show();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //SHA256 보안
    private String toSHA256(String str) {
        String SHA;
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).toUpperCase().substring(1));
            }
            SHA = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            SHA = null;
        }
        return SHA;
    }


}
