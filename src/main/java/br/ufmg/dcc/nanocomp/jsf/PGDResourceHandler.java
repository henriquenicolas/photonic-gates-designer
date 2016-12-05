package br.ufmg.dcc.nanocomp.jsf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.context.FacesContext;

import br.ufmg.dcc.nanocomp.peg.PEG;

public class PGDResourceHandler extends ResourceHandlerWrapper {
	
	private ResourceHandler wrapped;

	public PGDResourceHandler(ResourceHandler wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public Resource createResource(String resourceName, String libraryName) {
		if("js".equals(libraryName) && "ctl-parser.js".equals(resourceName)) {
			File grammar = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/grammar/ctl-parser.peg"));
			File script = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/js/ctl-parser.js"));
			try {
				if((grammar.exists()||grammar.createNewFile()) &&
						grammar.lastModified() > script.lastModified()) {
					try (Scanner in = new Scanner(grammar);
							FileWriter out = new FileWriter(script)) {
						out.write("ctlParser = ");
						out.write(PEG.getInstance().compile(in.useDelimiter("\\Z").next()));
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return super.createResource(resourceName,libraryName);
	}
	
	@Override
	public ResourceHandler getWrapped() {
		return wrapped;
	}

}
