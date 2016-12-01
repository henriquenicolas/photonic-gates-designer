package br.ufmg.dcc.nanocomp.analysis;

import br.ufmg.dcc.nanocomp.model.RobustnessAnalysis;
import br.ufmg.dcc.nanotec.model.Simulation;

public class DefaultSimulationRunnerFactory extends SimulationRunnerFactory {

	@Override
	public SimulationRunner build(Simulation s, RobustnessAnalysis analysis, boolean original) {
		return new LocalSimulationRunner(s,analysis,original);
	}

}
