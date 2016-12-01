package br.ufmg.dcc.nanocomp.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="executions")
public class Execution implements EntityInterface<Long> {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	private boolean original;
	
	@Column
	private Date date;
	
	@ManyToOne
	@JoinColumn(name="idRobustnessAnalysis", nullable = false)
	private RobustnessAnalysis robustnessAnalysis;
	
	@OneToMany(mappedBy = "execution", targetEntity = Result.class,
			fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@Fetch(value = FetchMode.SUBSELECT)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Result> results;
	
	public List<Value> getValues(double frequency) {
		TreeMap<Double, Result> map = new TreeMap<>();
		try {
			for(Result r : getResults()) {
				map.put(Math.abs(frequency-r.getFrequency()), r);
			}
			return map.values().iterator().next().getValues();
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean isOriginal() {
		return original;
	}
	
	public void setOriginal(boolean original) {
		this.original = original;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public RobustnessAnalysis getRobustnessAnalysis() {
		return robustnessAnalysis;
	}

	public void setRobustnessAnalysis(RobustnessAnalysis robustnessAnalysis) {
		this.robustnessAnalysis = robustnessAnalysis;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

}
