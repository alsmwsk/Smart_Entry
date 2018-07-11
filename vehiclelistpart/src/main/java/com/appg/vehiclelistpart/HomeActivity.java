package com.appg.vehiclelistpart;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static com.appg.vehiclelistpart.ConstClass.CLIENT_ID;
import static com.appg.vehiclelistpart.ConstClass.POST_DOOR_CLOSE_URL;
import static com.appg.vehiclelistpart.ConstClass.connectedKeyConnectionError;
import static com.appg.vehiclelistpart.ConstClass.connectedKeyDoorLock;
import static com.appg.vehiclelistpart.ConstClass.connectedKeyOff;
import static com.appg.vehiclelistpart.ConstClass.connectedKeyOn;
import static com.appg.vehiclelistpart.ConstClass.connectedKeyPinCancel;
//import static com.appg.vehiclelistpart.LoginActivity.userInfo;

public class HomeActivity extends AppCompatActivity {
    ImageButton imgBack;
    ImageButton imgOptions, imgConnectedKeyOTP, imgConnectedKeyOn, imgConnectedKeyOff;
    RelativeLayout layoutBack, layoutOptions;
    ConstraintLayout layoutBackground;

    LinearLayout layoutBottom;

    FrameLayout layoutHint;
    ImageButton imgHint;

    RelativeLayout layoutTooltip;
    ImageButton imgTooltipClose;

    //문열기 부분이 누락되어있음..
    Button btnEngineStart;
    ImageButton btnEngineStop, btnEmergencyLight, btnEmergencyLightWithHorn, btnAirConditional, btnDoorLock, btnDoorUnlock;
    TextView tvEngineStop, tvEmergencyLight, tvEmergencyLightWithHorn, tvAirConditional, tvDoorLock, tvDoorUnlock;

    static int lastCommand = -1;
    static long lastCommandTime;

    Boolean isPinActivityShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUIInfo();

  /*      if (hasSoftMenu()){ // 소프트키 있는지 확인
            int softkeyHeight = getSoftMenuHeight(); // 소프트키의 높이 확인
            Log.d("softkey Height",String.valueOf(softkeyHeight));
        }*/
  /*
  // 상태메뉴바 높이 확인
        int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int result = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
            Log.d("Height",String.valueOf(result));
        }*/

        setClickListener();
    }

    private void setUIInfo(){
        // 배경 화면 설정
        // 하드웨어 가속 기능때문에 4096x4096 사이즈 이상의 크기가 제대로 출력되지 않아서
        // 크기를 작게 조절하여 이미지가 보이도록 하는 부분입니다.
        layoutBackground = findViewById(R.id.layoutBackground);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.bg_home);
        Bitmap resized = Bitmap.createScaledBitmap( src, 1080, 1920, true );
        Drawable drawable = new BitmapDrawable(getResources(), resized);
        layoutBackground.setBackground(drawable);
        // 배경 화면 설정 완료

        imgBack = findViewById(R.id.imgBack);
        imgOptions = findViewById(R.id.imgOptions);
        layoutBack = findViewById(R.id.layoutBack);
        layoutOptions = findViewById(R.id.layoutOptions);

        imgConnectedKeyOTP = findViewById(R.id.imgConnectedKeyOTP);
        imgConnectedKeyOn = findViewById(R.id.imgConnectedKeyOn);
        imgConnectedKeyOff = findViewById(R.id.imgConnectedKeyOff);

        btnEngineStart = findViewById(R.id.btnEngineStart);
        btnEngineStop = findViewById(R.id.btnEngineStop);
        btnAirConditional = findViewById(R.id.btnAirConditional);
        btnEmergencyLight = findViewById(R.id.btnEmergencyLight);
        btnEmergencyLightWithHorn = findViewById(R.id.btnEmergencyLightWithHorn);
        btnDoorLock = findViewById(R.id.btnDoorLock);
        btnDoorUnlock = findViewById(R.id.btnDoorUnlock);

        tvEngineStop = findViewById(R.id.tvEngineStop);
        tvAirConditional = findViewById(R.id.tvAirConditional);
        tvEmergencyLight = findViewById(R.id.tvEmergencyLight);
        tvEmergencyLightWithHorn = findViewById(R.id.tvEmergencyLightWithHorn);
        tvDoorLock = findViewById(R.id.tvDoorLock);
        tvDoorUnlock = findViewById(R.id.tvDoorUnlock);

        layoutHint = findViewById(R.id.layoutHint);
        imgHint = findViewById(R.id.imgHint);

        layoutTooltip = findViewById(R.id.layoutTooltip);
        imgTooltipClose = findViewById(R.id.imgTooltipClose);

        layoutBottom = findViewById(R.id.layoutBottom);
    }

    private void setClickListener(){

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view){
                switch (view.getId()) {
                    case R.id.layoutBack :
                    case R.id.imgBack: {
                        finish();
                        break;
                    }
                    case R.id.layoutOptions:
                    case R.id.imgOptions:{
                        Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                    }

                    case R.id.tvAirConditional:
                    case R.id.btnAirConditional: {
                        Intent intent = new Intent(HomeActivity.this, ControlActivity.class);
                        startActivity(intent);
                        break;
                    }

                    case R.id.tvDoorLock:
                    case R.id.btnDoorLock: {
                        if (!isPinActivityShown) {
                            isPinActivityShown = true;
                            Intent intent = new Intent(HomeActivity.this, PinActivity.class);
                            intent.putExtra("Type", "doorLock");
                            startActivity(intent);
                        }
                        break;
                    }

                    case R.id.imgConnectedKeyOTP : {
                        Intent intent = new Intent(HomeActivity.this, OTPActivity.class);
                        startActivity(intent);
                        break;
                    }

                    // 1. PIN Code 검증 요청
                    case R.id.imgConnectedKeyOn : {
                        if (!isPinActivityShown) {
                            isPinActivityShown = true;
                            Intent intent = new Intent(HomeActivity.this, PinActivity.class);
                            intent.putExtra("Type", "connectedKeyOn");
                            startActivityForResult(intent, connectedKeyOn);
                        }
                        break;
                    }

                    case R.id.imgConnectedKeyOff : {
                        if (!isPinActivityShown) {
                            isPinActivityShown = true;
                            Intent intent = new Intent(HomeActivity.this, PinActivity.class);
                            intent.putExtra("Type", "connectedKeyOff");
                            startActivityForResult(intent, connectedKeyOff);
                        }
                        break;
                    }

                    case R.id.tvDoorUnlock:
                    case R.id.btnDoorUnlock: {
                        Toast.makeText(getApplicationContext(), "지원되지 않는 기능입니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case R.id.tvEmergencyLight:
                    case R.id.btnEmergencyLight: {
                        Toast.makeText(getApplicationContext(), "지원되지 않는 기능입니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case R.id.tvEmergencyLightWithHorn:
                    case R.id.btnEmergencyLightWithHorn: {
                        Toast.makeText(getApplicationContext(), "지원되지 않는 기능입니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case R.id.btnEngineStart: {
                        Toast.makeText(getApplicationContext(), "지원되지 않는 기능입니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case R.id.tvEngineStop:
                    case R.id.btnEngineStop: {
                        Toast.makeText(getApplicationContext(), "지원되지 않는 기능입니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case R.id.imgHint:
                    case R.id.layoutHint : {
                        layoutTooltip.setVisibility(View.VISIBLE);
                        break;
                    }

                    case R.id.imgTooltipClose : {
                        layoutTooltip.setVisibility(View.INVISIBLE);
                        break;
                    }

                    default:{
                        break;
                    }
                }
            }
        };

        imgBack.setOnClickListener(onClickListener);
        imgOptions.setOnClickListener(onClickListener);

        btnEngineStart.setOnClickListener(onClickListener);
        btnEngineStop.setOnClickListener(onClickListener);
        btnAirConditional.setOnClickListener(onClickListener);
        btnEmergencyLight.setOnClickListener(onClickListener);
        btnEmergencyLightWithHorn.setOnClickListener(onClickListener);
        btnEmergencyLightWithHorn.setOnClickListener(onClickListener);
        btnDoorLock.setOnClickListener(onClickListener);
        btnDoorUnlock.setOnClickListener(onClickListener);

        tvEngineStop.setOnClickListener(onClickListener);
        tvAirConditional.setOnClickListener(onClickListener);
        tvEmergencyLight.setOnClickListener(onClickListener);
        tvEmergencyLightWithHorn.setOnClickListener(onClickListener);
        tvEmergencyLightWithHorn.setOnClickListener(onClickListener);
        tvDoorLock.setOnClickListener(onClickListener);
        tvDoorUnlock.setOnClickListener(onClickListener);

        layoutBack.setOnClickListener(onClickListener);
        layoutOptions.setOnClickListener(onClickListener);

        imgConnectedKeyOTP.setOnClickListener(onClickListener);
        imgConnectedKeyOn.setOnClickListener(onClickListener);
        imgConnectedKeyOff.setOnClickListener(onClickListener);

        layoutHint.setOnClickListener(onClickListener);
        imgHint.setOnClickListener(onClickListener);
        layoutTooltip.setOnClickListener(onClickListener);
        imgTooltipClose.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isPinActivityShown = false;
        switch (resultCode){
            case connectedKeyOn :{
                new MyAsyncTask().execute(R.id.imgConnectedKeyOn);
                break;
            }
            case connectedKeyOff: {
                new MyAsyncTask().execute(R.id.imgConnectedKeyOff);
                break;
            }

            case connectedKeyConnectionError :{
                android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(HomeActivity.this);
                alt_bld.setMessage("원격제어 실행 실패입니다. 원격제어 조건을 확인하신 후 다시 실행하세요.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = alt_bld.create();
                alert.show();
                break;
            }

            case connectedKeyDoorLock: {
                // Connected Key ON이 되어있는지 확인해야 함. 안되어 있으면 안내문 출력
                new MyAsyncTask().execute(R.id.btnDoorLock);
                break;
            }

            case connectedKeyPinCancel: {
                break;
            }

            default : {
                android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(HomeActivity.this);
                alt_bld.setMessage("서버와의 통신 중 오류가 발생하였습니다.").setCancelable(
                        false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = alt_bld.create();
                alert.show();
                break;
            }
        }
    }

    public class MyAsyncTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            // 중복된 명령을 30초 내에 실행했는지 판별하는것..
            if (lastCommand != params[0]) {
                lastCommand = params[0];
                lastCommandTime = System.currentTimeMillis();
                return lastCommand;
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("mmss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String restTime = sdf.format(System.currentTimeMillis() - lastCommandTime);
                if ( (Integer.parseInt(restTime)) > 30){
                    lastCommand = params[0];
                    lastCommandTime = System.currentTimeMillis();
                    return lastCommand;
                } else {
                    return -9999;
                }
            }
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result != -9999){

                sendCommand(result);

                android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(HomeActivity.this);
                alt_bld.setMessage("차량으로 원격제어 명령을 전달하였습니다. 제어 결과는 잠시 후 Push 메시지로 수신될 예정입니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = alt_bld.create();
                alert.setTitle("");
                alert.show();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String restTime = sdf.format(System.currentTimeMillis() - lastCommandTime);

                android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(HomeActivity.this);
                alt_bld.setMessage("이전 원격 제어 명령을 수행하고 있습니다.\n" +
                                   "잠시 후 다시 실행해 주세요.\n" +
                                   "[연속적으로 30초 내 중복 원격 제어 명령 수행 불가] (잔여시간 " + (30 - Integer.parseInt(restTime)) + "초)")
                       .setCancelable(false)
                       .setPositiveButton("확인",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { dialog.cancel();
                          }
                        });
                android.app.AlertDialog alert = alt_bld.create();
                alert.setTitle("");
                alert.show();
            }
        }
    }

    //PINCode 인증후 명령요청하는 부분
    //key On과 key Off의 요청 url이 POST_DOOR_CLOSE_URL로 되어있음..
    //5. SmartEntry On요청 -> 서버
    private static void sendCommand(int command){
        switch (command){
            case R.id.imgConnectedKeyOn :{
                //POST /api/v1/spa/vehicles/:vehicleId/control/smartentryRequest
                //Body는 Control 01. Engine start 와 동일

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(POST_DOOR_CLOSE_URL+userInfo.getVehicleId(0)+"/control/smartentryRequest"); // url이 맞는지? (smart entry에서 connected key로 이름 변경됨)

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("action", "start");
                            jsonObject.accumulate("unit", "C");
                            jsonObject.accumulate("hvacType", "1"); // 0 : 구공조, 1 : 신공조

                            String json = jsonObject.toString();
                            Log.d("Msg",json);

                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Authorization", userInfo.getUserAccessToken());
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setRequestProperty("ccsp-device-id", userInfo.getDeviceID());
                            connection.setRequestProperty("ccsp-application-id", CLIENT_ID);

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
                                        String msgId = jsonObject.getString("msgID");

                                        if (retCode.equals("S")) {
                                            // 제어 결과
                                            userInfo.setConnectedKey(true);
                                        } else {
                                            // 실패 결과
                                        }

                                        connection.disconnect();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        connection.disconnect();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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
                break;
            }
            case R.id.imgConnectedKeyOff :{
                //POST /api/v1/spa/vehicles/:vehicleId/control/smartentryRequest
                //Body는 Control 02. Engine stop 과 동일

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(POST_DOOR_CLOSE_URL+userInfo.getVehicleId(0)+"/control/engine");

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("action", "stop");

                            String json = jsonObject.toString();
                            Log.d("Msg",json);

                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Authorization", userInfo.getUserAccessToken());
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setRequestProperty("ccsp-device-id", userInfo.getDeviceID());
                            connection.setRequestProperty("ccsp-application-id", CLIENT_ID);

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
                                        String msgId = jsonObject.getString("msgID");

                                        if (retCode.equals("S")) {
                                            // 제어 결과
                                            userInfo.setConnectedKey(false);
                                        } else {
                                            // 실패 결과
                                        }

                                        connection.disconnect();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        connection.disconnect();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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

                break;
            }
            case R.id.btnDoorLock: {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(POST_DOOR_CLOSE_URL+userInfo.getVehicleId(0)+"/control/door");

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("action", "close");

                            String json = jsonObject.toString();
                            Log.d("Msg",json);

                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Authorization", userInfo.getUserAccessToken());
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setRequestProperty("ccsp-device-id", userInfo.getDeviceID());
                            connection.setRequestProperty("ccsp-application-id", CLIENT_ID);

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
                                        String msgId = jsonObject.getString("msgID");

                                        if (retCode.equals("S")) {
                                            // 제어 결과
                                        }

                                        connection.disconnect();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        connection.disconnect();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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

                break;
            }
        }
    }

    private boolean hasSoftMenu() {
        //메뉴버튼 유무
        boolean hasMenuKey = ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey();

        //뒤로가기 버튼 유무
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);


        if (!hasMenuKey && !hasBackKey) { // lg폰 소프트키일 경우
            return true;
        } else { // 삼성폰 등.. 메뉴 버튼, 뒤로가기 버튼 존재
            return false;

        }
    }


    private int getSoftMenuHeight() {
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int deviceHeight = 0;


        if (resourceId > 0) {
            deviceHeight = resources.getDimensionPixelSize(resourceId);
        }
        return deviceHeight;
    }
}
