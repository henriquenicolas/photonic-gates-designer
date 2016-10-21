package br.ufmg.dcc.nanocomp.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.dcc.nanocomp.model.EntityInterface;

public abstract class DaoFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DaoFactory.class);
	
	private static DaoFactory INSTANCE;
	
	public abstract <IdType extends Serializable,EntityType extends EntityInterface<IdType>,DaoType extends Dao<IdType,EntityType>> DaoType getDao(Class<DaoType> daoClass);
	
	public static DaoFactory getInstance() {
		if(INSTANCE==null){
			try(InputStream is = DaoFactory.class.getResourceAsStream("/META-INF/dao.properties")){
				Properties config = new Properties();
				config.load(is);
				INSTANCE = (DaoFactory) Class.forName(config.getProperty("factory")).newInstance();
			} catch (IOException e) {
				LOGGER.warn("It was not possible to load Dao configuration file",e);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e){
				LOGGER.warn("It was not possible to instanciate DaoFactory",e);
			}
		}
		return INSTANCE;
	}

}
