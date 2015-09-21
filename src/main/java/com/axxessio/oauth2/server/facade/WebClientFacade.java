package com.axxessio.oauth2.server.facade;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.axxessio.oauth2.server.cipher.AsymetricCipher;
import com.axxessio.oauth2.server.common.ApplicationException;
import com.axxessio.oauth2.server.common.Enums;
import com.axxessio.oauth2.server.controller.to.AccessTokenTO;
import com.axxessio.oauth2.server.controller.to.ErrorMessageTO;
import com.axxessio.oauth2.server.controller.to.PublicKeyTO;
import com.axxessio.oauth2.server.service.TokenService;
import com.axxessio.oauth2.server.service.UserService;
import com.axxessio.oauth2.server.service.pdo.AccessToken;
import com.axxessio.oauth2.server.service.pdo.SessionScope;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/services/oauth2")
public class WebClientFacade {
	
	@Autowired
	TokenService ts;
	
	@Autowired
	UserService us;
	
	private static final Logger logger = Logger.getLogger(TokenService.class);
	
	/**
	 * This method accepts POST requests to generate new or update existing access token.
	 *  
	 * For a new token with scope for customer and order please send the following request
	 * <pre>
	 * POST http://{server}:{port}/services/oauth2/token
	 * 
	 * == Header ==
     * Accept: application/json
     * Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
     * Content-Type: application/x-www-form-urlencoded
     *
     * == Body ==
     * grant_type=password&username=admin&password=d033e22ae348aeb5660fc2140aec35850c4da997&scope=~customer~order
	 * </pre>
	 * 
	 * To refresh a token please send the following request
	 * <pre>
	 * POST http://{server}:{port}/services/oauth2/token
	 * 
	 * == Header ==
     * Accept: application/json
     * Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
     * Content-Type: application/x-www-form-urlencoded
     *
     * == Body ==
     * grant_type=refresh_token&refresh_token=############################
	 * </pre>
	 * @param accept
	 * @param authorization
	 * @param contentType
	 * @param host
	 * @param grantType
	 * @param password
	 * @param refreshToken
	 * @param username
	 * @param request
	 * @return JSON object with access and refresh token, timestamp, scope and associated rights
	 */
	@RequestMapping(value="/token", method=RequestMethod.POST)
	public ResponseEntity<?> createToken(@RequestHeader(value="Accept") String accept, 
									     @RequestHeader(value="Authorization") String authorization,
									     @RequestHeader(value="Content-Type") String contentType,
									     @RequestHeader(value="Host") String host,
									     @RequestBody MultiValueMap<String, String> body,
									     HttpServletRequest request) {
		
		String grantType = body.getFirst("grant_type");
		String password = body.getFirst("password");
		String refreshToken = body.getFirst("refresh_token");
		String scope = body.getFirst("scope");
		String username = body.getFirst("username");

		try {
			// validate request
			AccessToken at;
			AccessTokenTO ato;

			Request r = new Request(accept, authorization, contentType, grantType, host, password, refreshToken, scope, username);
		
			SessionScope<String, String> sessionScope;
			String   sessionId;
			
			logger.info("=================== Header =================");
			logger.info("Accept: " + accept + " - Authorization: " + authorization + " - Content-Type: " + contentType + " - Host: " + host);
			logger.info("=================== Form =================");
			logger.info("grant_type [" + grantType + "] - username  [" + username + "] - password [" + password + "] - refresh_token [" + refreshToken + "]");
	
			sessionScope = us.getSessionScope(username, r.getScopes());
			
			if (grantType.equals("password")) {
				sessionId = request.getSession(true).getId();
				
				at = ts.createToken(us.getUser(username), sessionId, username, password, grantType, scope, sessionScope);
			} else {
				// only token refresh, therefore only new access token is generated, refresh_token remains the same
				sessionId = request.getSession().getId();
				
				at = ts.refreshToken(sessionId, refreshToken);
			}

			ato = new AccessTokenTO (at); 
			
			HttpHeaders rh = new HttpHeaders();
			rh.set("Cache-Control", "no-store");
			rh.set("Pragman", "no-cache");
			
			rh.set("Signature-Algorithm", "DSASHA1");
			rh.set("Signature-Encoding", "base64");
			rh.set("Signature-Value", Base64.encodeBase64String(ts.getTokenSign(at))); 
			rh.set("Token-Encoding", "base64");
			rh.set("Token-Value", Base64.encodeBase64String(ato.toJSON().getBytes()));
					
			return new ResponseEntity<AccessTokenTO>(ato, rh, HttpStatus.OK);

		} catch (ApplicationException axcptn) {
			return buildStatusResponse(getResponseStatus(axcptn), axcptn.getErrorCode(), axcptn.getErrorDescription());
		} catch (JsonProcessingException jpxcptn) {
			return buildStatusResponse(HttpStatus.INTERNAL_SERVER_ERROR, 0x0001, "Error in processoing JSON mapping");
		}
	}

	/**
	 * This method is not part of OAuth2 specification. The intention is to delete a given token. 
	 * This is usually be done by client.
	 * <pre>
	 * DELETE http://{server}:{port}/services/oauth2/token?refresh_token=############################ 
	 * 
	 * == Header ==
     * Host: oauth2server.axxessio.com
     * Accept: application/json
     * Authorization: Basic c3lzdGVtOnN5c3RlbQ== (system:system)
     *
	 * @param accept
	 * @param authorization
	 * @param refreshToken
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/token", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteToken(@RequestHeader(value="Accept") String accept, 
									     @RequestHeader(value="Authorization") String authorization,
									     @RequestParam(value="refresh_token") String refreshToken,
									     HttpServletRequest request) {

		logger.info("DELETE /token - refresh_token: " + refreshToken);

		// Do some validation regarding parameters
		try {
			new Request(accept, authorization);
		} catch (ApplicationException axcptn) {
			return buildStatusResponse(getResponseStatus(axcptn), axcptn.getErrorCode(), axcptn.getErrorDescription());
		}
		ts.deleteAccessToken(request.getSession().getId(), refreshToken);

		return new ResponseEntity<String> ("token delted", HttpStatus.OK);
	}
	
	/**
	 * This method is not part of OAuth2 specification. The intention is to validate whether a given token is still valid or not. 
	 * This is usually be done be a resource server, not by a client.
	 * <pre>
	 * GET http://{server}:{port}/services/oauth2/pubkey
	 * 
	 * == Header ==
     * Accept: application/json
     * Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
     *
	 * @return public key for DSA algorithm in base64 encoded format.
	 */
	@RequestMapping(value="/pubkey", method=RequestMethod.GET)
	public ResponseEntity<?> getPublicKey(@RequestHeader(value="Accept") String accept, 
									   	  @RequestHeader(value="Authorization") String authorization) {

		logger.info("GET /pubkey");
		
		// Do some validation regarding parameters
		try {
			new Request(accept, authorization);
		} catch (ApplicationException axcptn) {
			return buildStatusResponse(getResponseStatus(axcptn), axcptn.getErrorCode(), axcptn.getErrorDescription());
		}
		
		// return new ResponseEntity<PublicKeyTO> (new PublicKeyTO(AsymetricCipher.getPublicKey()), HttpStatus.OK);
		PublicKeyTO pkto = new PublicKeyTO(AsymetricCipher.getPublicKey());
		
		return new ResponseEntity<PublicKeyTO>(pkto, HttpStatus.OK);
	}
	
	/**
	 * This method is not part of OAuth2 specification. The intention is to validate whether a given token is still valid or not. 
	 * This is usually be done be a resource server, not by a client.
	 * <pre>
	 * GET http://{server}:{port}/services/oauth2/token?access_token=############################
	 * 
	 * == Header ==
     * Host: oauth2server.axxessio.com
     * Accept: application/json
     * Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
     *
	 * @param accept
	 * @param authorization
	 * @param accessToken
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/token", method=RequestMethod.GET)
	public ResponseEntity<?> getToken(@RequestHeader(value="Accept") String accept, 
						   		      @RequestHeader(value="Authorization") String authorization,
						   		      @RequestParam(value="access_token") String accessToken) {

		logger.info("GET /token - access_token: " + accessToken);
		
		// Do some validation regarding parameters
		try {
			new Request(accept, authorization);
		} catch (ApplicationException axcptn) {
			return buildStatusResponse(getResponseStatus(axcptn), axcptn.getErrorCode(), axcptn.getErrorDescription());
		}
		
		if (ts.getAccessToken(accessToken, Enums.TOKEN_TYPE.ACCESS) == null)
			return buildStatusResponse(HttpStatus.NOT_FOUND, 0x0004, "access_token not valid!");			
		else
			return new ResponseEntity<String> ("token is valid", HttpStatus.OK);
	}
	
	private HttpStatus getResponseStatus (ApplicationException axcptn) {
		return axcptn.getErrorStatus() == Request.BAD_REQUEST ? HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND;
	}
	
	private ResponseEntity<ErrorMessageTO> buildStatusResponse (HttpStatus hs, int errorCode, String errorDescription) {
		ErrorMessageTO emto = new ErrorMessageTO("invalid_request", errorCode, errorDescription);
		
		return new ResponseEntity<ErrorMessageTO>(emto, hs);
	}
}