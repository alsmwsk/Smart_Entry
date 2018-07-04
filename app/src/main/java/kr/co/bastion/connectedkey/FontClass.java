package kr.co.bastion.connectedkey;

import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Field;

/*
FontClass

어플리케이션 기본 폰트를 HyundaiSansHeadKRRegular.ttf로 변경하기 위한 부분입니다.

 */

public class FontClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
             final Typeface customFontTypeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "HyundaiSansHeadKRRegular.ttf");


             final Field defaultFontTypefaceField = Typeface.class.getDeclaredField("SERIF");
             defaultFontTypefaceField.setAccessible(true);
             defaultFontTypefaceField.set(null, customFontTypeface);
         } catch (Exception e) {
             Log.d("Msg","Can not set custom font " + "HyundaiSansHeadKRRegular.ttf" + " instead of " + "SERIF");
         }

    }
}