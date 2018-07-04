package kr.co.bastion.connectedkey;

public class ConstClass {
    static final String ACCOUNT_STATE = "connected_key_account_01";
    static final String CLIENT_ID = "25fa8900-60b0-4f5d-802b-04c7168f64ea";
    static final String client_secret = "secret";

    static final String GET_OAUTH_URL = "http://bluelink.connected-car.io/api/v1/user/oauth2/authorize";
    static final String REDIRECT_URI = "http://bluelink.connected-car.io/api/v1/user/oauth2/redirect";
    static final String POST_TOKEN_URL = "http://bluelink.connected-car.io/api/v1/user/oauth2/token";
    static final String GET_USER_PROFILE_URL = "http://bluelink.connected-car.io/api/v1/user/profile";
    static final String POST_PUSH_DEVICE_REGISTRATION_URL = "http://bluelink.connected-car.io/api/v1/spa/notifications/register";
    static final String SIGN_URL = "http://bluelink.connected-car.io/web/v1/user/signin";

    static final String POST_DOOR_CLOSE_URL = "http://bluelink.connected-car.io/api/v1/spa/vehicles/";

    static String PUT_ISSUE_CONTROL_TOKEN = "http://bluelink.connected-car.io/api/v1/user/pin";

    final static String GET_MANAGE_CAR_SHARING = "http://bluelink.connected-car.io/api/v1/profile/users";

    static String GET_VEHICLE_LIST_URL = "http://bluelink.connected-car.io/api/v1/spa/vehicles";
    static String GET_REGISTER_CAR_URL = "http://bluelink.connected-car.io/api/v1/profile/vehicles";
    static String GET_REQUEST_CAR_SHARING_URL = "http://bluelink.connected-car.io/api/v1/profile/users/";

    static final String POST_PUSH_DEVICE_MAPPING_URL = "http://bluelink.connected-car.io/api/v1/spa/notifications/";

    static String SENDER_ID = "506332744487";//
    // "358342126764";

    static String DB_USER_TABLE_NAME = "CONNECTED_KEY_USER_TABLE";


    static final int connectedKeyOn = 100;
    static final int connectedKeyOff = 110;
    static final int connectedKeyDoorLock = 200;
    static final int connectedKeyConnectionError = -100;
    static final int connectedKeyNotOn = -200;
    static final int connectedKeyPinCancel = -900;

}
