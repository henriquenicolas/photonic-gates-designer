package br.ufmg.dcc.nanocomp.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="users")
@NamedQueries({
	@NamedQuery(name=User.LOGIN, query="select u from User u where u.email = :email and u.password = :password")
})
public class User implements EntityInterface<String> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String LOGIN = "user.login";

	@Id
	private String email;
	
	@Column
	private String name;
	
	@Column
	private String password;
	
	@OneToMany(mappedBy = "owner", targetEntity = PhotonicCrystal.class,
			fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@Fetch(value = FetchMode.SUBSELECT)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<PhotonicCrystal> simulations;
	
	@Override
	public String getId() {
		return getEmail();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<PhotonicCrystal> getSimulations() {
		return simulations;
	}

	public void setSimulations(List<PhotonicCrystal> simulations) {
		this.simulations = simulations;
	}
	
}