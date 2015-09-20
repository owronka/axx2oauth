package com.axxessio.oauth2.server.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.shiro.crypto.hash.Sha384Hash;
import org.apache.tomcat.util.codec.binary.Base64;


public class Helper {

	/**
	 * Simple helper method to validate if string is empty or not.
	 * @param s: String to validate
	 * @return true if string is not empty
	 */
	public static boolean isEmpty (String s) {
		return (s == null | "".equals(s)) ? true : false;
	}
	
	 
	public static String getSHA (String value, String algorithm) throws NoSuchAlgorithmException{
		if (isEmpty(value) || isEmpty(algorithm)) return null;
		
		MessageDigest md = MessageDigest.getInstance(algorithm);

		md.update(value.toString().getBytes());
	
		return Base64.encodeBase64URLSafeString(md.digest());
	 }
	
	public static String getSaltedPwdHash (String password, String salt) {
		Sha384Hash sh = new Sha384Hash(password, salt, 100);
		return sh.toHex();
	}
}
