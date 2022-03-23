/*
 * PCI version 1.0.0
 *
 *  Copyright ⓒ [2017] kt corp. All rights reserved.
 *
 *  This is a proprietary software of kt corp, and you may not use this file except in
 *  compliance with license agreement with kt corp. Any redistribution or use of this
 *  software, with or without modification shall be strictly prohibited without prior written
 *  approval of kt corp, and the copyright notice above does not evidence any actual or
 *  intended publication of such software.
 */
package com.gnifrix.util;

import android.util.Log;

import com.gnifrix.util.base64.Base64;
import com.kt.gigagenie.pci.Global;
import com.kt.gigagenie.pci.system.PciProperty;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * AES 128 utils
 * @author SangJin
 *
 */
public class AES128Utils {

	private static String aesKey;
	/* KEY SIZE Constant */

	
	public static Key getAESKey() throws UnsupportedEncodingException {
	    String iv;
	    Key keySpec;
	    
	    //String key = "1234567890123456";
	    iv = aesKey.substring(0, 16);
	    byte[] keyBytes = new byte[16];
	    byte[] b = aesKey.getBytes("UTF-8");

	    int len = b.length;
	    if (len > keyBytes.length) {
	       len = keyBytes.length;
	    }

	    System.arraycopy(b, 0, keyBytes, 0, len);
	    keySpec = new SecretKeySpec(keyBytes, "AES");

	    return keySpec;
	}

	/**
	 * AES 암호화 for PCI
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String encAES(String str) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		PciProperty pciProperty = Global.getInstance().getPciProperty();
		if(pciProperty == null) return null;

		String key = pciProperty.getAes_key();
		if(!pciProperty.getAes_encrypt_use() || key == null || key.trim().equals("") || key.equals("null")){
			return str;
		}else{
			return encAES(str, key);
		}
	}

	/**
	 * AES 복호화 for PCI
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String decAES(String str) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		PciProperty pciProperty = Global.getInstance().getPciProperty();
		if(pciProperty == null) return null;

		String key = pciProperty.getAes_key();

		if(!pciProperty.getAes_encrypt_use() || key == null || key.trim().equals("") || key.equals("null")){
			return str;
		}else{
			return decAES(str, key);
		}

	}

	// 암호화
	public static String encAES(String str, String key) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		aesKey = key;
	    Key keySpec = getAESKey();
	    String iv = "0987654321654321";
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
	    byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
	    String enStr = new String(Base64.encode(encrypted));

//		GLog.printTest("AES128Util", "encrypt AES128 : [" + str + "] to [" + enStr + "], key=" + key);
		decAES(enStr, key);

	    return enStr;
	}

	// 복호화
	public static String decAES(String enStr,String str2) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		aesKey = str2;
	    Key keySpec = getAESKey();
	    String iv = "0987654321654321";
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
	    byte[] byteStr = Base64.decode(enStr.getBytes("UTF-8"));
	    String decStr = new String(c.doFinal(byteStr), "UTF-8");

//		GLog.printTest("AES128Util", "decrypt AES128 : [" + enStr + "] to [" + decStr + "], key=" + str2);

	    return decStr;
	}
	
	public static void main(String[] args) {
		try {
			// 1.AES-128 비트 대칭키 생
			//keyGenerator(ALGO_AES, KEY_SIZE_128);
			//keyGenerator();
			// 2. 암호화 수행 			
			//byte[] encrptedByte = encrypt(plainText);
			String key = "1234567890123456";
			String enStr = encAES("948BC1258FF2" , key);
//			System.out.println("encrypted string: " + enStr);
			// 3. 복호화 수행
			String decrptedByte = decAES("52884affdaee71381d584be725192a61",key);
//			System.out.println("decrypted string: " + decrptedByte );
			//System.out.println("kgen.getAlgorithm(): " + kgen.getAlgorithm());
			//System.out.println("skey.getEncoded()(): " + new String(skey.getEncoded()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*private static final int KEY_SIZE_128 = 128;
	private static final int KEY_SIZE_256 = 256;	

	 DES 
	private static final String ALGO_DES = "DES";
	private static final String ALGO_AES = "AES";
	private static final String ALGO_SEED = "SEED";
	 AES 

	 SEED 
	private static String plainText = "1a2sv3b3f5rr6";
	private static KeyGenerator kgen = null;
	static SecretKey skey = null;
	static String sKeyHex = null;	

	private static SecretKeySpec keyGenerator(String key ) {
		SecretKeySpec keySpec = null;
		try {
			kgen = KeyGenerator.getInstance(ALGO_AES);
			kgen.init(KEY_SIZE_128);
			String iv;

		    iv = key.substring(0, 16);
		    byte[] keyBytes = new byte[16];
		    byte[] b = key.getBytes("UTF-8");

		    int len = b.length;
		    if (len > keyBytes.length) {
		       len = keyBytes.length;
		    }

		    System.arraycopy(b, 0, keyBytes, 0, len);
		    keySpec = new SecretKeySpec(keyBytes, "AES");
		    
			if(cipher.equals(ALGO_AES)) {
				kgen = KeyGenerator.getInstance(cipher);
			} else {
				return ;
			}
			kgen.init(keySize);
			skey = kgen.generateKey();
			// 비밀 키를 이렇게 저장하여 사용하면 암호화/복호화가 편해진다.
			sKeyHex = Hex.encodeHexString(skey.getEncoded());
			System.out.println("Key as Hex : " + sKeyHex); // 128bit = 16bytes
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keySpec;
	}

	public static String encAES(String plainText , String key) {
		//SecretKeySpec skeySpec = new SecretKeySpec(skey.getEncoded(), kgen.getAlgorithm());
		SecretKeySpec skeySpec =keyGenerator(key);
		try {
			Cipher cipher = Cipher.getInstance(kgen.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(plainText.getBytes());
			return Hex.encodeHexString(encrypted) ;	
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 	
	}
	public static String decAES(String enstr, String key) {
			 Key Spec 생성 
			//SecretKeySpec skeySpec = new SecretKeySpec(skey.getEncoded(), kgen.getAlgorithm());
		SecretKeySpec skeySpec =keyGenerator(key);
		try {
			
			 Cipher object 
			Cipher cipher = Cipher.getInstance(kgen.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);		
			byte[] original = cipher.doFinal(Hex.decodeHex(enstr.toCharArray()));			
			return new String(original,"UTF-8")  ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		try {
			// 1.AES-128 비트 대칭키 생
			//keyGenerator(ALGO_AES, KEY_SIZE_128);
			//keyGenerator();
			// 2. 암호화 수행 			
			//byte[] encrptedByte = encrypt(plainText);
			String key = "1234567890123456";
			String enStr = encAES(plainText , key);
			System.out.println("encrypted string: " + enStr);
			// 3. 복호화 수행
			String decrptedByte = decAES("52884affdaee71381d584be725192a61",key);
			System.out.println("decrypted string: " + decrptedByte );
			System.out.println("kgen.getAlgorithm(): " + kgen.getAlgorithm());
			//System.out.println("skey.getEncoded()(): " + new String(skey.getEncoded()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	
	
	/*public static Key getAESKey(String key) throws Exception {
	    String iv;
	    Key keySpec;

	    iv = key.substring(0, 16);
	    byte[] keyBytes = new byte[16];
	    byte[] b = key.getBytes("UTF-8");

	    int len = b.length;
	    if (len > keyBytes.length) {
	       len = keyBytes.length;
	    }

	    System.arraycopy(b, 0, keyBytes, 0, len);
	    keySpec = new SecretKeySpec(keyBytes, "AES");

	    return keySpec;
	}

	// 암호화
	public static String encAES(String str,String key) throws Exception {
	    Key keySpec = getAESKey(key);
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.ENCRYPT_MODE, keySpec);
	    byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
	    String enStr = new String(Base64.encodeBase64(encrypted));
	    byte[] iv2 = key.getBytes();
	    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv2);
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

	    byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
	    String enStr = new String(Base64.encodeBase64(encrypted));

	    return enStr;
	}

	// 복호화
	public static String decAES(String enStr,String key) throws Exception {
	    Key keySpec = getAESKey(key);	    
	    //c.init(Cipher.DECRYPT_MODE, keySpec);
	    
	    byte[] iv=key.getBytes();
	    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
	    
	    byte[] byteStr = Base64.decodeBase64(enStr.getBytes("UTF-8"));
	    String decStr = new String(c.doFinal(byteStr), "UTF-8");

	    return decStr;
	}*/
}
