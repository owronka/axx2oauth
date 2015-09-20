package com.axxessio.oauth2.server.controller.to;

import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.axxessio.oauth2.server.service.pdo.AccessToken;

@XmlRootElement
public class AccessTokenTO extends GenericTO{
	String   access_token;
	long     created_at;
	int      expires_in;
	String   refresh_token;
	Map<String, String> scope;
	String   token_type;

	public AccessTokenTO () {
	}
	
	public AccessTokenTO (AccessToken ac) {
		this.access_token = ac.getAccessToken();
		this.created_at = ac.getCreatedAt().getTime();
		this.expires_in = ac.getExpiresIn();
		this.refresh_token = ac.getRefreshToken();
		this.scope = ac.getSessionScope();
		this.token_type = ac.getTokenType();
	}
	
	public String getAccess_token() {
		return access_token;
	}

	public String getToken_type() {
		return token_type;
	}

	public long getCreated_at() {
		return created_at;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public Map<String, String> getScope() {
		return scope;
	}

	public void setScope(Map<String, String> newScope) {
		this.scope = newScope;
	}
}
