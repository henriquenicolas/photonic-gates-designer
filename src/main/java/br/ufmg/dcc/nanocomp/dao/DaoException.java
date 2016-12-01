package br.ufmg.dcc.nanocomp.dao;

public class DaoException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public DaoException(String string) {
		super(string);
	}
	
	public DaoException(Throwable cause) {
		super(cause);
	}

	public DaoException(String string, Throwable cause) {
		super(string,cause);
	}

}
