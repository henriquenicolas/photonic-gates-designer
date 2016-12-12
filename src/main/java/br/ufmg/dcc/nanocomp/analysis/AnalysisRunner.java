package br.ufmg.dcc.nanocomp.analysis;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import br.ufmg.dcc.nanocomp.ctl.CtlClass;
import br.ufmg.dcc.nanocomp.ctl.CtlList;
import br.ufmg.dcc.nanocomp.ctl.CtlVector3;
import br.ufmg.dcc.nanocomp.ctl.number.CtlDouble;
import br.ufmg.dcc.nanocomp.ctl.parser.CtlFile;
import br.ufmg.dcc.nanocomp.ctl.parser.ParserFactory;
import br.ufmg.dcc.nanocomp.model.RobustnessAnalysis;
import br.ufmg.dcc.nanocomp.peg.Parser;

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
		Parser<CtlFile> parser = ParserFactory.getInstance().getParser();
		CtlFile file = parser.parse(analysis.getCrystal().getCtl());
		SimulationRunnerFactory.getInstance().build(file,analysis,true).start();
		for(int j = 0; j<analysis.getWeight(); j++) {
			file = parser.parse(analysis.getCrystal().getCtl());
			for(String index : analysis.getRegion().split(",")) {
				CtlList geometry = file.getVariable("geometry").listValue();
				CtlClass cylinder = geometry.get(Integer.valueOf(index)).classValue();
				CtlVector3 center = cylinder.getProperty("center").vector3Value();
				
				double x = (random.nextGaussian()*analysis.getSigma())/1000;
				double y = (random.nextGaussian()*analysis.getSigma())/1000;
				double r = (random.nextGaussian()*analysis.getSigma())/1000;
				
				center.setX(new CtlDouble(center.getX().doubleValue()+x));
				center.setY(new CtlDouble(center.getY().doubleValue()+y));
				
				cylinder.setProperty("radius" ,new CtlDouble(cylinder.getProperty("radius").doubleValue()+r));
			}
			SimulationRunnerFactory.getInstance().build(file,analysis,false).start();
		}
	}

}
