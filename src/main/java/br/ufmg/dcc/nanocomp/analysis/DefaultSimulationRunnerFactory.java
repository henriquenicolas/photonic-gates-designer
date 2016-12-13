package br.ufmg.dcc.nanocomp.analysis;

import br.ufmg.dcc.nanocomp.model.Execution;

public class DefaultSimulationRunnerFactory extends SimulationRunnerFactory {

	@Override
	public SimulationRunner build(Execution execution) {
		return new MeepServerSimulationRunner(execution);
	}

}
