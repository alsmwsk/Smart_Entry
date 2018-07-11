package com.appg.vehiclelistpart;

import android.app.Application;

/*
UserClass

유저 정보 및 차량 정보, 상태 등을 저장하고 있는 클래스입니다.
 */

public class UserClass extends Application {
    private static String userName; //이름
    private static String userID; //유저의 로그인 ID
    private static String userEmail; //유저의 이메일
    private static String userMobileNum; // 유저의 핸드폰번호
    private static String userAccessToken; // 액세스 토큰
    private static String accessTokenType; // 액세스 토큰 타입
    private static String refreshAccessToken; // 액세스 토큰 새로받은거
    private static String accessTokenExpires; // 유효기간

    private static String deviceID; // 등록된 휴대폰ID

    private static String controlToken; // ?
    private static String controlTokenExpires; // ?

    private static  String[] vehicleId; // 차량 ID
    private static  String[] vehicleName; // 차량 이름
    private static  String[] vehicleNickname; // 차량 닉네임..

    private static int vehicleCount; // 차량 개수
    private static int pinWrongCount; // 핀 잘못친 횟수
    private static Boolean isPinLimited; // 핀 제한여부
    private static Boolean isConnectedKeyOn; // ConnectedKeyOn 상태

    @Override
    public void onCreate(){
        userName = "";
        userID= "";
        userEmail= "";
        userMobileNum= "";
        userAccessToken= "";
        accessTokenType = "";
        refreshAccessToken = "";
        accessTokenExpires = "";
        controlToken = "";
        controlTokenExpires = "";

        deviceID = "";

        vehicleCount = 0;
        pinWrongCount = 0;
        isPinLimited = false;

        isConnectedKeyOn = false;
        super.onCreate();
    }

    public void setUserName(String str){
        userName = str;
    }
    public void setUserID(String str){
        userID = str;
    }
    public void setUserEmail(String str){
        userEmail = str;
    }
    public void setUserMobileNum(String str){
        userMobileNum = str;
    }
    public void setUserAccessToken(String str){
        userAccessToken = str;
    }
    public void setAccessTokenType(String str){
        accessTokenType = str;
    }
    public void setRefreshAccessToken(String str){
        refreshAccessToken = str;
    }
    public void setAccessTokenExpires(String str){
        accessTokenExpires = str;
    }

    public void setDeviceID(String str) { deviceID = str; }

    public void setControlToken(String str){
        controlToken = str;
    }
    public void setControlTokenExpires(String str){
        controlTokenExpires = str;
    }

    public void setVehicleCount(int cnt){
        vehicleCount = cnt;
        if (vehicleCount > 0) {
            vehicleId = new String[vehicleCount - 1];
            vehicleName = new String[vehicleCount - 1];
            vehicleNickname = new String[vehicleCount - 1];

            for (int i = 0; i < vehicleCount - 1; i++) {
                vehicleId[i] = "";
                vehicleName[i] = "";
                vehicleNickname[i] = "";
            }
        }
    }
    public void setVehicleId(String str, int cnt) { vehicleId[cnt] = str; }
    public void setVehicleName(String str, int cnt) { vehicleName[cnt] = str; }
    public void setVehicleNickname(String str, int cnt) { vehicleNickname[cnt] = str; }
    public void setPinWrongCount(int cnt){ pinWrongCount = cnt; }
    public void setPinLimited(Boolean result) { isPinLimited = result; }
    public void setConnectedKey(Boolean result) { isConnectedKeyOn = result; }


    ////////////////////////////////////////////////////////////////////////////

    public String getUserName(){
        return userName;
    }
    public String getUserID(){
        return userID;
    }
    public String getUserEmail(){
        return userEmail;
    }
    public String getUserMobileNum(){
        return userMobileNum;
    }
    public String getUserAccessToken(){
        return userAccessToken;
    }
    public String getAccessTokenType(){
        return accessTokenType;
    }
    public String getRefreshAccessToken(){
        return refreshAccessToken;
    }
    public String getAccessTokenExpires(){
        return accessTokenExpires;
    }

    public String getDeviceID() { return deviceID; }

    public String getControlToken(){
        return controlToken;
    }
    public String getControlTokenExpires(){
        return controlTokenExpires;
    }

    public int getVehicleCount() { return vehicleCount; }
    public String getVehicleId(int cnt) { return vehicleId[cnt]; }
    public String getVehicleName(int cnt) { return vehicleName[cnt]; }
    public String getVehicleNickname(int cnt) { return vehicleNickname[cnt]; }
    public int getPinWrongCount() { return pinWrongCount; }
    public Boolean getPinLimited() { return isPinLimited; }
    public Boolean getConnectedKey() { return isConnectedKeyOn; }
}