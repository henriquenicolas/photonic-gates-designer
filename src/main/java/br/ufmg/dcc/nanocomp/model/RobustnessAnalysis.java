package br.ufmg.dcc.nanocomp.model;

import java.util.List;

import br.ufmg.dcc.nanotec.model.Cylinder;
import br.ufmg.dcc.nanotec.model.Simulation;

public class RobustnessAnalysis {
	
	public class Region {
		List<Cylinder> cylinders;
	}
	
	private Simulation simulation;
	
	private List<Region> regions;
	
	private int overTolerance;
	
	private int underTolerance;
	
	private int weight;
	
	private double sigma;
	
	private double[] expectedResults;

}
