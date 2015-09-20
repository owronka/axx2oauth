package com.axxessio.oauth2.server;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.codec.Base64;

import com.axxessio.oauth2.server.controller.to.AccessTokenTO;
import com.axxessio.oauth2.server.controller.to.PublicKeyTO;

public class TokenHandler {
	private static final ObjectMapper mapper = new ObjectMapper();

	private static Logger logger; 
	private static Signature dsa;
	
	private AccessTokenTO ato;
	
	private PublicKeyTO  pubKey;
	private DSAPublicKey dsaPubKey;
	
	private CloseableHttpClient httpclient = HttpClients.createDefault();
	private CloseableHttpResponse response; 
	private HttpEntity responseBody;
	private HttpGet httpGet;

	private UsernamePasswordCredentials creds;

	public TokenHandler(String url, String usr, String pwd) throws AuthenticationException, URISyntaxException, ClientProtocolException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		dsa = Signature.getInstance("SHA1withDSA", "SUN");
		httpGet = new HttpGet();
		creds = new UsernamePasswordCredentials(usr, pwd);

		logger.info("Algorithm [" + dsa.getAlgorithm() +"] Provider [" + dsa.getProvider() + "]");

		httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet, null));
	    httpGet.addHeader("Accept", "application/json");
		
		httpGet.setURI(new URI(url + "/services/oauth2/pubkey"));
		
		response = httpclient.execute(httpGet);
		
		responseBody = response.getEntity();
		
		pubKey = mapper.readValue(responseBody.getContent(), PublicKeyTO.class);
		
		BigInteger y = new BigInteger (Base64.decode(pubKey.getY()));
		BigInteger g = new BigInteger (Base64.decode(pubKey.getG()));
		BigInteger p = new BigInteger (Base64.decode(pubKey.getP()));
		BigInteger q = new BigInteger (Base64.decode(pubKey.getQ()));
		
		logger.info("Y:[" + y + "] G:[" + g + "] P:[" + p + "] Q:[" + q + "]");
		
		DSAPublicKeySpec pks = new DSAPublicKeySpec (y, p, q, g);
		
		dsaPubKey = (DSAPublicKey) KeyFactory.getInstance("DSA", "SUN").generatePublic(pks);
		
		logger.info("Y:[" + dsaPubKey.getY() + "] G:[" + dsaPubKey.getParams().getG() + "] P:[" + dsaPubKey.getParams().getP() + "] Q:[" + dsaPubKey.getParams().getQ() + "]");

		EntityUtils.consume(responseBody);
	}
	
	public String getAccessToken () {
		return ato != null ? ato.getAccess_token() : null;
	}
	
	public String getRefreshToken () {
		return ato != null ? ato.getRefresh_token() : null;
	}
	
	public boolean isValid (String signatureValue){
		try {
			dsa.initVerify(dsaPubKey);
		} catch (InvalidKeyException ikxcptn) {
			if (logger != null) logger.severe(ikxcptn.toString());
			
			return false;
		}
		try {
			dsa.update(getSignedTokenValues(ato));

			return dsa.verify(Base64.decode(signatureValue));		
			
		} catch (SignatureException sxcptn) {
			if (logger != null) logger.severe(sxcptn.toString());
			
			return false;
		}
	}
		
	public boolean isValid (){
		Calendar now = new GregorianCalendar();
		Calendar created = new GregorianCalendar();
		
		
		if (ato == null) return false;
		
		created.setTimeInMillis(ato.getCreated_at());
		created.add(Calendar.SECOND, ato.getExpires_in());

		return created.after(now) ? true : false; 
	}
		
	public boolean hasRight(String scope, char right) {
		Map<String, String> scopeMap = ato.getScope();
		String rights = scopeMap.get(scope);
		
		if (rights != null) {
			if (rights.lastIndexOf(right) != -1) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isOnline (AccessTokenTO ato) {
		return true;
	}


	public void setLogger (Logger newLogger) {
		logger = newLogger;
	}

	public void setToken (InputStream is) throws IOException, JsonMappingException {
		ato = mapper.readValue(is, AccessTokenTO.class);

		System.out.println("Algorithm [" + dsaPubKey.getAlgorithm() +"] Key [" + Base64.encodeToString(dsaPubKey.getEncoded()) + "] Format [" + dsaPubKey.getFormat() + "]");
	}
	
	private byte[] getSignedTokenValues (AccessTokenTO ato) {
	    
		StringBuffer sb = new StringBuffer();
		
		
		// we now concat all data we want to sign with private key of OAuth2 service
		sb.append(ato.getAccess_token());
		sb.append(ato.getCreated_at());
		sb.append(ato.getExpires_in());
		
		Set<Entry<String, String>>s = ato.getScope().entrySet();
		
		Iterator<Entry<String, String>> it = s.iterator();
		
		while (it.hasNext()) {
			Entry<String, String> e = it.next();
			
			sb.append(e.getKey());
			sb.append(e.getValue());
		}
		
		logger.info(sb.toString());
		
		return sb.toString().getBytes();
    }

	
}
