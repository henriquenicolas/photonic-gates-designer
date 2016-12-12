package br.ufmg.dcc.nanocomp.analysis;

import br.ufmg.dcc.nanocomp.ctl.parser.CtlFile;
import br.ufmg.dcc.nanocomp.model.RobustnessAnalysis;

public class DefaultSimulationRunnerFactory extends SimulationRunnerFactory {

	@Override
	public SimulationRunner build(CtlFile file, RobustnessAnalysis analysis, boolean original) {
		return new LocalSimulationRunner(file,analysis,original);
	}

}
