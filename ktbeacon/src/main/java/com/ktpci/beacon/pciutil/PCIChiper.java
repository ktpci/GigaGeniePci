package com.ktpci.beacon.pciutil;

import android.os.Build;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.ktpci.beacon.pciutil.PCITime.currentDate;
import static com.ktpci.beacon.pciutil.PCITime.currentTime;
import static com.ktpci.beacon.pciutil.PCITime.preDate;

public class PCIChiper {
    public static final String CHIPER = "kt";

    public String PCIChiper(String googleAdid) {
        String eAdid = googleAdid;
        int cTime = currentTime("HHmm");
        String cDate = currentDate("yyyyMMdd");
        String pDate = preDate("yyyMMdd");

        if(cTime < 400) {
            eAdid = Encrypt(googleAdid, pDate);
        }else{
            eAdid = Encrypt(googleAdid, cDate);
        }
        return eAdid;

    }

    public static byte[] getBytesFromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

    public static UUID getUUIDFromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        Long high = byteBuffer.getLong();
        Long low = byteBuffer.getLong();

        return new UUID(high, low);
    }

    String arrayToString(byte[] input) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return "[ " + IntStream.range(0, input.length)
                    .mapToObj(i -> ""+(input[i]&0xFF))
                    .collect(Collectors.joining(", ")) + " ]";
        }
        return null;
    }

    static byte[] encAesCbc(byte[] toEncrypt, byte[] aesKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
            byte[] encrypted = cipher.doFinal(toEncrypt);
            return Arrays.copyOfRange(encrypted, encrypted.length-16, encrypted.length);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("This should never happen?",e);
        }

    }


    static byte[] decAesCbc(byte[] toDecrypt, byte[] aesKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
            byte[] encrypted = cipher.doFinal(toDecrypt);
            return Arrays.copyOfRange(encrypted, encrypted.length-16, encrypted.length);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("This should never happen?",e);
        }
    }

    static byte[] sha1(String text, int length) {
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                crypt.update(text.getBytes(StandardCharsets.UTF_8));
            }
            return Arrays.copyOfRange(crypt.digest(), 0, length);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String Encrypt(String adid, String date) {

        UUID adidUUID = UUID.fromString(adid);
        byte[] uuidByte = getBytesFromUUID(adidUUID);

        String today = CHIPER + date;
//        long now = System.currentTimeMillis(); // current Time
//        Date date = new Date(now);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        today = sdf.format(date);

        byte[] keyFromSha1 = sha1(today, 16); //sha1 key
        byte[] encrypted = encAesCbc(uuidByte, keyFromSha1);

        return getUUIDFromBytes(encrypted).toString();
    }

    public static String Decrypt(String encryptAdid, String key) {
        UUID encyptUUID = UUID.fromString(encryptAdid);
        byte[] encyptUUIDByte = getBytesFromUUID(encyptUUID);
        byte[] keyFromSha1 = sha1(CHIPER + key, 16);
        byte[] dec = decAesCbc(encyptUUIDByte, keyFromSha1);
        return getUUIDFromBytes(dec).toString();
    }

}