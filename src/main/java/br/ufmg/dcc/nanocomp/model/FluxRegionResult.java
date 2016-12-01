package br.ufmg.dcc.nanocomp.model;

public class FluxRegionResult {

	private RobustnessAnalysis robustnessAnalysis;

	private int index;

	private double originalResult;

	private double averageResult;

	private double errorCount;

	private int errorPercentage;

	public RobustnessAnalysis getRobustnessAnalysis() {
		return robustnessAnalysis;
	}

	public void setRobustnessAnalysis(RobustnessAnalysis robustnessAnalysis) {
		this.robustnessAnalysis = robustnessAnalysis;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getOriginalResult() {
		return originalResult;
	}

	public void setOriginalResult(double originalResult) {
		this.originalResult = originalResult;
	}

	public double getAverageResult() {
		return averageResult;
	}

	public void setAverageResult(double averageResult) {
		this.averageResult = averageResult;
	}

	public double getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(double errorCount) {
		this.errorCount = errorCount;
	}

	public int getErrorPercentage() {
		return errorPercentage;
	}

	public void setErrorPercentage(int errorPercentage) {
		this.errorPercentage = errorPercentage;
	}


}
