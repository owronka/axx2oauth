package com.axxessio.oauth2.server.service.pdo;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Set;


/**
 * The persistent class for the T_RIGHT database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Right.findAllRights", query = "SELECT r FROM Right r ORDER BY r.name ASC"),
	@NamedQuery(name = "Right.findRightById", query = "SELECT r FROM Right r WHERE r.id=?1"),
    @NamedQuery(name = "Right.findRightByName", query = "SELECT r FROM Right r WHERE r.name=?1 ")})

@Table(name="A2O_RIGHT")
public class Right implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String scope;
	private String right;
	
	private Set<Role> roles;

    public Right() {
    }


    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "RI_ID")
    public long getId() {
        return this.id;
    }


    public void setId(long id) {
        this.id = id;
    }
    
    @Column(name = "RI_NAME")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Column(name = "RI_SCOPE")
	public String getScope() {
		return this.scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

    @Column(name = "RI_RIGHT")
	public String getRight() {
		return this.right;
	}

	public void setRight(String right) {
		this.right = right;
	}

    //bi-directional many-to-many association to Role
    @ManyToMany
    @JoinTable(name="A2O_ROLERIGHT", joinColumns={@JoinColumn(name="RR_RI_ID")}, inverseJoinColumns={@JoinColumn(name="RR_RO_ID")})
	public Set<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}