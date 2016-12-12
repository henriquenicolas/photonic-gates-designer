package br.ufmg.dcc.nanocomp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="meepServers")
public class MeepServer implements EntityInterface<Long> {

	private static final long serialVersionUID = 2L;

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String host;
	
	@Column
	private Integer port;
	
	@Column
	private String path;
	
	@Column
	private Long totalExecutions;
	
	@Column
	private Integer activeExecutions;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public Long getTotalExecutions() {
		return totalExecutions;
	}

	public void setTotalExecutions(Long totalExecutions) {
		this.totalExecutions = totalExecutions;
	}
	
	public Integer getActiveExecutions() {
		return activeExecutions;
	}

	public void setActiveExecutions(Integer activeExecutions) {
		this.activeExecutions = activeExecutions;
	}
}
