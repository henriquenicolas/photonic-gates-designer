package br.ufmg.dcc.nanocomp.analysis;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.dcc.nanocomp.dao.ExecutionDao;
import br.ufmg.dcc.nanocomp.dao.jpa.JpaDaoFactory;
import br.ufmg.dcc.nanocomp.model.Execution;
import br.ufmg.dcc.nanocomp.model.Result;
import br.ufmg.dcc.nanocomp.model.Value;

public class MeepServerSimulationRunner extends Thread implements SimulationRunner {

	private static final Pattern MEEP_FLUX_PATTERN = Pattern.compile("flux\\d:(.*)");
	private static final Logger LOGGER = LoggerFactory.getLogger(MeepServerSimulationRunner.class);

	private Execution execution;

	protected MeepServerSimulationRunner(Execution execution) {
		this.execution = execution;
	}

	@Override
	public void run() {
		try {
			URL url = new URL("http", execution.getMeepServer().getHost(), execution.getMeepServer().getPort(), execution.getMeepServer().getPath()+"/process");
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)){
				writer.write(execution.getCtl());
			} catch(IOException e) {
				LOGGER.error("Failed to write simulation to meep process",e);
			}
			try (Scanner scanner = new Scanner(connection.getInputStream())){
				while (scanner.hasNextLine()) {
					Matcher matcher = MEEP_FLUX_PATTERN.matcher(scanner.nextLine());
					if(matcher.find()) {
						String[] numbers = matcher.group(1).split(", ");
						double frequency = Double.parseDouble(numbers[1]);
						Result r = new Result();
						execution.getResults().add(r);
						r.setExecution(execution);
						r.setFrequency(frequency);
						r.setValues(new ArrayList<>(numbers.length-2));
						for(int i = 2;i<numbers.length;i++) {
							Value v = new Value();
							v.setResult(r);
							v.setValue(Double.parseDouble(numbers[i]));
							r.getValues().add(v);
						}
					}
				}
			} catch (IOException e) {
				LOGGER.error("Failed to read meep output",e);
			} catch (Exception e) {
				LOGGER.error("Unexpected error",e);
			}
			try(JpaDaoFactory daoFactory = new JpaDaoFactory()) {
				daoFactory.getDao(ExecutionDao.class).save(execution);
			} catch (Exception e) {
				LOGGER.error("Failed to store result",e);
			}
		} catch (IOException e) {
			LOGGER.error("Failed to start meep process",e);
		}
	}

}
