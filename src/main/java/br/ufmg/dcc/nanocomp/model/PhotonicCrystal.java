package br.ufmg.dcc.nanocomp.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="crystals")
@NamedQueries({
	@NamedQuery(name = PhotonicCrystal.LIST_BY_USER, query="select s from PhotonicCrystal s where s.owner = :user")
})
public class PhotonicCrystal implements EntityInterface<Long> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String LIST_BY_USER = "list.by.user";

	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	private String name;
	
	@Lob
	@Column
	private String ctl;
	
	@Column
	private Date date;

	@ManyToOne
	@JoinColumn(name="idOwner", nullable = false)
	private User owner;
	
	@OneToMany(mappedBy = "crystal", targetEntity = RobustnessAnalysis.class,
			fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@Fetch(value = FetchMode.SUBSELECT)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<RobustnessAnalysis> robustnessAnalysis;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCtl() {
		return ctl;
	}

	public void setCtl(String ctl) {
		this.ctl = ctl;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<RobustnessAnalysis> getRobustnessAnalysis() {
		return robustnessAnalysis;
	}

	public void setRobustnessAnalysis(List<RobustnessAnalysis> robustnessAnalysis) {
		this.robustnessAnalysis = robustnessAnalysis;
	}

}
