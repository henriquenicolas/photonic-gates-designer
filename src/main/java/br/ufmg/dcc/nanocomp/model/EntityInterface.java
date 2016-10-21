package br.ufmg.dcc.nanocomp.model;

import java.io.Serializable;

/**
 * Interface to represent any Entity
 * @author Alexandre Alphonsos Rodrigues Pereira
 * @author Jeronimo Nunes Rocha
 * @author Felipe Marcelino
 *
 * @param <IdType> The type of the Primary Key of the Entity
 */

public interface EntityInterface<IdType extends Serializable> extends Serializable {
	
	public IdType getId();

}
