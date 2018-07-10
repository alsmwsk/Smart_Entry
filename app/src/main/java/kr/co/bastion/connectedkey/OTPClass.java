package kr.co.bastion.connectedkey;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/*
OTPClass

String Key와 시간정보를 조합하여 OTP를 생성하는 부분입니다.
 */

// 비트연산자 & 이진수로 표현해서 각 숫자의 비트가 같으면 1 아니면 0


public class OTPClass {
    private static long DISTANCE = 60000; // 60sec
    private static final String ALGORITHM = "HmacSHA256";

    public static long makeOTPCode(long time, String str) {
        byte[] data = new byte[8];

        long value = time;
        for (int i = 8; i-- > 0; value >>>= 8){
            data[i] = (byte)value;
        }
        try {
            Mac mac = Mac.getInstance(ALGORITHM); // Message Authentication Code.
            mac.init(new SecretKeySpec(str.getBytes(), ALGORITHM));

            byte[] hash = mac.doFinal(data);

            int offset = hash[20 - 1] & 0xF;

            long truncatedHash = 0;
            for (int i = 0; i < 4; i++) {
                truncatedHash <<= 8;
                truncatedHash |= hash[offset + i] & 0xFF;
            }
            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= 100000000;

            return truncatedHash;
        } catch (Exception e){
            return 0;
        }
    }

    public static String create(String str, long time) {
        return String.format("%08d",makeOTPCode(time / DISTANCE, str));
    }

    public long getDISTANCE() {
        return DISTANCE;
    }

    public void setDISTANCE(long distance){
        DISTANCE = distance;
    }

}
