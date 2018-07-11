package com.appg.remote_control_part;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.appg.remote_control_part.ConstClass.CLIENT_ID;
import static com.appg.remote_control_part.ConstClass.GET_REGISTER_CAR_URL;
import static com.appg.remote_control_part.ConstClass.GET_REQUEST_CAR_SHARING_URL;
import static com.appg.remote_control_part.ConstClass.GET_VEHICLE_LIST_URL;
import static com.appg.remote_control_part.ConstClass.POST_PUSH_DEVICE_MAPPING_URL;
import static com.appg.remote_control_part.ConstClass.REDIRECT_URI;
//import static com.appg.remote_control_part.LoginActivity.userInfo;

public class VehicleListActivity extends AppCompatActivity {
    private long backPressedTime = 0;
    private final long FINISH_INTERVAL_TIME = 2000;

    static String userID;
    static String userEMail;
    static String userName;
    static String userMobileNum;
    static String AccessToken;
    static String carID = "";
    static String shareID = "";

    static UserClass userInfo;

    TextView txvUsername;
    TextView txvUsermail;

    WebView webView;

    LinearLayout linearLayout_NoVehicle;
    ScrollView scrollView_VehicleList;

    LinearLayout LayoutCarShareByOwner;
    LinearLayout LayoutCarRemote;

    ImageView imgBack;
    ImageView imgOptions;
    RelativeLayout layoutBack, layoutOptions;

    Button btnRegisterVehicles;
    Button btnRegisterVehicles2;
    Button btnRequestVehiclesShareByUser;

    Boolean isExistVehicles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiclelist);


    }

    @Override
    protected void onStart() {
        super.onStart();

//        setIntentParams(); // 로그인 페이지에서 받은 내용들을 설정하는 부분

        setUIInfo(true);
        //Boolean isVehicleRegistered = getVehicleList(); // 유저의 정보를 이용하여 유저에게 등록된 차량을 확인하는 부분.
        //setUIInfo(isVehicleRegistered); // 등록되어 있는 차량이 있으면 차량 목록이 나타나도록 설정하고
        // 등록되어 있지 않으면 차량이 없는 화면이 나타나도록 설정.
        // 현재 차량 목록은 직접 하드코딩으로 넣어둔 상태입니다.

    }

    private void setUIInfo(Boolean isVehicleRegistered) {
        webView = findViewById(R.id.webview1);

        linearLayout_NoVehicle = findViewById(R.id.linearLayout_NoVehicle);
        scrollView_VehicleList = findViewById(R.id.ScrollView_VehicleList);



        //소유자에 의한 차량공유 계약자 -> 사용자
        //1.차량공유요청 URL 호출
        //2.차량공유요청 URL 호출 결과
        //어떤 차량을 누구에게 공유할 것인지 그리고 스마트 엔트리 권한부여 또는 안함
        //3.사용자 차량 공규 허가 요청
        //4.공유 요청 push 전달(id)서버->공유대상자
        //5.공유 Share Id 전달 서버->계약자(소유자)
        //6.공유 수락 URL 요청 공유대상자->서버
        //7.공유 수락 URL 정보 전달 서버 -> 공유대상자
        //8. 공유 수락 URL 화면 표시 공유대상자 -> 서버
        //9. 공유 CarID 전달 서버 -> 공유대상자
        //10. 공유 결과 push 전달 서버 -> 계약자
        //웹뷰에서 결과 받아온다.
        LayoutCarShareByOwner = findViewById(R.id.LayoutCarShareByOwner);
        LayoutCarShareByOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        System.out.println(url);
                        if (url.contains(REDIRECT_URI)) {

                            webView.setVisibility(View.INVISIBLE);
                            int idx = url.indexOf("share_id"); // 계약자가 받은 share_id값.. 그러면 공유대상자가 받은 share_id값은 어디에??
                            String id = url.substring(idx + 9);

                            if (idx < 0) {
                                Toast.makeText(getApplicationContext(), "차량 정보를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                shareID = id; // 공유받은 차량 ID 획득
                            }
                        }
                    }
                });
                webView.setWebChromeClient(new WebChromeClient());
                webView.setNetworkAvailable(true);

                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true); // 자바스크립트 사용
                webSettings.setDomStorageEnabled(true); // 로컬스트로지사용 팝업창 하루동안 안보기 같은 기능에 사용됨
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 필요에 의해 팝업창을 띄울 경우가 있는데, 해당 속성을 추가해야 window.open() 이 제대로 작동합니다.
                webView.addJavascriptInterface(new MyJavascriptInterface(), "Android"); // 자바스크립트에서 안드로이드 함수사용.

                Map<String, String> extraHeaders = new HashMap<String, String>();
                extraHeaders.put("Authorization","Bearer "+AccessToken);
                extraHeaders.put("Accept-Language","KO");
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(GET_REQUEST_CAR_SHARING_URL+userID+"/cars/"+carID+"/share",extraHeaders); // carID를 받아오지 못하여 확인할 수 없는 상황입니다.
            }
        });

        //원격제어 웹뷰에서 결과받아온다.
        LayoutCarRemote = findViewById(R.id.LayoutCarRemote);
        LayoutCarRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 푸시 매핑 시작
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(POST_PUSH_DEVICE_MAPPING_URL); // 이것을 왜 하는 걸까?

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("vehicleId", userInfo.getVehicleId(0)); // 사용자가 클릭한 차량을 동적으로 처리 해줘야되는데..

                            String json = jsonObject.toString();
                            Log.d("Msg",json);

                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Authorization", userInfo.getUserAccessToken());
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setRequestProperty("ccsp-device-id", userInfo.getDeviceID());
                            connection.setRequestProperty("ccsp-application-id", CLIENT_ID);

                            connection.setConnectTimeout(5000); //초기 연결이 소켓이 기다리는 시간제어
                            connection.setReadTimeout(5000); // 입력 스트림이 데이터의 도착을 기다리는 시간제어
                            connection.setDoOutput(true); // 데이터를 요청 몸체에 쓸 것인지 지정
                            connection.setDoInput(true); // 데이터를 읽을 것인지 지정

                            OutputStream output = connection.getOutputStream();
                            output.write(json.getBytes()); //Bytes로 바꾸는 이유가 무엇인가?
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
                                        String msgId = jsonObject.getString("msgID");

                                        if (retCode.equals("S")) {
                                            Log.d("Msg","푸시 매핑 성공");
                                        } else {
                                            Log.d("Msg","푸시 매핑 실패");
                                        }

                                        connection.disconnect();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                connection.disconnect();
                                Log.d("Msg","푸시 매핑 실패");
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

                // 푸시 매핑 종료
                Intent intent = new Intent(VehicleListActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        //사용자 이름
        txvUsername = findViewById(R.id.txvUsername);
        txvUsername.setText(userName);
        //사용자 이메일
        txvUsermail = findViewById(R.id.txvUsermail);
        txvUsermail.setText(userEMail);

        //뒤로 가기버튼 누르면 로그인 화면으로 이동
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehicleListActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //뒤로가기 레이아웃
        layoutBack = findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehicleListActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //설정화면으로 이동
        imgOptions = findViewById(R.id.imgOptions);
        imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehicleListActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });

        //설정화면으로 이동
        layoutOptions = findViewById(R.id.layoutOptions);
        layoutOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehicleListActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });

        //등록된 차량이 없을 경우
        //3. 신규 차량 등록 요청 - 1
        if (!isVehicleRegistered){
            linearLayout_NoVehicle.setVisibility(View.VISIBLE);
            scrollView_VehicleList.setVisibility(View.INVISIBLE);
            imgOptions.setVisibility(View.INVISIBLE);

            btnRegisterVehicles = findViewById(R.id.btnRegisterVehicles);
            btnRegisterVehicles.setOnClickListener(new View.OnClickListener() { // 등록된 차량이 없는 화면에서의 차량 등록버튼
                @Override
                public void onClick(View v) {
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            System.out.println(url);
                            //4. 신규 차량 등록 요청 결과
                            if (url.contains(REDIRECT_URI)) {

                                webView.setVisibility(View.INVISIBLE);
                                int idx = url.indexOf("car_id");
                                String id = url.substring(idx + 7); // car id 가져오기

                                if (idx < 0) {
                                    Toast.makeText(getApplicationContext(), "차량 등록 실패", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d("Msg","car ID : "+id);
                                    carID = id;
                                    // 차량 ID 데이터베이스에 등록하는 부분 추가.
                                    onRestart(); // 다시 목록이 불러와지는지 확인이 필요함.
                                }
                            }
                        }
                    });
                    webView.setWebChromeClient(new WebChromeClient());
                    webView.setNetworkAvailable(true);

                    WebSettings webSettings = webView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webSettings.setDomStorageEnabled(true);
                    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                    webView.addJavascriptInterface(new MyJavascriptInterface(), "Android");

                    Map<String, String> extraHeaders = new HashMap<String, String>();
                    extraHeaders.put("Authorization","Bearer "+AccessToken);
                    extraHeaders.put("Accept-Language","KO");
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(GET_REGISTER_CAR_URL,extraHeaders);
                }
            });
        }//등록된 차량이 없을 경우
        // 등록된 차량이 있을 경우
        // 4. 신규 차량 등록 요청 - 2
        else {
            linearLayout_NoVehicle.setVisibility(View.INVISIBLE);
            scrollView_VehicleList.setVisibility(View.VISIBLE);
            imgOptions.setVisibility(View.VISIBLE);
        }
        btnRegisterVehicles2 = findViewById(R.id.btnRegisterVehicles2); // 등록된 차량이 있는 화면에서의 차량 등록 버튼
        btnRegisterVehicles2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        System.out.println(url);
                        if (url.contains(REDIRECT_URI)) {

                            webView.setVisibility(View.INVISIBLE);
                            int idx = url.indexOf("car_id");
                            String id = url.substring(idx + 7);

                            if (idx < 0) {
                                Toast.makeText(getApplicationContext(), "차량 등록 실패", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Log.d("Msg","car ID : "+id);
                                carID = id; // 차량id 받아오기.
                                onRestart(); // 다시 목록이 불러와지는지 확인이 필요함.
                            }
                        }
                    }
                });
                webView.setWebChromeClient(new WebChromeClient());
                webView.setNetworkAvailable(true);

                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webView.addJavascriptInterface(new MyJavascriptInterface(), "Android");

                Map<String, String> extraHeaders = new HashMap<String, String>();
                extraHeaders.put("Authorization","Bearer "+AccessToken);
                extraHeaders.put("Accept-Language","KO");
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(GET_REGISTER_CAR_URL,extraHeaders);
            }
        });

        btnRequestVehiclesShareByUser = findViewById(R.id.btnRequestVehiclesShareByUser); // 사용자가 계약자에게 요청하는 버튼
        btnRequestVehiclesShareByUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        System.out.println(url);
                        if (url.contains(REDIRECT_URI)) {

                            webView.setVisibility(View.INVISIBLE);
                            int idx = url.indexOf("share_id");
                            String id = url.substring(idx + 9);

                            if (idx < 0) {
                                Toast.makeText(getApplicationContext(), "차량 정보를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Log.d("Msg", "Share ID : "+id);
                                shareID = id; // 공유받은 차량 ID 획득
                            }
                        }
                    }
                });
                webView.setWebChromeClient(new WebChromeClient());
                webView.setNetworkAvailable(true);

                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webView.addJavascriptInterface(new MyJavascriptInterface(), "Android");

                Map<String, String> extraHeaders = new HashMap<String, String>();
                extraHeaders.put("Authorization","Bearer "+AccessToken);
                extraHeaders.put("Accept-Language","KO");
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(GET_REQUEST_CAR_SHARING_URL+userID+"/shares",extraHeaders);
            }
        });
    }// setUIinfo 끝

    private void setIntentParams(){
        AccessToken = userInfo.getUserAccessToken();
        userID = userInfo.getUserID();
        userEMail = userInfo.getUserEmail();
        userMobileNum = userInfo.getUserMobileNum();
        userName = userInfo.getUserName();
    }

    //1. 차량 목록 요청
    //차량목록 가져오기
    private Boolean getVehicleList(){
        isExistVehicles = false; // 차량이 존재하는지 여부

        new Thread() {
            public void run() {
                try {
                    URL url = new URL(GET_VEHICLE_LIST_URL);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestProperty("Authorization", userInfo.getUserAccessToken());
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("ccsp-device-id", userInfo.getDeviceID());
                    connection.setRequestProperty("ccsp-application-id", CLIENT_ID); // Device ID하나만 있으면 되는거아닌가?
                    connection.setRequestMethod("GET");
                    //누락 되어있음connection.connect();

                    // 2. 차량 목록 요청 결과
                    // 성공하면 result = 200 실패하면 그 밖에 값
                    int result = connection.getResponseCode();
                    try{

                        if (result == 200) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

                            String line;
                            String page = "";

                            while ((line = reader.readLine()) != null){
                                page += line;
                            }

                            Log.d("Msg","전체내용 : "+page);
                            connection.disconnect();
                            try
                            {
                                JSONObject jsonObject = new JSONObject(page);
                                String resCode = jsonObject.getString("resCode");
                                String resMsg = jsonObject.getString("resMsg"); // 여기에 등록된 차량정보가 온다.
                                String msgID = jsonObject.getString("msgID");

                                JSONArray jsonArray = new JSONArray(resMsg); // resMsg는 jsonObject인데 JSONArray이 치환?
                                int count = jsonArray.length();
                                userInfo.setVehicleCount(count);
                                if (count < 1){
                                    isExistVehicles = false;
                                } else {
                                    for (int i = 0; i < count-1; i++){
                                        JSONObject vehicleJsonObject = jsonArray.getJSONObject(i);
                                        String vehicleId = vehicleJsonObject.getString("vehicleId"); // 차량id의 형식 ??
                                        String vehicleName = vehicleJsonObject.getString("vehicleName"); // 이거는 벤츠,소나타?
                                        String vehicleNickname = vehicleJsonObject.getString("nickname"); // 닉네임은 무엇인가?

                                        userInfo.setVehicleId(vehicleId,i);
                                        userInfo.setVehicleName(vehicleName,i);
                                        userInfo.setVehicleNickname(vehicleNickname,i);
                                    }
                                    isExistVehicles = true;
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("Msg", "실패 / error:" + result);
                            userInfo.setVehicleCount(0);
                            connection.disconnect();
                        }
                    } catch (Exception e){
                        Log.d("Msg", "실패 / error:" + result);
                        connection.disconnect();
                    }
                } catch (NullPointerException npe) {
                    Log.d("Msg", "Null Pointer Exception");
                } catch (Exception e) {
                    Log.d("Msg", e.getMessage());
                }
            }
        }.start();

        //return isExistVehicles;
        // 차량 목록을 불러와야하는데 불러오지 못해서 강제로 true로 해둠.
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (webView.getVisibility() == View.VISIBLE){
            if (keyCode == KeyEvent.KEYCODE_BACK){
                if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                    webView.loadUrl("about:blank");
                    webView.clearHistory();
                    webView.setVisibility(View.INVISIBLE);
                    return false;
                } else {
                    backPressedTime = tempTime;
                    Toast.makeText(this, "뒤로 버튼을 한 번 더 누르면 페이지가 종료됩니다.", Toast.LENGTH_SHORT).show();
                    keyCode = KeyEvent.KEYCODE_UNKNOWN;
                }
            }
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK){
                if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                    super.onBackPressed();
                    return false;
                } else {
                    backPressedTime = tempTime;
                    Toast.makeText(this, "뒤로 버튼을 한 번 더 누르면 어플이 종료됩니다.", Toast.LENGTH_SHORT).show();
                    keyCode = KeyEvent.KEYCODE_UNKNOWN;
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public class MyJavascriptInterface {

        @JavascriptInterface
        public void getHtml(String html) { //위 자바스크립트가 호출되면 여기로 html이 반환됨
            System.out.println(html);
        }
    }

    private void settingUserInfo(String deviceid, String tokenString, String tokenType, String refresh_token, String tokenExpires
    , String user_ID, String user_EMAIL, String user_NAME, String user_MOBILENUMBER)
    {
        userInfo.setConnectedKey(true);
        userInfo.setDeviceID(deviceid);
        userInfo.setUserAccessToken(tokenString);
        userInfo.setAccessTokenType(tokenType);
        userInfo.setRefreshAccessToken(refresh_token);
        userInfo.setAccessTokenExpires(tokenExpires);
        userInfo.setUserID(user_ID);
        userInfo.setUserEmail(user_EMAIL);
        userInfo.setUserName(user_NAME);
        userInfo.setUserMobileNum(user_MOBILENUMBER);
    }
}
