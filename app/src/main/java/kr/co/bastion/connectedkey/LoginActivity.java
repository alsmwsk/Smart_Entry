package kr.co.bastion.connectedkey;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.support.constraint.Constraints.TAG;
import static java.lang.Thread.sleep;
import static kr.co.bastion.connectedkey.ConstClass.ACCOUNT_STATE;
import static kr.co.bastion.connectedkey.ConstClass.CLIENT_ID;
import static kr.co.bastion.connectedkey.ConstClass.GET_OAUTH_URL;
import static kr.co.bastion.connectedkey.ConstClass.GET_USER_PROFILE_URL;
import static kr.co.bastion.connectedkey.ConstClass.POST_PUSH_DEVICE_REGISTRATION_URL;
import static kr.co.bastion.connectedkey.ConstClass.POST_TOKEN_URL;
import static kr.co.bastion.connectedkey.ConstClass.REDIRECT_URI;
import static kr.co.bastion.connectedkey.ConstClass.SENDER_ID;
import static kr.co.bastion.connectedkey.ConstClass.SIGN_URL;
import static kr.co.bastion.connectedkey.ConstClass.client_secret;

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

        //인터넷 연결 상태 확인.
        checkInternetInfo();

        webView = findViewById(R.id.loginWebView);

        // 로그인 페이지 시작
        webView.setWebViewClient(new WebViewClient() {
            //웹사이트의 주소 컨트롤을 위해 만든 메소드.?
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            // 오류가 났을 경우, 오류는 복수할 수 없음
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                switch (errorCode) {
                    case ERROR_AUTHENTICATION:
                        break;               // 서버에서 사용자 인증 실패
                    case ERROR_BAD_URL:
                        break;                           // 잘못된 URL
                    case ERROR_CONNECT:
                        break;                          // 서버로 연결 실패
                    case ERROR_FAILED_SSL_HANDSHAKE:
                        break;    // SSL handshake 수행 실패
                    case ERROR_FILE:
                        break;                                  // 일반 파일 오류
                    case ERROR_FILE_NOT_FOUND:
                        break;               // 파일을 찾을 수 없습니다
                    case ERROR_HOST_LOOKUP:
                        break;           // 서버 또는 프록시 호스트 이름 조회 실패
                    case ERROR_IO:
                        break;                              // 서버에서 읽거나 서버로 쓰기 실패
                    case ERROR_PROXY_AUTHENTICATION:
                        break;   // 프록시에서 사용자 인증 실패
                    case ERROR_REDIRECT_LOOP:
                        break;               // 너무 많은 리디렉션
                    case ERROR_TIMEOUT:
                        break;                          // 연결 시간 초과
                    case ERROR_TOO_MANY_REQUESTS:
                        break;     // 페이지 로드중 너무 많은 요청 발생
                    case ERROR_UNKNOWN:
                        break;                        // 일반 오류
                    case ERROR_UNSUPPORTED_AUTH_SCHEME:
                        break; // 지원되지 않는 인증 체계
                    case ERROR_UNSUPPORTED_SCHEME:
                        break;          // URI가 지원되지 않는 방식
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) { // 로그인 화면에서 로그인을 누르면 아래 내용이 실행됨.
                super.onPageFinished(view, url);

                System.out.println(url);
                if (url.contains(REDIRECT_URI)) { // 넘어온 페이지의 url이 리다이렉트 url이 맞는지 확인하고,
                    int idx = url.indexOf("state");
                    String state = url.substring(idx + 6);

                    if (idx < 0 || !state.equals(ACCOUNT_STATE)) { // 앞서 입력한 state 값이 정상적인지 체크.
                        Toast.makeText(getApplicationContext(), "잘못된 접근입니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    idx = url.indexOf("code");
                    code = url.substring(idx + 5, idx + 27); // 인증 코드 획득

                    new Thread() {
                        public void run() { //
                            try {
                                URL url = new URL(POST_TOKEN_URL); // 인증 코드를 통해 엑세스 토큰을 획득 시도
                                Log.d("Msg", "get authentication code:" + code);

                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("POST");

                                // 긴줄 한개만 나온다.
                                String base64EncodedString = "Basic " + Base64.encodeToString((CLIENT_ID + ":" + client_secret).getBytes(), Base64.NO_WRAP);
                                String tempData = "grant_type=authorization_code&code=" + code + "&redirect_uri=" + REDIRECT_URI;

                                connection.setConnectTimeout(5000);
                                connection.setRequestProperty("Authorization", base64EncodedString);
                                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                connection.setReadTimeout(5000);
                                connection.setDoOutput(true);
                                connection.setDoInput(true);

                                OutputStream output = connection.getOutputStream();
                                output.write(tempData.getBytes());
                                output.close();
                                connection.connect();

                                int result = connection.getResponseCode();
                                if (result == 200) { // 획득 성공했다면, 돌려받은 json 내용을 분석하여 액세스 토큰을 획득
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

                                    // 로그인 성공 시 푸시 등록
                                    registPushId();


                                    // JSON 결과 분석, 저장
                                    getResultJSON(reader);
                                } else {
                                    Log.d("Msg", "액세스 토큰 획득 실패 / error:" + connection.getResponseMessage());
                                    connection.disconnect();
                                    finish();
                                }
                            } catch (NullPointerException npe) {
                                Log.d("Msg", "Null Pointer Exception");
                            } catch (Exception e) {
                                Log.d("Msg", e.getMessage());
                            }

                            finish();
                        }
                    }.start();
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.setNetworkAvailable(true);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new MyJavascriptInterface(), "Android");

        webView.loadUrl(GET_OAUTH_URL + "?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&state=" + ACCOUNT_STATE); // 1차적으로 OAUTH2 인증 후 redirect 페이지로 이동
    }

    public class MyJavascriptInterface {

        @JavascriptInterface
        public void getHtml(String html) { //위 자바스크립트가 호출되면 여기로 html이 반환됨
            System.out.println(html);
        }
    }

    void registPushId(){
        new AsyncTask<Void,Void,String>() {
            // 스레드에 의해 처리될 내용을 담기 위한 함수
            //GCM 으로 디바이스 ID 등록
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";

                Log.d(TAG, msg);
                try {
                    InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                    String rid = instanceID.getToken(SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    //instanceID.deleteToken(rid,GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                    msg = "Device registered, registration ID=" + rid;

                    pushRegId = rid;

                    Log.d(TAG, msg);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            // 데이터 처리를 다 한 후 수행되는 메소드.
            @Override
            protected void onPostExecute(String s) {
                setRegisterPushDevice();
                super.onPostExecute(s);
            }
        }.execute(null, null, null); // 파라미터 3개를 NULL로 준 이유?
    }


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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        Log.d("Msg",webView.getOriginalUrl());

        if ( (keyCode == KeyEvent.KEYCODE_BACK) && (webView.getUrl().equals(SIGN_URL)) ){
            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                super.onBackPressed();
                return false;
            } else {
                backPressedTime = tempTime;
                Toast.makeText(this, "뒤로 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                keyCode = KeyEvent.KEYCODE_UNKNOWN;
            }
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            if (webView.getOriginalUrl().equals(SIGN_URL)){
                webView.loadUrl(GET_OAUTH_URL + "?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&state=" + ACCOUNT_STATE);
            }
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_BACK) && !(webView.canGoBack())) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private void checkInternetInfo(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()){
            Toast.makeText(getApplicationContext(),"인터넷 상태를 확인해 주세요.",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

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
                String tokenString = jsonObject.getString("access_token"); // 획득한 Access Token 값.
                String tokenType = jsonObject.getString("token_type");
                String refresh_token = jsonObject.getString("refresh_token");
                String tokenExpires = jsonObject.getString("expires_in");
                Log.d("Msg", "토큰 : " + tokenString);

                userInfo.setUserAccessToken(tokenString);
                userInfo.setAccessTokenType(tokenType);
                userInfo.setRefreshAccessToken(refresh_token);
                userInfo.setAccessTokenExpires(tokenExpires);

                URL url = new URL(GET_USER_PROFILE_URL);
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

                    Intent resultIntent = new Intent(LoginActivity.this, VehicleListActivity.class);
                    startActivity(resultIntent);
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
