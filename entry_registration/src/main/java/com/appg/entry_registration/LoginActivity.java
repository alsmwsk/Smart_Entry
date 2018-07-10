package com.appg.entry_registration;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.support.constraint.Constraints.TAG;
import static com.appg.entry_registration.ConstClass.ACCOUNT_STATE;
import static com.appg.entry_registration.ConstClass.CLIENT_ID;
import static com.appg.entry_registration.ConstClass.GET_OAUTH_URL;
import static com.appg.entry_registration.ConstClass.GET_USER_PROFILE_URL;
import static com.appg.entry_registration.ConstClass.POST_PUSH_DEVICE_REGISTRATION_URL;
import static com.appg.entry_registration.ConstClass.POST_TOKEN_URL;
import static com.appg.entry_registration.ConstClass.REDIRECT_URI;
import static com.appg.entry_registration.ConstClass.SENDER_ID;
import static com.appg.entry_registration.ConstClass.SIGN_URL;
import static com.appg.entry_registration.ConstClass.client_secret;

public class LoginActivity extends Activity {
    WebView webView;
    WebView registWebView;
    WebSettings webSettings;
    String code = "";
    static String DeviceID = "";
    static String pushRegId;

    private long backPressedTime = 0;
    private final long FINISH_INTERVAL_TIME = 2000;

    static UserClass userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userInfo = new UserClass();
        // 유저 정보 클래스 생성

        webView = findViewById(R.id.loginWebView);

        }


    // 9. Phone Device 등록 요청 - 2
    private void setRegisterPushDevice() {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(POST_PUSH_DEVICE_REGISTRATION_URL);

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.accumulate("uuid", createUUID(getApplicationContext()));
                    jsonObject.accumulate("pushRegId", pushRegId);
                    jsonObject.accumulate("pushType", "GCM");

                    String json = jsonObject.toString();
                    Log.d("Msg",json);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("ccsp-service-id", CLIENT_ID);

                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    OutputStream output = connection.getOutputStream();
                    output.write(json.getBytes());
                    output.flush();
                    output.close();
                    connection.connect();
                    try {
                        int result = connection.getResponseCode();
                        Log.d("Msg", String.valueOf(result));
                        Log.d("Msg", connection.getResponseMessage());
                        // 10. DeviceId 전달
                        if (result == 200) {

                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

                            String line;
                            String page = "";

                            while ((line = reader.readLine()) != null) {
                                page += line;
                            }

                            Log.d("Msg", "전체내용 : " + page);
                            try {
                                jsonObject = new JSONObject(page);
                                String retCode = jsonObject.getString("retCode");
                                String resCode = jsonObject.getString("resCode");
                                String resMsg = jsonObject.getString("resMsg");
                                String msgId = jsonObject.getString("msgId");

                                if (retCode.equals("S")) {
                                    jsonObject = new JSONObject(resMsg);
                                    DeviceID = jsonObject.getString("deviceId");

                                    userInfo.setDeviceID(DeviceID);

                                    Log.d("Msg","Register push device successfully.");
                                }

                                connection.disconnect();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Push Device Registration Error : "+result,Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        connection.disconnect();
                    }
                } catch (MalformedURLException murle){
                    murle.printStackTrace();
                } catch (IOException ioe){
                    ioe.printStackTrace();
                } catch (Exception e) {
                    Log.d("Msg", e.toString());
                }
            }
        }.start();
    }



    //인터넷 연결상태 체크
    private void checkInternetInfo(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()){
            Toast.makeText(getApplicationContext(),"인터넷 상태를 확인해 주세요.",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //안드로이드 디바이스 고유식별정보 생성
    private static String createUUID(Context context) {
      /*  final TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice = "" + tm.getDeviceId();
        final String tmSerial = "" + tm.getSimSerialNumber();
        final String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());*/

        String androidID = android.provider.Settings.Secure.getString( context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID );

        return androidID;
    }

    private void getResultJSON(BufferedReader reader){
        String line = "";
        StringBuffer page = new StringBuffer();

        try {

            while (true) {
                try {
                    line = reader.readLine();
                    if (line != null) {
                        page.append(line);
                    } else {
                        break;
                    }
                }catch (Exception e){
                    break;
                }
            }

            Log.d("Msg", "전체내용 : " + page);
            try {
                JSONObject jsonObject = new JSONObject(page.toString());
                String tokenString = jsonObject.getString("access_token"); // 획득한 Access Token 값. ACCESS TOKEN 획득
                String tokenType = jsonObject.getString("token_type");
                String refresh_token = jsonObject.getString("refresh_token");
                String tokenExpires = jsonObject.getString("expires_in");
                Log.d("Msg", "토큰 : " + tokenString);

                userInfo.setUserAccessToken(tokenString);
                userInfo.setAccessTokenType(tokenType);
                userInfo.setRefreshAccessToken(refresh_token);
                userInfo.setAccessTokenExpires(tokenExpires);

                URL url = new URL(GET_USER_PROFILE_URL); // USER PROFILE 요청
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(60000);
                connection.setReadTimeout(60000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + tokenString);

                int result = connection.getResponseCode();
                if (result == 200) { // Access Token을 넣어 success로 나오면 해당 유저의 정보를 불러온 후 로그인 완료 처리. 차량 목록 액티비티로 이동함.
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    page.delete(0, page.length()); // 앞서 사용한 스트링 초기화
                    while ((line = reader.readLine()) != null)
                        page.append(line);

                    jsonObject = new JSONObject(page.toString());
                    String user_ID = jsonObject.getString("id");
                    String user_EMAIL = jsonObject.getString("email");
                    String user_NAME = jsonObject.getString("name");
                    String user_MOBILENUMBER = jsonObject.getString("mobileNum");

                    userInfo.setUserID(user_ID);
                    userInfo.setUserEmail(user_EMAIL);
                    userInfo.setUserName(user_NAME);
                    userInfo.setUserMobileNum(user_MOBILENUMBER);

                    connection.disconnect();


                } else {
                    System.out.println("유저 정보 호출 실패 / error : " + result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
