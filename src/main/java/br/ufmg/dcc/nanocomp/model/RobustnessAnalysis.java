package br.ufmg.dcc.nanocomp.model;

import java.util.ArrayList;
import java.util.Collections;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="robustnessAnalysis")
public class RobustnessAnalysis implements EntityInterface<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	private Date date;

	@Column
	private boolean running;

	/**
	 * Simulation used for analysis
	 */
	@ManyToOne
	@JoinColumn(name="idCrystal", nullable = false)
	private PhotonicCrystal crystal;

	/**
	 * Index of cylinders whose radius and position will be changed
	 * separated by comma
	 */
	@Lob
	@Column
	private String region;

	/**
	 * How many times each cylinder will be tested
	 */
	@Column
	private int weight;

	/**
	 * Parameter of gaussian distribution used to change radius and position of cylinder
	 */
	@Column
	private double sigma;

	/**
	 * Executions of this {@link RobustnessAnalysis}
	 */
	@OneToMany(mappedBy = "robustnessAnalysis", targetEntity = Execution.class,
			fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@Fetch(value = FetchMode.SUBSELECT)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Execution> executions;
	
	public List<FluxRegionResult> compute(double frequency, int tolerance, boolean[] expectedResults) {
		try {
			List<Value> originalResults = getOriginalExecution().getValues(frequency);
			double[] averageValues = new double[expectedResults.length];
			int[] errorCount = new int[expectedResults.length];
			for(Execution e : getExecutions()) {
				List<Value> vs = e.getValues(frequency);
				for(int i = 0; i < averageValues.length && i < vs.size(); i++) {
					double value = vs.get(i).getValue();
					double original = originalResults.get(i).getValue();
					averageValues[i] += value;
					if(expectedResults[i]){
						if(value<original-original*(tolerance/100d)) {
							errorCount[i]++;
						}
					} else {
						if(value>original+original*(tolerance/100d)) {
							errorCount[i]++;
						}
					}
				}
			}
			List<FluxRegionResult> results = new ArrayList<>(expectedResults.length);
			for(int i = 0; i < averageValues.length; i++) {
				FluxRegionResult r = new FluxRegionResult();
				r.setIndex(i+1);
				r.setRobustnessAnalysis(this);
				r.setAverageResult(averageValues[i]/getExecutions().size());
				r.setOriginalResult(originalResults.get(i).getValue());
				r.setErrorCount(errorCount[i]);
				r.setErrorPercentage((int)(((double)errorCount[i]/(double)getExecutions().size())*100));
				results.add(r);
			}
			return results;
		} catch (Exception e) {
			FluxRegionResult r = new FluxRegionResult();
			r.setIndex(0);
			r.setRobustnessAnalysis(this);
			r.setAverageResult(0);
			r.setOriginalResult(0);
			r.setErrorCount(0);
			r.setErrorPercentage(0);
			return Collections.singletonList(r);
		}
	}
	
	private Execution getOriginalExecution() {
		for(Execution e : getExecutions()) {
			if(e.isOriginal()) return e;
		}
		return null;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public PhotonicCrystal getCrystal() {
		return crystal;
	}

	public void setCrystal(PhotonicCrystal crystal) {
		this.crystal = crystal;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public double getSigma() {
		return sigma;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public List<Execution> getExecutions() {
		return executions;
	}
	
	public void setExecutions(List<Execution> executions) {
		this.executions = executions;
	}

}
