package br.ufmg.dcc.nanocomp.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.dcc.nanocomp.model.RobustnessAnalysis;
import br.ufmg.dcc.nanotec.model.Simulation;

public abstract class SimulationRunnerFactory {

	private static SimulationRunnerFactory instance;
	private static final Logger LOGGER = LoggerFactory.getLogger(SimulationRunnerFactory.class);

	protected SimulationRunnerFactory() {

	}

	public static SimulationRunnerFactory getInstance() {
		if(instance==null){
			try(InputStream is = SimulationRunnerFactory.class.getResourceAsStream("/META-INF/simulation-runner.properties")){
				Properties config = new Properties();
				config.load(is);
				instance = (SimulationRunnerFactory) Class.forName(config.getProperty("factory")).newInstance();
			} catch (IOException e) {
				LOGGER.warn("It was not possible to load SimulationRunner configuration file",e);
				instance = new DefaultSimulationRunnerFactory();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e){
				LOGGER.warn("It was not possible to instanciate SimulationRunnerFactory",e);
				instance = new DefaultSimulationRunnerFactory();
			}
		}
		return instance;
	}

	public abstract SimulationRunner build(Simulation s, RobustnessAnalysis analysis, boolean original);
}
