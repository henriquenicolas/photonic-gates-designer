package br.ufmg.dcc.nanocomp.dao.jpa;

import br.ufmg.dcc.nanocomp.dao.MeepServerDao;
import br.ufmg.dcc.nanocomp.model.MeepServer;

public class JpaMeepServerDao extends AbstractJpaDao<Long,MeepServer> implements MeepServerDao {
	
	protected JpaMeepServerDao() {
		
	}

	@Override
	public Class<MeepServer> getEntityClass() {
		return MeepServer.class;
	}

}
