package br.ufmg.dcc.nanocomp.dao;

import java.io.Serializable;

import br.ufmg.dcc.nanocomp.model.EntityInterface;

public abstract class DaoFactory {
	
	public abstract <IdType extends Serializable,EntityType extends EntityInterface<IdType>,DaoType extends Dao<IdType,EntityType>> DaoType getDao(Class<DaoType> daoClass);

}
