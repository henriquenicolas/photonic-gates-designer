package br.ufmg.dcc.nanocomp.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.dcc.nanocomp.analysis.AnalysisRunner;
import br.ufmg.dcc.nanocomp.dao.CrystalDao;
import br.ufmg.dcc.nanocomp.dao.RobustnessAnalysisDao;
import br.ufmg.dcc.nanocomp.model.FluxRegionResult;
import br.ufmg.dcc.nanocomp.model.PhotonicCrystal;
import br.ufmg.dcc.nanocomp.model.RobustnessAnalysis;

@ViewScoped
@ManagedBean(name="analysisBean")
public class AnalysisBean extends AbstractBean {

	private static final long serialVersionUID = 6L;
	private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisBean.class);
	
	private PhotonicCrystal crystal;
	private List<FluxRegionResult> results;
	
	// Run
	private String region;
	private int weight = 10;
	private double sigma = 20;
	
	// Update
	private double frequency;
	private int tolerance;
	private String expectedResults = "1";
	
	@PostConstruct
	public void init() {
		try {
			Long crystalId = Long.valueOf(getParameter("crystal"));
			crystal = getDao(CrystalDao.class).find(crystalId);
			results = new ArrayList<>(crystal.getRobustnessAnalysis().size()*2);
			for(RobustnessAnalysis analysis : crystal.getRobustnessAnalysis()) {
				String[] eResults = expectedResults.split(",");
				boolean[] expectedResults = new boolean[eResults.length];
				for(int i = 0; i< eResults.length; i++) {
					expectedResults[i] = "1".equals(eResults[i]);
				}
				results.addAll(analysis.compute(frequency,tolerance,expectedResults));
			}
		} catch (Exception e) {
			LOGGER.error("Failed to load results",e);
		}
	}
	
	public void update() {
		init();
		addMessage("Atualizado!");
	}
	
	public void run() {
		try {
			RobustnessAnalysis robustnessAnalysis = new RobustnessAnalysis();
			robustnessAnalysis.setCrystal(crystal);
			robustnessAnalysis.setRunning(true);
			robustnessAnalysis.setSigma(sigma);
			robustnessAnalysis.setWeight(weight);
			robustnessAnalysis.setDate(new Date());
			robustnessAnalysis.setRegion(this.region);
			getDao(RobustnessAnalysisDao.class).save(robustnessAnalysis);
			new AnalysisRunner(robustnessAnalysis).start();
			addMessage("Análise enviada com sucesso para execução");
			init();
		} catch (Exception e) {
			LOGGER.error("Failed to create analysis",e);
			addError("Não foi possível criar análise");
		}

	}
	
	public List<FluxRegionResult> getResults() {
		return results;
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

	public int getTolerance() {
		return tolerance;
	}

	public void setTolerance(int tolerance) {
		this.tolerance = tolerance;
	}
	
	public double getFrequency(){
		return frequency;
	}
	
	public void setFrequency(double frequency){
		this.frequency = frequency;
	}
	
	public String getExpectedResults() {
		return expectedResults;
	}
	
	public void setExpectedResults(String expectedResults) {
		this.expectedResults = expectedResults;
	}

}
