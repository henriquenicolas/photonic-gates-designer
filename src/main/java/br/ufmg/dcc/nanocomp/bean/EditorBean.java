package br.ufmg.dcc.nanocomp.bean;

import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import br.ufmg.dcc.nanocomp.dao.CrystalDao;
import br.ufmg.dcc.nanocomp.dao.MeepServerDao;
import br.ufmg.dcc.nanocomp.model.MeepServer;

@RequestScoped
@ManagedBean(name="editorBean")
public class EditorBean extends AbstractBean {

	private static final long serialVersionUID = 1L;

	private static final Random RAND = new Random();
	
	private String ctl;
	private MeepServer meepServer;
	
	@PostConstruct
	public void init() {
		try {
			Long crystalId = Long.valueOf(getParameter("crystal"));
			ctl = getDao(CrystalDao.class).find(crystalId).getCtl();
		} catch (Exception e) {
			
		}
		try {
			List<MeepServer> servers = getDao(MeepServerDao.class).list();
			meepServer = servers.get(RAND.nextInt(servers.size()));
		} catch (Exception e) {
			
		}
	}

	public String getCtl() {
		return ctl;
	}

	public void setCtl(String ctl) {
		this.ctl = ctl;
	}
	
	public String getMeepServer() {
		try {
			return new StringBuilder("ws://").append(meepServer.getHost()).append(":").append(meepServer.getPort())
					.append(meepServer.getPath()).append("/execute").toString();
		} catch (Exception e) {
			addError("Falha ao construir a url do meep server");
			return null;
		}
	}
	
	public void setMeepServer(String server) {
		
	}

}
