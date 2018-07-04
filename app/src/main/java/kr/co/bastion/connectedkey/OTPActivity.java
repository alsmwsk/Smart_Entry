package kr.co.bastion.connectedkey;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static kr.co.bastion.connectedkey.LoginActivity.userInfo;

public class OTPActivity extends AppCompatActivity {
    ImageButton imgBack, imgOptions;
    ProgressBar progressBar;
    TextView tvSec;
    Button btnNextOTP;
    TextView tvOTP1, tvOTP2, tvOTP3, tvOTP4, tvOTP5, tvOTP6, tvOTP7, tvOTP8;
    RelativeLayout layoutBack, layoutOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        setUIInfo();

        setClickListener();

        btnNextOTP.callOnClick();

    }

    private void setUIInfo(){
        tvOTP1 = findViewById(R.id.tvOTP1);
        tvOTP2 = findViewById(R.id.tvOTP2);
        tvOTP3 = findViewById(R.id.tvOTP3);
        tvOTP4 = findViewById(R.id.tvOTP4);
        tvOTP5 = findViewById(R.id.tvOTP5);
        tvOTP6 = findViewById(R.id.tvOTP6);
        tvOTP7 = findViewById(R.id.tvOTP7);
        tvOTP8 = findViewById(R.id.tvOTP8);

        progressBar = findViewById(R.id.progressBarTime);
        tvSec = findViewById(R.id.tvSec);

        imgBack = findViewById(R.id.imgBack);
        layoutBack = findViewById(R.id.layoutBack);

        imgOptions = findViewById(R.id.imgOptions);
        layoutOption = findViewById(R.id.layoutOption);

        btnNextOTP = findViewById(R.id.btnNextOTP);
    }
    private void setClickListener(){

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 삭제 여부 문의해둠
            }
        });
        layoutOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 삭제 여부 문의해둠
            }
        });
        btnNextOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String OTPCode = OTPClass.create(userInfo.getUserID(), System.currentTimeMillis()); // System.currentTimeMillis() = UTC??

                tvOTP8.setText(String.valueOf(OTPCode.charAt(7)));
                tvOTP7.setText(String.valueOf(OTPCode.charAt(6)));
                tvOTP6.setText(String.valueOf(OTPCode.charAt(5)));
                tvOTP5.setText(String.valueOf(OTPCode.charAt(4)));
                tvOTP4.setText(String.valueOf(OTPCode.charAt(3)));
                tvOTP3.setText(String.valueOf(OTPCode.charAt(2)));
                tvOTP2.setText(String.valueOf(OTPCode.charAt(1)));
                tvOTP1.setText(String.valueOf(OTPCode.charAt(0)));

                setUTCTimer();
            }
        });
    }


    private void setUTCTimer(){
        if (!tvSec.getText().equals("0")){
            return;
        }

        final Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            public void run(){
                OTPActivity.this.runOnUiThread(new Runnable(){
                    public void run(){
                        SimpleDateFormat sdf = new SimpleDateFormat("ss");
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String utcTime = sdf.format(new Date());
                        progressBar.setProgress(Integer.parseInt(utcTime));
                        int restTime = 59-Integer.parseInt(utcTime);
                        tvSec.setText(String.valueOf(restTime));
                        if (restTime == 0){
                            timer.cancel();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

}
