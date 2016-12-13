package br.ufmg.dcc.nanocomp.analysis;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import br.ufmg.dcc.nanocomp.ctl.CtlClass;
import br.ufmg.dcc.nanocomp.ctl.CtlList;
import br.ufmg.dcc.nanocomp.ctl.CtlVector3;
import br.ufmg.dcc.nanocomp.ctl.number.CtlDouble;
import br.ufmg.dcc.nanocomp.ctl.parser.CtlFile;
import br.ufmg.dcc.nanocomp.ctl.parser.ParserFactory;
import br.ufmg.dcc.nanocomp.dao.DaoException;
import br.ufmg.dcc.nanocomp.dao.MeepServerDao;
import br.ufmg.dcc.nanocomp.dao.jpa.JpaDaoFactory;
import br.ufmg.dcc.nanocomp.model.Execution;
import br.ufmg.dcc.nanocomp.model.MeepServer;
import br.ufmg.dcc.nanocomp.model.RobustnessAnalysis;
import br.ufmg.dcc.nanocomp.peg.Parser;

public class AnalysisRunner extends Thread {
	
	private static final AtomicInteger ID = new AtomicInteger(0);
	private RobustnessAnalysis analysis;
	private List<MeepServer> servers;
	private Iterator<MeepServer> iterator;
	
	public AnalysisRunner(RobustnessAnalysis analysis) {
		super("AnalysisRunner-"+ID.incrementAndGet());
		this.analysis = analysis;
		
		try (JpaDaoFactory daoFactory = new JpaDaoFactory()){
			MeepServerDao dao = daoFactory.getDao(MeepServerDao.class);
			servers = dao.list();
			if(servers.isEmpty()) throw new RuntimeException("No servers available");
			iterator = servers.iterator();
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}
	}
	
	public MeepServer nextServer() {
		if(iterator.hasNext()) {
			return iterator.next();
		} else {
			iterator = servers.iterator();
			return iterator.next();
		}
	}
	
	@Override
	public void run() {
		Random random = new Random();
		Parser<CtlFile> parser = ParserFactory.getInstance().getParser();
		
		SimulationRunnerFactory.getInstance().build(new Execution(nextServer(),analysis)).start();
		for(int j = 0; j<analysis.getWeight(); j++) {
			CtlFile file = parser.parse(analysis.getCrystal().getCtl());
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
			SimulationRunnerFactory.getInstance().build(new Execution(nextServer(),analysis,file.toString())).start();
		}
	}

}
