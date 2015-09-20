package com.axxessio.oauth2.server.service.pdo;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({
		@NamedQuery(name = "AccessToken.findAccessTokenByAccessToken", query = "select a from AccessToken a where a.accessToken=?1 "),
		@NamedQuery(name = "AccessToken.findAccessTokenByRefreshToken", query = "SELECT a FROM AccessToken a WHERE a.refreshToken=?1"),
		@NamedQuery(name = "AccessToken.deleteAccessToken", query = "DELETE FROM AccessToken a WHERE a.sessionId=?1")
})

@Table(name="A2O_ACCESS_TOKEN")
public class AccessToken implements Serializable {
	private static final long serialVersionUID = 1L;

	private long   id;
	private String accessToken;
	private String tokenType;
	private int    expiresIn;
	private String refreshToken;
	private String sessionId;
	private Date   createdAt;
	private String user;
	private SessionScope<String, String> sessionScope;
	
	public AccessToken () {
	}
	
	public AccessToken (String newAccessToken, String newTokenType, int newExpiresIn, String newRefreshToken, String newSessionId, String newUser, SessionScope<String, String> newSessionScope) {
		this.accessToken = newAccessToken;
		this.tokenType = newTokenType;
		this.expiresIn = newExpiresIn;
		this.refreshToken = newRefreshToken;
		this.sessionId = newSessionId;
		this.createdAt = new Date();
		this.user = newUser;
		this.sessionScope = newSessionScope;
	}

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="access_token_seq")
    @SequenceGenerator(name="access_token_seq",sequenceName="A2O_ACCESS_TOKEN_ID", allocationSize=10)
    @Column(name = "AT_ID")
    public long getId() {
        return this.id;
    }


    public void setId(long id) {
        this.id = id;
    }


    @Column(name = "AT_ACCESS_TOKEN")
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

    @Column(name = "AT_TOKEN_TYPE")
	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

    @Column(name = "AT_EXPIRES_IN")
	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

    @Column(name = "AT_REFRESH_TOKEN")
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

    @Column(name = "AT_SESSION_ID")
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "AT_CREATED_AT")
    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "AT_USER")
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Lob
	@Column(name = "AT_SESSION_SCOPE")
	public SessionScope<String, String>getSessionScope() {
		return this.sessionScope;
	}

	public void setSessionScope(SessionScope<String, String> sessionScope) {
		this.sessionScope = sessionScope;
	}
}
