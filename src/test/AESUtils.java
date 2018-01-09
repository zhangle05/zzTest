package pku.dishu.tps.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AESUtils {

    private static final String ENCODING = "utf-8";

    private static String key = "81eb6d792cd7db84";

    private static final Logger logger = LoggerFactory.getLogger(AESUtils.class);

    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                logger.error("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                logger.error("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes(ENCODING);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = hex2byte(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, ENCODING);
                return originalString;
            } catch (Exception e) {
                logger.error("doFinal Error", e);
                return null;
            }
        } catch (Exception ex) {
            logger.error("Unkonw  Error", ex);
            return null;
        }
    }

    // 判断Key是否正确
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes(ENCODING);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(ENCODING));
        return byte2hex(encrypted).toLowerCase();
    }

    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Encrypt("测试", key));
        System.out.println(Decrypt("5aa08246bd8f0d0408aed3acc8649306", key));
        String encode = Encrypt("", key);
        System.out.println("encode:" + encode + ".");
        String decode = Decrypt(encode, key);
        System.out.println("decode:" + decode + ".");
        encode = Encrypt("user=21&code=aaaa-bbb-cccc-ddd", key);
        System.out.println("encode:" + encode + ".");
        decode = Decrypt(encode, key);
        System.out.println("decode:" + decode + ".");
        
    }
}
