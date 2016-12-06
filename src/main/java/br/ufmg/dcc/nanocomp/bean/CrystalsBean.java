package br.ufmg.dcc.nanocomp.bean;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.LoggerFactory;

import br.ufmg.dcc.nanocomp.dao.DaoException;
import br.ufmg.dcc.nanocomp.dao.CrystalDao;
import br.ufmg.dcc.nanocomp.model.PhotonicCrystal;

@ViewScoped
@ManagedBean(name="crystalsBean")
public class CrystalsBean extends AbstractBean {
	
	private static final long serialVersionUID = 1L;
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CrystalsBean.class);

	private List<PhotonicCrystal> crystals;
	
	@PostConstruct
	public void init() {
		this.crystals = getDao(CrystalDao.class).listSimulationsByUser(getUser());
	}
	
	public void upload(FileUploadEvent file) {
		try (Scanner scanner = new Scanner(file.getFile().getInputstream())){
			PhotonicCrystal s = new PhotonicCrystal();
			s.setName(file.getFile().getFileName());
			s.setOwner(getSessionBean(LoginBean.class).getUser());
			s.setDate(new Date());
			s.setCtl(scanner.useDelimiter("\\Z").next());
			getDao(CrystalDao.class).save(s);
			addMessage("cristal carregada com sucesso!!!");
			this.crystals = getDao(CrystalDao.class).listSimulationsByUser(getUser());
		} catch (Exception e) {
			addError("Não foi possível salvar o cristal");
			LOGGER.error("It was not possible to save crystal",e);
		}
	}
	
	public void delete(PhotonicCrystal s) {
		try {
			getDao(CrystalDao.class).delete(s);
			this.crystals.remove(s);
			addMessage("Cristal apagado com sucesso");
		} catch (DaoException e) {
			addError("Não foi possível salvar a cristal");
			LOGGER.error("It was not possible to save crystal",e);
		}
	}
	
	public StreamedContent download(PhotonicCrystal crystal) {
		return new DefaultStreamedContent(new ByteArrayInputStream(crystal.getCtl().getBytes()),"text/plain",crystal.getName());
	}
	
	public List<PhotonicCrystal> getCrystals() {
		return crystals;
	}
	
	public void setCrystals(List<PhotonicCrystal> crystals) {
		this.crystals = crystals;
	}

}
