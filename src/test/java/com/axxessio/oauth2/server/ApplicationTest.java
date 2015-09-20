package com.axxessio.oauth2.server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class ApplicationTest {

	private static final Logger logger = Logger.getLogger("ApplicationTest");
	private CloseableHttpClient httpclient;
	private TokenHandler th;
	private UsernamePasswordCredentials creds;
	
	@BeforeClass
	public void setUp() throws AuthenticationException, ClientProtocolException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, URISyntaxException, IOException {
	    creds = new UsernamePasswordCredentials("system", "system");
	    httpclient = HttpClients.createDefault();
	    th = new TokenHandler("http://localhost:8080", "system", "system");
	    
	    th.setLogger(logger);
	}
	
	@Test
	public void test() {
		testCreateToken();
		
		testCheckToken();
		
		testRefreshToken();
		
		testDeleteToken();
	}
	
	void testCreateToken() {
		HttpPost httpPost = new HttpPost("http://localhost:8080/services/oauth2/token");
		CloseableHttpResponse response = null; 
	    HttpEntity requestBody;
	    HttpEntity responseBody;
	    
	    try {
			httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));
		    httpPost.addHeader("Accept", "application/json");
		    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
	
		    requestBody = new ByteArrayEntity("grant_type=password&username=admin&password=d033e22ae348aeb5660fc2140aec35850c4da997&scope=~customer~order".getBytes("UTF-8"));
		    
		    httpPost.setEntity(requestBody);
		    
			response = httpclient.execute(httpPost);
	
			responseBody = response.getEntity();

			th.setToken(responseBody.getContent());
			
			// check signature is valid
			assertTrue(th.isValid(response.getFirstHeader("Signature-Value").getValue()));
			
			// check timestamp is valid, otherwise refresh token must be called
			assertTrue(th.isValid());

			EntityUtils.consume(responseBody);
		    
		    assertTrue(th.hasRight("customer", 'c'));
		    assertTrue(th.hasRight("customer", 'r'));
		    assertTrue(th.hasRight("customer", 'u'));
		    assertTrue(th.hasRight("customer", 'd'));
		    assertTrue(th.hasRight("order", 'c'));
		    assertTrue(th.hasRight("order", 'r'));
		    assertTrue(th.hasRight("order", 'u'));
		    assertTrue(th.hasRight("order", 'd'));
		    
		} catch (AuthenticationException | IOException xcptn) {
			logger.severe(xcptn.toString());
		} finally {
		    if (response != null) {
				try {
					response.close();
				} catch (IOException ioxcptn) {
					logger.severe(ioxcptn.toString());
				}
		    }
		}		
	}
	
	void testCheckToken() {
		HttpGet httpGet = new HttpGet("http://localhost:8080/services/oauth2/token?access_token&" + th.getAccessToken());
		CloseableHttpResponse response = null; 
	    
	    try {
			httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet, null));
		    httpGet.addHeader("Accept", "application/json");
	
			response = httpclient.execute(httpGet);
	
			StatusLine sl = response.getStatusLine();
			
			// Status must be 200 OK
			assertTrue (sl.getStatusCode() == 200);
		    
		} catch (AuthenticationException | IOException xcptn) {
			logger.severe(xcptn.toString());
		} finally {
		    if (response != null) {
				try {
					response.close();
				} catch (IOException ioxcptn) {
					logger.severe(ioxcptn.toString());
				}
		    }
		}		
	}
	
	void testRefreshToken() {
		
	}
	
	void testDeleteToken() {
		
	}

}