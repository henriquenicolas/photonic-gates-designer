package br.ufmg.dcc.nanocomp.dao.jpa;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import br.ufmg.dcc.nanocomp.dao.UserDao;
import br.ufmg.dcc.nanocomp.model.User;

public class JpaUserDao extends AbstractJpaDao<String, User> implements UserDao {
	
	protected JpaUserDao() {
		
	}

	@Override
	public Class<User> getEntityClass() {
		return User.class;
	}
	
	@Override
	public User save(User entity) {
		entity.setPassword(getSHA1Code(entity.getPassword()));
		return super.save(entity);
	}
	
	@Override
	public User update(User entity) {
		entity.setPassword(getSHA1Code(entity.getPassword()));
		return super.update(entity);
	}

	@Override
	public User login(String email, String password) {
		HashMap<String, Object> params = new HashMap<>(2);
		params.put("email", email);
		params.put("password", getSHA1Code(password));
		return super.findSingleResult(User.LOGIN, params);
	}

	/**
	 * Method used to apply the SHA-1 hash to a string.
	 * @see {@link MessageDigest}
	 * @param s String to be hashed.
	 * @return The hashed string.
	 * @throws NoSuchAlgorithmException
	 */    
	public static String getSHA1Code(String s) {
		MessageDigest md = null; 
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.reset();
			md.update(s.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		BigInteger hash = new BigInteger(1, md.digest());  
		return hash.toString(16);  
	}

}
