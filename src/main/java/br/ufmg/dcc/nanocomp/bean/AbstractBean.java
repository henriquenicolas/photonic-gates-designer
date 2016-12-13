package br.ufmg.dcc.nanocomp.bean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.ufmg.dcc.nanocomp.dao.Dao;
import br.ufmg.dcc.nanocomp.dao.jpa.JsfJpaDaoFactory;
import br.ufmg.dcc.nanocomp.model.EntityInterface;
import br.ufmg.dcc.nanocomp.model.User;

/**
 * Abstract class for {@link ManagedBean}
 * It contains useful methods that the {@link ManagedBean} will probably use
 * @author Alexandre Alphonsos Rodrigues Pereira
 * @author Jeronimo Nunes Rocha
 * @author Felipe Marcelino
 *
 */
public abstract class AbstractBean implements Serializable {

	private static final long serialVersionUID = 2L;
	
	
	protected <IdType extends Serializable,EntityType extends EntityInterface<IdType>,DaoType extends Dao<IdType,EntityType>> DaoType getDao(Class<DaoType> daoClass){
		return JsfJpaDaoFactory.getInstance().getDao(daoClass);
	}
	
	/**
	 * Method used to add an info message to the client's messages queue
	 * @param msg Message to be sent
	 */
	protected final void addMessage(String msg){
		addMessage(new FacesMessage(msg));
	}

	/**
	 * Method used to add a {@link FacesMessage} to the client's messages queue
	 * @param msg Message to be sent
	 */
	protected final void addMessage(FacesMessage msg){
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	/**
	 * Method used to add an error message to the client's messages queue
	 * @param msg Message to be sent
	 */
	protected final void addError(String error){
		addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, error, null));
	}
	
	/**
	 * Method used to add a warn message to the client's messages queue
	 * @param msg Message to be sent
	 */
	protected final void addWarning(String warn){
		addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, warn, null));
	}
	
	/**
	 * Recover a {@link ManagedBean} from a {@link HttpSession}
	 * @param session The session to look for the {@link ManagedBean}
	 * @param beanClass The {@link ManagedBean} to look for
	 * @return The {@link ManagedBean} if exist's, null otherwise
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSessionBean(HttpSession session,Class<T> beanClass){
		return (T) session.getAttribute(beanClass.getAnnotation(ManagedBean.class).name());
	}
	
	/**
	 * Recover a {@link ManagedBean} from a {@link HttpSession}
	 * @param beanClass The {@link ManagedBean} to look for
	 * @return The {@link ManagedBean} if exist's, null otherwise
	 */
	public static <T> T getSessionBean(Class<T> beanClass){
		return getSessionBean(getSession(),beanClass);
	}

	/**
	 * Method used to recover the user session from the JSF Thread
	 * @return the {@link HttpSession}
	 */
	protected final static HttpSession getSession() {
		return getHttpServletRequest().getSession();
	}
	
	/**
	 * Method used to recover the user request from the JSF Thread
	 * @return the {@link HttpServletRequest}
	 */
	protected final static HttpServletRequest getHttpServletRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}
	
	/**
	 * Method used to recover the faces {@link ExternalContext}
	 * @return
	 */
	protected final static ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}
	
	/**
	 * Method used to recover the faces {@link ResourceHandler}
	 * @return
	 */
	protected final static ResourceHandler getResourceHandler() {
		return FacesContext.getCurrentInstance().getApplication().getResourceHandler();
	}
	
	/**
	 * Method used to recover the {@link ServletContext} from the JSF Thread
	 * @return
	 */
	protected final static ServletContext getServletContext() {
		return (ServletContext) getExternalContext().getContext();
	}
	
	/**
	 * Method used to get the absolute path using the application path as root
	 * @param path The path, relative to the application path. / means application root
	 * @return The absolute path in filesystem
	 */
	protected final static String getRealPath(String path){
		return getServletContext().getRealPath(path);
	}
	
	protected final static String getParameter(String name) {
		return getHttpServletRequest().getParameter(name);
	}
	
	protected User getUser() {
		return getSessionBean(LoginBean.class).getUser();
	}
	
}
