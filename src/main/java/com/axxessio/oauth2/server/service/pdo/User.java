package com.axxessio.oauth2.server.service.pdo;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Set;


/**
 * The persistent class for the DPUSER database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "User.findUserByName", query = "SELECT u FROM User u WHERE u.name=?1 ")})
@Table(name="A2O_USER")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
    private String name;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String salt;
    private Set<Role> roles;

    public User() {
    }


    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name="U_ID")
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

   @Column(name="U_NAME", unique=true, nullable=false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

   @Column(name="U_EMAIL")
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    @Column(name="U_FIRST_NAME", nullable=false)
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

    @Column(name="U_LAST_NAME", nullable=false)
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

    @Column(name="U_PASSWORD", nullable=false)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    @Column(name="U_SALT", nullable=false)
	public String getSalt() {
		return this.salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	//bi-directional many-to-many association to Role
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="A2O_USERROLE", joinColumns={@JoinColumn(name="UR_U_ID")}, inverseJoinColumns={@JoinColumn(name="UR_RO_ID")})
	public Set<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}