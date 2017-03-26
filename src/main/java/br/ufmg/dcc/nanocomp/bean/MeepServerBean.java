package br.ufmg.dcc.nanocomp.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.dcc.nanocomp.dao.DaoException;
import br.ufmg.dcc.nanocomp.dao.MeepServerDao;
import br.ufmg.dcc.nanocomp.model.MeepServer;

@ViewScoped
@ManagedBean(name="meepServerBean")
public class MeepServerBean extends AbstractBean {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(MeepServerBean.class);
	
	private MeepServer server;
	
	private List<MeepServer> servers;
	
	@PostConstruct
	public void init() {
		server = new MeepServer();
		server.setPort(8080);
		server.setPath("/meep-server");
		try {
			servers = getDao(MeepServerDao.class).list();
		} catch (DaoException e) {
			LOGGER.error("Failed to load server list",e);
		}
	}
	
	public void save() {
		try {
			getDao(MeepServerDao.class).save(server);
			addMessage("Server added successfully");
			init();
		} catch (DaoException e) {
			LOGGER.error("Error while saving meep server",e);
			addError("Could not add server");
		}
	}
	
	public MeepServer getServer() {
		return server;
	}
	
	public void setServer(MeepServer server) {
		this.server = server;
	}
	
	public List<MeepServer> getServers() {
		return servers;
	}
	
	public void setServers(List<MeepServer> servers) {
		this.servers = servers;
	}
}
