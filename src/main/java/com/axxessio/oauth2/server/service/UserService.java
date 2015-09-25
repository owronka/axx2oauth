package com.axxessio.oauth2.server.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.axxessio.oauth2.server.service.pdo.Role;
import com.axxessio.oauth2.server.service.pdo.SessionScope;
import com.axxessio.oauth2.server.service.pdo.User;

@Component
public class UserService {
	
	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("OAuth2");
	private static final EntityManager em = emf.createEntityManager();
	private static final Logger logger = Logger.getLogger(TokenService.class);
	
	public SessionScope<String, String> getSessionScope (String username, String[] scopes) {
		// now we read rights base on scope parameter
		User user = getUser (username);
		
		if (user == null) return null;
		
		Role role = user.getRoles().iterator().next();
		SessionScope<String, String> sessionScope = new SessionScope<String, String>();
				
		if (role != null && scopes != null) {
			for (int i = 0; i < scopes.length; i++) {
				sessionScope.put(scopes[i], role.rightsToString(scopes[i]));
			}
		}
		
		return sessionScope;
	}

	public Role getRole(long roleId) {
		Query query = em.createNamedQuery("Role.findRoleByRoleId");
		query.setParameter(1, roleId);
		Role role = null;
		try {
			role = (Role) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Problem - can not find role with id [" + roleId + "] in the database");
		}
		return role;
	}

	public Role getRole(String roleName) {
		Query query = em.createNamedQuery("Role.findRoleByRoleName");
		query.setParameter(1, roleName);
		Role role = null;
		try {
			role = (Role) query.getSingleResult();
		} catch (Exception e) {
			logger.warn("Problem - can not find role [" + roleName + "] in the database");
		}
		return role;
	}
	
	public User getUser (String name) {
		Query query = em.createNamedQuery("User.findUserByName");
		query.setParameter(1, name);
		User user = null;
		try {
			user = (User) query.getSingleResult();
		} catch (Exception e) {
			logger.warn("Problem - can not find user [" + name + "] in the database");
		}
		return user;	
	}
	
}
