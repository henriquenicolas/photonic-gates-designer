package br.ufmg.dcc.nanocomp.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.dcc.nanocomp.dao.UserDao;
import br.ufmg.dcc.nanocomp.model.User;


@SessionScoped
@ManagedBean(name="loginBean")
public class LoginBean extends AbstractBean {

	private static final long serialVersionUID = 4L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginBean.class);

	/**
	 * The logged user
	 */
	private User user;
	
	/**
	 * The username of the user attempting to login
	 */
	private String email;

	/**
	 * The password of the user attempting to login
	 */
	private String password;

	/**
	 * Attempt to login
	 * @return "pretty:feed" if the login was successful null otherwise
	 */
	public String login() {
		try {
			user = getDao(UserDao.class).login(email,password);
			if(user==null){
				addError("Usuários ou senha inválidos");
				return null;
			} else {
				getSession().setAttribute("logged", Boolean.TRUE);
				return "pretty:crystals";
			}
		} catch(Exception e) {
			LOGGER.error("An error occurred while trying to login",e);
			return "pretty:";
		}
	}
	
	/**
	 * Method listener of an event that PrettyFaces will fire when the user access /logout
	 */
	public void logout() {
		getSession().invalidate();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}