package com.gnifrix.util;

import android.content.ContextWrapper;
import android.util.Log;

import com.gnifrix.debug.GLog;
import com.gnifrix.util.base64.Base64;
import com.kt.gigagenie.pci.R;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by LeeBaeng on 2018-09-20.
 */
public class HsUtil_Encrypter {

    public static String UTF_8;   /** KISA 보완조치 - by dalkommjk | 2019-11-01 */
    public static String SHA_256;
    // 키
    private static String KEY;
    // 128bit (16자리) = 0123456789012345
    private static String KEY_128;
    // 256bit (32자리) = 01234567890123456789012345678901
    private static String KEY_256;

    public HsUtil_Encrypter(ContextWrapper context) {
        try {
            UTF_8 = context.getResources().getString(R.string.encoding_type_utf8);
            SHA_256 = context.getResources().getString(R.string.encoding_type_sha256);
            KEY = context.getResources().getString(R.string.sample_key);
            KEY_128 = context.getResources().getString(R.string.sample_key_16);
            KEY_256 = context.getResources().getString(R.string.sample_key_32);

        } catch (Exception e) {
            Log.e(this.toString(), "HsUtil_Encrypter's Parameter set failed.", e);
        }
    }

    // AES 128 암호화
    public static String encryptAES128(String string) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] keyData = KEY_128.getBytes(UTF_8);

        // 운용모드 CBC, 패딩은 PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // key 와 iv 같게..
        // 블록 암호의 운용 모드(Block engine modes of operation)가 CBC/OFB/CFB를 사용할 경우에는
        // Initialization Vector(IV), IvParameterSpec를 설정해줘야한다. 아니면 InvalidAlgorithmParameterException 발생
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyData, "AES"), new IvParameterSpec(keyData));

        // AES 암호화
        byte[] encrypted = cipher.doFinal(string.getBytes(UTF_8));
        // base64 인코딩
        byte[] base64Encoded = Base64.encode(encrypted);
        String result = new String(base64Encoded, UTF_8);

        GLog.printTest("encryptAES128", "encrypt AES128 : [" + string + "] to [" + result + "], key=" + KEY_128);

        String decode = decryptAES128(result);
        return result;
    }

    // AES 128복호화
    public static String decryptAES128(String string) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] keyData = KEY_128.getBytes(UTF_8);

        // 운용모드 CBC, 패딩은 PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyData, "AES"), new IvParameterSpec(keyData));

        // base64 디코딩
        byte[] base64Decoded = Base64.decode(string.getBytes(UTF_8));
        // AES 복화화
        byte[] decrypted = cipher.doFinal(base64Decoded);
        String result = new String(decrypted, UTF_8);

        GLog.printTest("decryptAES128", "decrypt AES128 : [" + string + "] to [" + result + "], key=" + KEY_128);

        return result;
    }

    // AES 256 암호화
    public static String encryptAES256(String string) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] key256Data = KEY_256.getBytes(UTF_8);
        byte[] key128Data = KEY_128.getBytes(UTF_8);

        // 운용모드 CBC, 패딩은 PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // key 와 iv 같게..
        // 블록 암호의 운용 모드(Block engine modes of operation)가 CBC/OFB/CFB를 사용할 경우에는
        // Initialization Vector(IV), IvParameterSpec를 설정해줘야한다. 아니면 InvalidAlgorithmParameterException 발생

        // AES 256은 미국만 되는거라. JDK/JRE 패치를 해야된다.
        // http://www.oracle.com/technetwork/java/javase/downloads/index.html 에서
        // Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files for JDK/JRE 8 이런 링크 찾아서 다운
        // $JAVA_HOME\jre\lib\security 아래에 local_policy.jar, US_export_policy.jar 파일 overwrite!

        // iv값이 16자리가 아니면..
        // java.security.InvalidAlgorithmParameterException: Wrong IV length: must be 16 bytes long 발생
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key256Data, "AES"), new IvParameterSpec(key128Data));

        // AES 암호화
        byte[] encrypted = cipher.doFinal(string.getBytes(UTF_8));

        // base64 인코딩
        byte[] base64Encoded = Base64.encode(encrypted);
        String result = new String(base64Encoded, UTF_8);

        return result;
    }

    // AES 256복호화
    public static String decryptAES256(String string) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] key256Data = KEY_256.getBytes(UTF_8);
        byte[] key128Data = KEY_128.getBytes(UTF_8);

        // 운용모드 CBC, 패딩은 PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key256Data, "AES"), new IvParameterSpec(key128Data));

        // base64 디코딩
        byte[] base64Decoded = Base64.decode(string.getBytes(UTF_8));
        byte[] decrypted = cipher.doFinal(base64Decoded);
        String result = new String(decrypted, UTF_8);

        return result;
    }


    public static String encryptSHA256(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance(SHA_256);

        byte[] stringBytes = string.getBytes();
        int stringBytesLength = stringBytes.length;

        byte[] dataBytes = new byte[1024];
        dataBytes = stringBytes.clone();
//        System.arraycopy(stringBytes, 0, dataBytes, 0, stringBytesLength);
//        for (int i = 0; i < stringBytesLength; i++) {
//            dataBytes[i] = stringBytes[i];
//        }
        messageDigest.update(dataBytes, 0, stringBytesLength);
        byte[] encrypted = messageDigest.digest();

        // base64 인코딩
        byte[] base64Encoded = Base64.encode(encrypted);
        String result = new String(base64Encoded, UTF_8);

        return result;
    }


    public static String encryptSha256Base64(String msg) {
        String logHeader = "HsUtil_Encode/encodeSha256Base64";
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256);
            byte[] hash = Base64.encode(digest.digest(msg.getBytes("UTF-8")));
            String encodedStr = new String(hash);
            GLog.printInfo(logHeader,"encodeSha256Base64(" + msg + ") -> (" + hash.length + "bytes) " + encodedStr);
            return encodedStr;
        } catch (NoSuchAlgorithmException e) {
            GLog.printExceptWithSaveToPciDB(logHeader,"get SHA-256 MessageDigest instance fail: " + e.getMessage(), e, ErrorInfo.CODE_ETC_ENCRYPTION_ERR, ErrorInfo.CATEGORY_ETC);
        } catch (UnsupportedEncodingException e) {
            GLog.printExceptWithSaveToPciDB(logHeader,"get message UTF-8 bytes fail: " + e.getMessage(), e, ErrorInfo.CODE_ETC_ENCRYPTION_ERR, ErrorInfo.CATEGORY_ETC);
        }
        return null;
    }

}


