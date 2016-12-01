package br.ufmg.dcc.nanocomp.dao.jpa;

import java.util.HashMap;
import java.util.List;

import br.ufmg.dcc.nanocomp.dao.CrystalDao;
import br.ufmg.dcc.nanocomp.model.PhotonicCrystal;
import br.ufmg.dcc.nanocomp.model.User;

public class JpaCrystalDao extends AbstractJpaDao<Long, PhotonicCrystal> implements CrystalDao {
	
	protected JpaCrystalDao() {
		
	}

	@Override
	public Class<PhotonicCrystal> getEntityClass() {
		return PhotonicCrystal.class;
	}

	@Override
	public List<PhotonicCrystal> listSimulationsByUser(User user) {
		HashMap<String, Object> params = new HashMap<>(1);
		params.put("user", user);
		return super.findListResult(PhotonicCrystal.LIST_BY_USER, params);
	}
	
}
