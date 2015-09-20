package com.axxessio.oauth2.server.controller.to;

import java.security.interfaces.DSAPublicKey;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.tomcat.util.codec.binary.Base64;

@XmlRootElement
public class PublicKeyTO {
	String algorithm;
	String encoding = "base64";
	String format;
	String y;
	String g;
	String p;
	String q;
	
	
	public PublicKeyTO () {
	}
	
	public PublicKeyTO (DSAPublicKey key) {
		this.algorithm = key.getAlgorithm();
		this.format = key.getFormat();

		this.y    = Base64.encodeBase64String(key.getY().toByteArray());
		this.g = Base64.encodeBase64String(key.getParams().getG().toByteArray());
		this.p = Base64.encodeBase64String(key.getParams().getP().toByteArray());
		this.q = Base64.encodeBase64String(key.getParams().getQ().toByteArray());
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getFormat() {
		return format;
	}

	public String getY() {
		return y;
	}

	public String getG() {
		return g;
	}

	public String getP() {
		return p;
	}

	public String getQ() {
		return q;
	}
}
