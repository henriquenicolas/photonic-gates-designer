package br.ufmg.dcc.nanocomp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="simulations")
public class Simulation implements EntityInterface<Long> {
	
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	@Lob
	@Column
	private String ctl;

	@ManyToOne
	@JoinColumn(name="idOwner", nullable = false)
	private User owner;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCtl() {
		return ctl;
	}

	public void setCtl(String ctl) {
		this.ctl = ctl;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

}
