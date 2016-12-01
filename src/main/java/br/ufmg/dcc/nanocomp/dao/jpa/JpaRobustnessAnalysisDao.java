package br.ufmg.dcc.nanocomp.dao.jpa;

import br.ufmg.dcc.nanocomp.dao.RobustnessAnalysisDao;
import br.ufmg.dcc.nanocomp.model.RobustnessAnalysis;

public class JpaRobustnessAnalysisDao extends AbstractJpaDao<Long, RobustnessAnalysis> implements RobustnessAnalysisDao {
	
	protected JpaRobustnessAnalysisDao() {
		
	}

	@Override
	public Class<RobustnessAnalysis> getEntityClass() {
		return RobustnessAnalysis.class;
	}

}
