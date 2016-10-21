package br.ufmg.dcc.nanocomp.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.PersistenceException;

import br.ufmg.dcc.nanocomp.dao.UserDao;
import br.ufmg.dcc.nanocomp.model.User;

@ViewScoped
@ManagedBean(name = "signUpBean")
public class SignUpBean extends AbstractBean {

	private static final long serialVersionUID = 3L;

	/**
	 * The user created
	 */
	private User user = new User();

	public String save() {
		UserDao dao = getDao(UserDao.class);
			try {
				dao.save(getUser());
				addMessage("Cadastro realizado com sucesso!");
				return "pretty:login";
			} catch (PersistenceException e) {
				addError("Não foi possível realizar o cadastro, usuário já existente.");
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				addError("Não foi possível realizar o cadastro.");
				e.printStackTrace();
				return null;
			}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	

}
