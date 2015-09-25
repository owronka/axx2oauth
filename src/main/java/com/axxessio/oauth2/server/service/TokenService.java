package com.axxessio.oauth2.server.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.axxessio.oauth2.server.cipher.AsymetricCipher;
import com.axxessio.oauth2.server.common.ApplicationException;
import com.axxessio.oauth2.server.common.Enums;
import com.axxessio.oauth2.server.common.Helper;
import com.axxessio.oauth2.server.service.pdo.AccessToken;
import com.axxessio.oauth2.server.service.pdo.SessionScope;
import com.axxessio.oauth2.server.service.pdo.User;

@Service
public class TokenService {

	@PersistenceContext(unitName="OAuth2")
	EntityManager em;

	private static final Logger logger = Logger.getLogger(TokenService.class);
	
	@Transactional
	public AccessToken createToken (User user, String sessionId, String username, String password, String grantType, String scope, SessionScope<String, String> sessionScope) throws ApplicationException {
		AccessToken at = null;
		
		// validate user
		if (user == null) {
			throw new ApplicationException(0x0001, "user for name [" + username + "] not found!");
		}

		// user password comes in as SHA1 hash. This hash is salted with user salt from DB and SHA384 computed. 
		// This is then compared with user password in DB
		if (! Helper.getSaltedPwdHash(password, user.getSalt()).equals(user.getPassword())) {
			throw new ApplicationException(0x0002, "user or password not valid");
		}
		
		if (grantType.equals("password")) {
			// ok, this is a request for an new token, therefore we have to generate a new sessionId for security reasons
			at = new AccessToken(generateAccessToken(sessionId), "bearer", 3600, generateCryptoHash(sessionId, "SHA"), sessionId, username, sessionScope);
		} 
		
		// this makes REST service more stable, user can request access token several times, only the last is valid 
		deleteAccessToken(sessionId, null);
		
		// all tokens are stored in db.
		setAccessToken(at);
		
		return at;
	}

	@Transactional
	public void deleteAccessToken (String sessionId, String refreshToken) {
		Query query = em.createNamedQuery("AccessToken.deleteAccessToken");
		query.setParameter(1, sessionId);
		try {
			query.executeUpdate();
		} catch (Exception e) {
			logger.warn("Problem - can not delete refresh_token [" + refreshToken + "] for session [" + sessionId + "] from the database");
		}
	}
	
	public AccessToken getAccessToken (String token, Enums.TOKEN_TYPE tt) {
		Query query = em.createNamedQuery(tt == Enums.TOKEN_TYPE.ACCESS ? "AccessToken.findAccessTokenByAccessToken" : "AccessToken.findAccessTokenByRefreshToken");
		query.setParameter(1, token);
		AccessToken accessToken = null;
		try {
			accessToken = (AccessToken) query.getSingleResult();
		} catch (Exception e) {
			logger.warn("Problem - can not find token [" + token +"] in the database");
		}
		return accessToken;	
	}
	
	public byte[] getTokenSign (AccessToken at) {
	    
		byte[] sign = null;
		StringBuffer sb = new StringBuffer();
		
		
		// we now concat all data we want to sign with private key of OAuth2 service
		sb.append(at.getAccessToken());
		sb.append(at.getCreatedAt().getTime());
		sb.append(at.getExpiresIn());
		
		Set<Entry<String, String>>s = at.getSessionScope().entrySet();
		
		Iterator<Entry<String, String>> it = s.iterator();
		
		while (it.hasNext()) {
			Entry<String, String> e = it.next();
			
			sb.append(e.getKey());
			sb.append(e.getValue());
		}
		
		logger.info(sb.toString());
		
		sign = AsymetricCipher.sign(sb.toString().getBytes());
		
		return sign;
    }

	public AccessToken refreshToken (String sessionId, String refreshToken) throws ApplicationException {
		AccessToken at = getAccessToken(refreshToken, Enums.TOKEN_TYPE.REFRESH);
		
		if (at == null || !at.getSessionId().equals(sessionId)) {
			throw new ApplicationException(0x0003, "refresh_token is not associated with sessionId");
		}

		at = new AccessToken(generateAccessToken(sessionId), "bearer", 3600, refreshToken, sessionId, at.getUser(), at.getSessionScope());
		
		// delete old token from db
		deleteAccessToken(sessionId, refreshToken);
		
		// all tokens are stored in db.
		setAccessToken(at);
		
		return at;
	}
	
	public AccessToken setAccessToken(AccessToken newAccessToken) {
		Query query = em.createNamedQuery("AccessToken.findAccessTokenByAccessToken");
		query.setParameter(1, newAccessToken.getAccessToken());
	
		AccessToken accessToken = null;
		
		try {
			accessToken = (AccessToken) query.getSingleResult();
		} catch (NoResultException e) {
			logger.info("new access token [" + newAccessToken.getAccessToken() +"] does not exist in the database");
		}
		if (accessToken == null) {
			em.persist(newAccessToken);
		} else {
		    // ok, update token
			accessToken.setAccessToken(newAccessToken.getAccessToken());
			accessToken.setExpiresIn(newAccessToken.getExpiresIn());
			accessToken.setRefreshToken(newAccessToken.getRefreshToken());
			accessToken.setSessionId(newAccessToken.getSessionId());
			
		    em.persist(accessToken);
		}
		
		return newAccessToken;
	}
	
	/**
	 * Helper method to generate a new access token. To prevent equal access tokens for same session id value is salted with current date.
	 * @param value
	 * @return: new access token
	 */
	private String generateAccessToken (String value) {
		Random r = new Random();
		return generateCryptoHash (value + Long.toString(r.nextLong()), "MD5");
	}

	/**
	 * Generates crypto hash for given value by using selected algorithm 
	 * @param value: String to generate crypto hash for
	 * @param algorithm: can be MD5 or SHA
	 * @return: hash value
	 */
	private String generateCryptoHash (String value, String algorithm) {
		MessageDigest md;
		
		try {
			md = MessageDigest.getInstance(algorithm);
			md.reset();
			
			md.update(value.getBytes());
			
			byte [] digest = md.digest();
			
			BigInteger bigInt = new BigInteger(1,digest);
			
			return bigInt.toString(16);
		} catch (Exception xcptn) {
			logger.error(xcptn.toString());
		}
		return null;
	}
}
