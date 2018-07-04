package kr.co.bastion.connectedkey.gcm;

import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        //send new registration token to app server
    }
}