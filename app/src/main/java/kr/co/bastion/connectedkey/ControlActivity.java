package kr.co.bastion.connectedkey;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ControlActivity extends AppCompatActivity {
    ImageButton imgBack;
    RelativeLayout layoutBack;

    TextView tvTitle, textView1, textView2, textView3, textView4, textView5, textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        setUIInfo();

        setClickListener();
    }

    private void setUIInfo(){
        imgBack = findViewById(R.id.imgBack);
        layoutBack = findViewById(R.id.layoutBack);
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

    }
}
