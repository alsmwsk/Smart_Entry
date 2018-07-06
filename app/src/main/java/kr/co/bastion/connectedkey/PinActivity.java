package kr.co.bastion.connectedkey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static kr.co.bastion.connectedkey.ConstClass.PUT_ISSUE_CONTROL_TOKEN;
import static kr.co.bastion.connectedkey.ConstClass.connectedKeyConnectionError;
import static kr.co.bastion.connectedkey.ConstClass.connectedKeyDoorLock;
import static kr.co.bastion.connectedkey.ConstClass.connectedKeyOff;
import static kr.co.bastion.connectedkey.ConstClass.connectedKeyOn;
import static kr.co.bastion.connectedkey.ConstClass.connectedKeyPinCancel;
import static kr.co.bastion.connectedkey.LoginActivity.DeviceID;
import static kr.co.bastion.connectedkey.LoginActivity.userInfo;

public class PinActivity extends Activity  {
    static StringBuffer pin;
    static int numCount = 0;

    TextView tv1, tv2, tv3, tv4, temp;

    Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    Button btnCancel;
    ImageButton btnDelete;
    FrameLayout DeleteLayout;

    Intent getIntent;

    String Type="";

    Boolean isEditable = true;




    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        getIntent = getIntent();
        Type = getIntent.getStringExtra("Type");

        pin = new StringBuffer();

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);

        btn0 = findViewById(R.id.button0);
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn5 = findViewById(R.id.button5);
        btn6 = findViewById(R.id.button6);
        btn7 = findViewById(R.id.button7);
        btn8 = findViewById(R.id.button8);
        btn9 = findViewById(R.id.button9);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if  (isEditable) {
                    switch (view.getId()) {
                        case R.id.button0: {
                            setPinNumber("0");
                            break;
                        }
                        case R.id.button1: {
                            setPinNumber("1");
                            break;
                        }
                        case R.id.button2: {
                            setPinNumber("2");
                            break;
                        }
                        case R.id.button3: {
                            setPinNumber("3");
                            break;
                        }
                        case R.id.button4: {
                            setPinNumber("4");
                            break;
                        }
                        case R.id.button5: {
                            setPinNumber("5");
                            break;
                        }
                        case R.id.button6: {
                            setPinNumber("6");
                            break;
                        }
                        case R.id.button7: {
                            setPinNumber("7");
                            break;
                        }
                        case R.id.button8: {
                            setPinNumber("8");
                            break;
                        }
                        case R.id.button9: {
                            setPinNumber("9");
                            break;
                        }
                        case R.id.btnCancel: {
                            setResult(connectedKeyPinCancel);
                            finish();
                            break;
                        }
                        case R.id.btnDelete: {
                            setPinNumber("");
                            break;
                        }
                        case R.id.DeleteLayout: {
                            setPinNumber("");
                            break;
                        }
                    }
                }
            }
        };

        btn0.setOnClickListener(onClickListener);
        btn1.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        btn3.setOnClickListener(onClickListener);
        btn4.setOnClickListener(onClickListener);
        btn5.setOnClickListener(onClickListener);
        btn6.setOnClickListener(onClickListener);
        btn7.setOnClickListener(onClickListener);
        btn8.setOnClickListener(onClickListener);
        btn9.setOnClickListener(onClickListener);

        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(onClickListener);
        DeleteLayout = findViewById(R.id.DeleteLayout);
        DeleteLayout.setOnClickListener(onClickListener);

        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(onClickListener);

        temp = findViewById(R.id.textView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        keyCode = KeyEvent.KEYCODE_UNKNOWN;
        return super.onKeyDown(keyCode, event);
    }

    public void setPinNumber(String value){
        //btnDelete이거나 DeleteLayout이거나..
        if (value.equals("")){
            if (numCount > 0){
                numCount--;
                switch(numCount){
                    case 0: {
                        tv1.setText("");
                        break;
                    }
                    case 1: {
                        tv2.setText("");
                        break;
                    }
                    case 2: {
                        tv3.setText("");
                        break;
                    }
                    case 3: {
                        tv4.setText("");
                        break;
                    }
                }
                pin.deleteCharAt(numCount);
            }
        } else {
            // 초기값 -1?
            if (numCount < 4){
                pin.append(value);
                switch(numCount){
                    case 0: {
                        tv1.setText("*");
                        numCount++;
                        break;
                    }
                    case 1: {
                        tv2.setText("*");
                        numCount++;
                        break;
                    }
                    case 2: {
                        tv3.setText("*");
                        numCount++;
                        break;
                    }
                    case 3: {
                        tv4.setText("*");
                        isEditable = false;
                        try {
                            // AsyncTask 호출..
                            new myAsyncTask().execute(pin.toString(),Type);
                        } catch (Exception e){
                            Log.d("Msg", "Error : "+e.getMessage());
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public class myAsyncTask extends AsyncTask<String,String,Boolean> {
        Boolean isPinRight = false;
        String type;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(PinActivity.this, "","원격제어 명령 전송중", true);
        }
        //params 부분은 어디에 있는가??
        //2. PIN Code 검증 결과
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // PIN Code 발급 요청
                URL url = new URL(PUT_ISSUE_CONTROL_TOKEN);
                String enteredPin = params[0];
                type = params[1];
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("Authorization","Bearer "+ userInfo.getUserAccessToken());
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestMethod("PUT");

                connection.setDoOutput(true);
                connection.setDoInput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("pin", enteredPin);
                jsonObject.accumulate("deviceId", DeviceID);

                String json = jsonObject.toString();

                OutputStream output = connection.getOutputStream();
                output.write(json.getBytes());
                output.close();
                connection.connect();

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
                        try
                        {
                            jsonObject = new JSONObject(page);
                            String controlToken = jsonObject.getString("controlToken");
                            // String expiresTime = jsonObject.getString("expiresTime");
                            // expiresTime : API 문서에는 있는데 실제로는 없어서 주석처리함.

                            userInfo.setControlToken(controlToken);
                            // userInfo.setControlTokenExpires(expiresTime);

                            isPinRight = true;

                        } catch (JSONException e) {
                            e.printStackTrace();

                            setResult(connectedKeyConnectionError);
                            finish();
                        }

                    } else {
                        isPinRight = false;
                    }
                    connection.disconnect();

                } catch (Exception e){
                    Log.d("Msg", "실패 / error:" + result + " "+connection.getResponseMessage());
                    connection.disconnect();

                    setResult(connectedKeyConnectionError);
                    if (dialog != null){
                        dialog.dismiss();
                        dialog = null;
                    }
                    finish();
                }

            } catch (Exception e) {
                setResult(connectedKeyConnectionError);
                if (dialog != null){
                    dialog.dismiss();
                    dialog = null;
                }
                finish();
            }

            return isPinRight;
        }

        // 2. PIN Code 검증 결과(pinactivity)
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            numCount = 0;
            pin.setLength(0);
            tv1.setText("");
            tv2.setText("");
            tv3.setText("");
            tv4.setText("");

            if(result){
                /*dialog.setProgressStyle(ProgressBar.INVISIBLE);
                dialog.setMessage("원격제어 명령 전송이 완료되었습니다.");*/
                Toast.makeText(getApplicationContext(),"원격제어 명령 전송이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                switch (type){
                    case "connectedKeyOn" :{
                        if (dialog != null){
                            dialog.dismiss();
                            dialog = null;
                        }
                        setResult(connectedKeyOn);
                        finish();
                        break;
                    }

                    case "connectedKeyOff" :{
                        if (dialog != null){
                            dialog.dismiss();
                            dialog = null;
                        }
                        setResult(connectedKeyOff);
                        finish();
                        break;
                    }

                    case "doorLock" : {
                        if (dialog != null){
                            dialog.dismiss();
                            dialog = null;
                        }
                        setResult(connectedKeyDoorLock);
                        finish();
                         // vehicleID 필요
                        break;
                    }
                }
            } else {

                int count = userInfo.getPinWrongCount();

                if (dialog != null){
                    dialog.dismiss();
                    dialog = null;
                }

                if (count > 4 || userInfo.getPinLimited()){
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(PinActivity.this);
                    alt_bld.setMessage("비밀번호 5회 입력 오류로 차단된 계정입니다. 관리자에게 문의해 주세요.").setCancelable(
                            false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alt_bld.create();
                    alert.show();
                    userInfo.setPinLimited(true);
                    isEditable = true;
                } else {
                    userInfo.setPinWrongCount(++count);
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(PinActivity.this);
                    alt_bld.setMessage("비밀번호가 일치하지 않습니다. 5회 연속 입력 실패시 계정이 차단되오니 주의하시기 바랍니다.").setCancelable(
                            false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alt_bld.create();
                    alert.show();
                    isEditable = true;
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }
}


