package br.ufmg.dcc.nanocomp.jsf;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class JpaFilter implements Filter {

	public static final String ENTITY_MANAGER = "entityManager";
	
	private static EntityManagerFactory emf;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		emf = Persistence.createEntityManagerFactory("photonic-gates-designer");
	}
	
	public static EntityManagerFactory getEmf() {
		return emf;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		EntityManager manager = emf.createEntityManager();
		request.setAttribute(ENTITY_MANAGER, manager);
		EntityTransaction tx = manager.getTransaction();

		try {
			tx.begin();
			chain.doFilter(request, response);
			if(tx.getRollbackOnly()){
				tx.rollback();
			} else {
				tx.commit();
			}
        } catch (Exception e) {
        	e.printStackTrace();
        	tx.rollback();
        } finally {
        	manager.close();
        }
	}

	@Override
	public void destroy() {
		emf.close();
	}

}
