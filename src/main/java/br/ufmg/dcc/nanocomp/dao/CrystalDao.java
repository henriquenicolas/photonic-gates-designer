package br.ufmg.dcc.nanocomp.dao;

import java.util.List;

import br.ufmg.dcc.nanocomp.model.PhotonicCrystal;
import br.ufmg.dcc.nanocomp.model.User;

public interface CrystalDao extends Dao<Long,PhotonicCrystal>{

	public List<PhotonicCrystal> listSimulationsByUser(User user);

}
