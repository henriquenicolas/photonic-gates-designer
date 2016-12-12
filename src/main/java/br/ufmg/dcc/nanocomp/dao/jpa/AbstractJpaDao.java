package br.ufmg.dcc.nanocomp.dao.jpa;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.dcc.nanocomp.dao.Dao;
import br.ufmg.dcc.nanocomp.dao.DaoException;
import br.ufmg.dcc.nanocomp.jsf.JpaFilter;
import br.ufmg.dcc.nanocomp.model.EntityInterface;

/**
 * Abstract JPA Implementation of the Dao interface
 * @param <T> The generic type of the model class.
 */
public abstract class AbstractJpaDao<IdType extends Serializable,T extends EntityInterface<IdType>> implements Dao<IdType,T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJpaDao.class);
	
	private transient EntityManager entityManager;

	public abstract Class<T> getEntityClass();

	/**
	 * Method used to flush the connection, 
	 * executing any pending statements with the database.
	 */
	public void flushConnection() {
		getEntityManager().flush();
	}

	/**
	 * Method used to join transactions.
	 */
	public void joinTransaction() {
		getEntityManager().joinTransaction();
	}

	/**
	 * Method used to persist a given entity into the database.
	 * @param entity The entity to be persisted.
	 * @return T The persisted and managed entity.
	 */
	@Override
	public T save(T entity) {
		getEntityManager().persist(entity);
		flushConnection();
		return entity;
	}


	/**
	 * Method used to delete a given entity from the database.
	 * @param entity The entity to be deleted.
	 * @throws DaoException 
	 */
	public void delete(T entity) throws DaoException {
		try {
			getEntityManager().remove(find(entity.getId()));
			flushConnection();
		} catch (Exception e) {
			throw new DaoException("Failed to delete entity",e);
		}
	}

	/**
	 * Method used to update a given entity of the database.
	 * @param entity The entity to be updated.
	 * @return entity The updated and managed entity.
	 */
	public T update(T entity) {
		T returnedEntity = getEntityManager().merge(entity);
		flushConnection();
		return returnedEntity;
	}

	/**
	 * Method used to find an entity of the database, given its id.
	 * @param entityId The entity id to be found.
	 * @return entity The entity if found, null otherwise.
	 */
	public T find(IdType entityId) {
		T entity = getEntityManager().find(getEntityClass(), entityId);
		flushConnection();
		return entity;
	}

	/**
	 * Method used to find only the reference to an entity 
	 * of the database, given its id.<br/>
	 * This is used to lazily retrieve the entity.
	 * @param entityId The entity id to be found.
	 * @return entity The entity if found, null otherwise.
	 */
	public T findReferenceOnly(Object entityId) {
		T entity = getEntityManager().getReference(getEntityClass(), entityId);
		flushConnection();
		return entity;		
	}

	/**
	 * Method used to list all records of the entity from the database. 
	 * @return The list of records of the entity.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public List<T> list() {
		List<T> list = null;
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(getEntityClass()));
		list = getEntityManager().createQuery(cq).getResultList();
		flushConnection();
		return list;
	}

	/**
	 * Method used to find a single result, 
	 * given a named query and a set of parameters.
	 * @param namedQuery The name of the query.
	 * @param parameters The parameters of the query.
	 * @return Returns the entity if found, otherwise returns 0.
	 */	
	@SuppressWarnings("unchecked")
	protected <V> V findSingleResult(String namedQuery, Map<String, Object> parameters) {
		Query query = getEntityManager().createNamedQuery(namedQuery);
		populateQueryParameters(query, parameters);
		try {
			return (V) query.getSingleResult();
		} catch (NoResultException e){
			return null;
		}
	}	

	/**
	 * Method used to find a single result, given a named query.
	 * @param namedQuery The name of the query.
	 * @return Returns the entity if found, otherwise returns 0.
	 */
	protected <V> V findSingleResult(String namedQuery) {
		return findSingleResult(namedQuery,null);
	}

	/**
	 * Method that given the query and a Map of objects populate a query
	 * @param query The unpopulated query.
	 * @param parameters The parameters to populate the query.
	 */
	public void populateQueryParameters(Query query, Map<String, Object> parameters) {
		if (query != null && parameters != null && !parameters.isEmpty()) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Finds a list of results of a given query.
	 * @param namedQuery The query.
	 * @return The list of results of a given query.
	 */	
	protected <V> List<V> findListResult(String namedQuery) {
		return findListResult(namedQuery, null);
	}

	/**
	 * Finds a list of results of a given parameterized query.
	 * @param namedQuery The query.
	 * @param parameters The list of parameters of the given query.
	 * @return The list of results of a given parameterized query.
	 */
	@SuppressWarnings("unchecked")
	protected <V> List<V> findListResult(String namedQuery, Map<String, Object> parameters) {
		List<V> result = null;
		Query query = getEntityManager().createNamedQuery(namedQuery);
		populateQueryParameters(query, parameters);
		result = (List<V>) query.getResultList();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected <V> List<V> findListResult(String namedQuery, Map<String, Object> parameters, int first, int size) {
		List<V> result = null;
		Query query = getEntityManager().createNamedQuery(namedQuery);
		populateQueryParameters(query, parameters);
		query.setFirstResult(first).setMaxResults(first);
		result = (List<V>) query.getResultList();
		return result;
	}

	/**
	 * Method used to apply the MD5 hash to a string.
	 * @see {@link MessageDigest}
	 * @param string String to be hashed.
	 * @return The hashed string.
	 * @throws NoSuchAlgorithmException
	 */
	public static String getMD5Code(String string) {
		BigInteger hash;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(string.getBytes());
			hash = new BigInteger(1, md.digest());  
		} catch (NoSuchAlgorithmException|NullPointerException e) {
			LOGGER.error("Failed to convert string to MD5",e);
			return null;
		}
		return hash.toString(16);  
	}
	
	protected EntityManager getEntityManager() {
		if(this.entityManager!=null && this.entityManager.isOpen()){
			return entityManager;
		} else {
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) ec.getRequest();
			//If you store entityManager, the JpaDao isn't thread safe
			return (EntityManager) request.getAttribute(JpaFilter.ENTITY_MANAGER);
		}
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}