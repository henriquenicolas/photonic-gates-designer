package br.ufmg.dcc.nanocomp.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;

import br.ufmg.dcc.nanocomp.model.EntityInterface;

/**
 * Generic interface for all data access objects.
 * @author Jerônimo Nunes Rocha
 * @param <IdType> The type of the primary key of the {@link Entity}
 * @param <T> The type of the {@link Entity} that this Dao will handle
 */
public interface Dao<IdType extends Serializable,T extends EntityInterface<IdType>> {
	
	/**
	 * Finds an object by its id if it exists.
	 * @param id The id of the object.
	 * @return The object, if found.
	 * @throws DaoException 
	 * @throws DaoExcep throws DaoExceptiontion 
	 */
	T find(IdType id) throws DaoException;
	
	/**
	 * Saves an object.
	 * @param t The object.
	 * @throws DaoException 
	 */
	T save(T t) throws DaoException;
	
	/**
	 * Updates an object.
	 * @param t The object.
	 * @throws DaoException 
	 */
	T update(T t) throws DaoException;
	
	/**
	 * Deletes an object.
	 * @param t The object.
	 * @throws DaoException 
	 */
	void delete(T t) throws DaoException;
	
	/**
	 * Lists persisted objects.
	 * @return The list of objets.
	 * @throws DaoException 
	 */
	List<T> list() throws DaoException;
	
}