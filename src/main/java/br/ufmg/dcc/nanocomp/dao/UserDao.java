package br.ufmg.dcc.nanocomp.dao;

import br.ufmg.dcc.nanocomp.model.User;

public interface UserDao extends Dao<String,User> {

	public User login(String email, String password);
	
}
