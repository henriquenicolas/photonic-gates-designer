package br.ufmg.dcc.nanocomp.dao.jpa;

import br.ufmg.dcc.nanocomp.dao.ExecutionDao;
import br.ufmg.dcc.nanocomp.model.Execution;

public class JpaExecutionDao extends AbstractJpaDao<Long,Execution> implements ExecutionDao {
	
	protected JpaExecutionDao() {
		
	}

	@Override
	public Class<Execution> getEntityClass() {
		return Execution.class;
	}

}
