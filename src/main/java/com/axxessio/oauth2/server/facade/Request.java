package com.axxessio.oauth2.server.facade;

import com.axxessio.oauth2.server.common.Helper;
import com.axxessio.oauth2.server.common.ApplicationException;

public class Request {
	public static int BAD_REQUEST = 1;
	public static int NOT_FOUND = 2;
	
	private String accept;
	private String authorization;
	private String contentType;
	private String grantType;
	private String password;
	private String refreshToken;
	private String scope;
	private String username;
	
	public Request (String newAccept, String newAuthorization) throws ApplicationException{
		this.accept = newAccept;
		this.authorization = newAuthorization;

		if (Helper.isEmpty (accept)) {
			throw new ApplicationException(0x0010, "No Accept: header found!", BAD_REQUEST);
		}
		
		if (Helper.isEmpty (authorization)) {
			throw new ApplicationException(0x0011, "No Authorization: header found!", NOT_FOUND);
		}
	}

	public Request (String newAccept, String newAuthorization, String newContentType, String newGrantType, String newPassword, 
			        String newRefreshToken, String newScope, String newUsername) throws ApplicationException{
		this.accept = newAccept;
		this.authorization = newAuthorization;
		this.contentType = newContentType;
		this.grantType = newGrantType;
		this.password = newPassword;
		this.refreshToken = newRefreshToken;
		this.scope = newScope;
		this.username = newUsername;

		if (Helper.isEmpty (accept)) {
			throw new ApplicationException(0x0012, "No Accept: header found!", BAD_REQUEST);
		}
		
		if (Helper.isEmpty (authorization)) {
			throw new ApplicationException(0x0013, "No Authorization: header found!", NOT_FOUND);
		}
		
		if (Helper.isEmpty (contentType)) {
			throw new ApplicationException(0x0014, "No Content-Type: header found!", NOT_FOUND);
		}
		
		if (!contentType.startsWith("application/x-www-form-urlencoded")) {
			throw new ApplicationException(0x0015, "Content-Type: header must be 'application/x-www-form-urlencoded'!", BAD_REQUEST);
		}
		
		if (Helper.isEmpty (grantType)) {
			throw new ApplicationException(0x0016, "No grant_type form parameter found!", NOT_FOUND);
		}
		
		if (!grantType.equals("password") && !grantType.equals("refresh_token")) {
			throw new ApplicationException(0x0017, "grant_type must be 'password' or 'refresh_token'!", BAD_REQUEST);
		}

		if (grantType.equals("password")) {
			if (Helper.isEmpty (username)){
				throw new ApplicationException(0x0019, "username parameter must not be null!", BAD_REQUEST);
			}
			if (Helper.isEmpty (password)){
				throw new ApplicationException(0x0018, "password parameter must not be null!", BAD_REQUEST);
			}
		} else {
			if (Helper.isEmpty (refreshToken)){
				throw new ApplicationException(0x001A, "refresh_token parameter must not be null!", BAD_REQUEST);
			}
		}
	}
	
	String[] getScopes () {
		if (scope == null) return null;

		if (scope.startsWith("~")) {
			return scope.substring(1, scope.length()).split("~");
		} else {
			return scope.split("~");
		}
	}
}
