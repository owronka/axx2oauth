package com.axxessio.oauth2.server.service.pdo;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


/**
 * The persistent class for the T_ROLE database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Role.findAllRoles", query = "SELECT r FROM Role r ORDER BY r.name ASC"),
	@NamedQuery(name = "Role.findRoleByRoleId", query = "SELECT r FROM Role r WHERE r.id=?1"),
	@NamedQuery(name = "Role.findRoleByRoleName", query = "SELECT r FROM Role r WHERE r.name=?1 ")
})
@Table(name="A2O_ROLE")
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
    private String name;
    private Set<Right> rights;
    private Set<User> users;

    public Role() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "RO_ID")
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

    @Column(name = "RO_NAME", nullable=false, length=20)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

    //bi-directional many-to-many association to Right
    @ManyToMany(mappedBy="roles", fetch=FetchType.EAGER)
	public Set<Right> getRights() {
		return this.rights;
	}
    
    public String rightsToString (String scope) {
		Iterator<Right> rit = rights.iterator();
		Hashtable<String, String> ht = new Hashtable<String, String>();
		String rights;
		
		while (rit.hasNext()) {
			Right right = rit.next();

			if (ht.containsKey(scope)) {
				rights = ht.get(scope);
				rights += right.getRight();
				ht.put(scope, rights);
			} else {
				ht.put(scope, right.getRight());
			}
		}

		rights = new String();
		Iterator<String> kit = ht.keySet().iterator();
		
		while (kit.hasNext()) {
			String key = kit.next();
			String value = ht.get(key);
			
			if (value.contains("C")) rights += "c";
			if (value.contains("R")) rights += "r";
			if (value.contains("U")) rights += "u";
			if (value.contains("D")) rights += "d";
		}
		
		return rights;
    }

	public void setRights(Set<Right> rights) {
		this.rights = rights;
	}
	
    //bi-directional many-to-many association to User
    @ManyToMany(mappedBy="roles", fetch=FetchType.EAGER)
	public Set<User> getUsers() {
		return this.users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
}