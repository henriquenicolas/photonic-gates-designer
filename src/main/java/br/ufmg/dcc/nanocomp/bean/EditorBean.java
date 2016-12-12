package br.ufmg.dcc.nanocomp.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import br.ufmg.dcc.nanocomp.dao.CrystalDao;

@RequestScoped
@ManagedBean(name="editorBean")
public class EditorBean extends AbstractBean {

	private static final long serialVersionUID = 1L;
	
	private String ctl;
	
	@PostConstruct
	public void init() {
		try {
			Long crystalId = Long.valueOf(getParameter("crystal"));
			ctl = getDao(CrystalDao.class).find(crystalId).getCtl();
		} catch (Exception e) {
			// ignore
		}
	}

	public String getCtl() {
		return ctl;
	}

	public void setCtl(String ctl) {
		this.ctl = ctl;
	}

}
