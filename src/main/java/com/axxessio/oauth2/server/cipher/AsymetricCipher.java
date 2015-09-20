package com.axxessio.oauth2.server.cipher;

import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.util.logging.Logger;

import org.apache.shiro.codec.Base64;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class AsymetricCipher implements InitializingBean {
	private static KeyPairGenerator kpg;
	private static KeyPair kp;
	private static DSAPublicKey pubk;
	private static DSAPrivateKey prvk;
	private static Signature dsa;

	
	private static final Logger logger = Logger.getLogger(AsymetricCipher.class.getName());
	
	public void afterPropertiesSet () {		  
		// Generate a key-pair
		try {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
			kpg = KeyPairGenerator.getInstance("DSA", "SUN");
			kpg.initialize(1024, sr); // 512 is the keysize.
			kp = kpg.generateKeyPair();
			pubk = (DSAPublicKey) kp.getPublic();
			logger.info("Y:[" + pubk.getY() + "] G:[" + pubk.getParams().getG() + "] P:[" + pubk.getParams().getP() + "] Q:[" + pubk.getParams().getQ() + "]");
			prvk = (DSAPrivateKey) kp.getPrivate();
			dsa = Signature.getInstance("SHA1withDSA", "SUN");
		} catch (NoSuchAlgorithmException nsaxcptn) {
			logger.severe(nsaxcptn.toString());
		} catch (NoSuchProviderException nspxcptn) {
			logger.severe(nspxcptn.toString());
		}

	}

	public static byte[] sign(byte[] inpBytes) {
	
		try {
			dsa.initSign(prvk);
			dsa.update(inpBytes);

			logger.info("Algorithm [" + dsa.getAlgorithm() +"] Provider [" + dsa.getProvider() + "]");
			
			return dsa.sign();
		} catch (InvalidKeyException ikxcptn) {
			logger.severe(ikxcptn.toString());
		} catch (SignatureException sxcptn) {
			logger.severe(sxcptn.toString());
		}
		
		return null;
	}
	
	public static String sign(String inpString){
		try {
			dsa.initSign(prvk);
			dsa.update(inpString.getBytes());
			
			logger.info("Algorithm [" + dsa.getAlgorithm() +"] Provider [" + dsa.getProvider() + "]");
			
			return new String (Base64.encode(dsa.sign()));
		} catch (InvalidKeyException ikxcptn) {
			logger.severe(ikxcptn.toString());
		} catch (SignatureException sxcptn) {
			logger.severe(sxcptn.toString());
		}
		
		return null;
	}
	
	public static boolean verify(byte[] signature, byte[] inpBytes) {
		try {
			dsa.initVerify(pubk);
			dsa.update(inpBytes);
		
			return dsa.verify(signature);
		} catch (InvalidKeyException ikxcptn) {
			logger.severe(ikxcptn.toString());
		} catch (SignatureException sxcptn) {
			logger.severe(sxcptn.toString());
		}
		
		return false;
	}
	
	public static DSAPublicKey getPublicKey () {
		return pubk;
	}
}
