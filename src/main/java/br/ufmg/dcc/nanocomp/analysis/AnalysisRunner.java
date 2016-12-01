package br.ufmg.dcc.nanocomp.analysis;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import br.ufmg.dcc.nanocomp.model.RobustnessAnalysis;
import br.ufmg.dcc.nanotec.ctl.parser.Parser;
import br.ufmg.dcc.nanotec.ctl.parser.ParserFactory;
import br.ufmg.dcc.nanotec.model.CtlObject;
import br.ufmg.dcc.nanotec.model.Cylinder;
import br.ufmg.dcc.nanotec.model.Simulation;
import br.ufmg.dcc.nanotec.model.Vector3;

public class AnalysisRunner extends Thread {
	
	private static final AtomicInteger ID = new AtomicInteger(0);
	private RobustnessAnalysis analysis;
	
	public AnalysisRunner(RobustnessAnalysis analysis) {
		super("AnalysisRunner-"+ID.incrementAndGet());
		this.analysis = analysis;
	}
	
	@Override
	public void run() {
		Random random = new Random();
		Parser parser = ParserFactory.getInstance().buildParser();
		Simulation s = parser.parse(analysis.getCrystal().getCtl());
		SimulationRunnerFactory.getInstance().build(s,analysis,true).start();
		for(int j = 0; j<analysis.getWeight(); j++) {
			s = parser.parse(analysis.getCrystal().getCtl());
			for(String index : analysis.getRegion().split(",")) {
				CtlObject o = s.getGeometry().get(Integer.valueOf(index));
				Cylinder c = (Cylinder)o;
				double x = (random.nextGaussian()*analysis.getSigma())/1000;
				double y = (random.nextGaussian()*analysis.getSigma())/1000;
				double r = (random.nextGaussian()*analysis.getSigma())/1000;
				
				Vector3 center = c.getCenter();
				center.setX(center.getX().doubleValue()+x);
				center.setY(center.getY().doubleValue()+y);
				
				c.setRadius(c.getRadius().doubleValue()+r);
			}
			SimulationRunnerFactory.getInstance().build(s,analysis,false).start();
		}
	}

}
