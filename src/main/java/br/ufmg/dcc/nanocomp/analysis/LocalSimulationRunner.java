package br.ufmg.dcc.nanocomp.analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.dcc.nanocomp.ctl.parser.CtlFile;
import br.ufmg.dcc.nanocomp.dao.DaoException;
import br.ufmg.dcc.nanocomp.dao.DaoFactory;
import br.ufmg.dcc.nanocomp.dao.ExecutionDao;
import br.ufmg.dcc.nanocomp.model.Execution;
import br.ufmg.dcc.nanocomp.model.Result;
import br.ufmg.dcc.nanocomp.model.RobustnessAnalysis;
import br.ufmg.dcc.nanocomp.model.Value;

public class LocalSimulationRunner extends Thread implements SimulationRunner {

	private static final Pattern MEEP_FLUX_PATTERN = Pattern.compile("flux\\d:(.*)");
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalSimulationRunner.class);

	private CtlFile simulation;
	private RobustnessAnalysis robustnessAnalysis;
	private boolean original;

	private Process meepProcess;

	protected LocalSimulationRunner(CtlFile simulation, RobustnessAnalysis robustnessAnalysis, boolean original) {
		this.simulation = simulation;
		this.robustnessAnalysis = robustnessAnalysis;
		this.original = original;
	}

	@Override
	public void run() {
		try {
			meepProcess = Runtime.getRuntime().exec("meep");
			new Thread(()->{
				try (InputStreamReader reader = new InputStreamReader(meepProcess.getInputStream());
						BufferedReader bufferedReader = new BufferedReader(reader)){
					String line;
					Execution execution = new Execution();
					execution.setDate(new Date());
					execution.setOriginal(original);
					execution.setRobustnessAnalysis(robustnessAnalysis);
					execution.setResults(new ArrayList<>());
					while ((line=bufferedReader.readLine())!=null) {
						Matcher matcher = MEEP_FLUX_PATTERN.matcher(line);
						if(matcher.find()) {
							String[] numbers = matcher.group(1).split(", ");
							double frequency = Double.parseDouble(numbers[1]);
							Result r = new Result();
							execution.getResults().add(r);
							r.setFrequency(frequency);
							r.setValues(new ArrayList<>(numbers.length-2));
							for(int i = 2;i<numbers.length;i++) {
								Value v = new Value();
								v.setResult(r);
								v.setValue(Double.parseDouble(numbers[i]));
								r.getValues().add(v);
							}
						}
						DaoFactory.getInstance().getDao(ExecutionDao.class).save(execution);
					}
				} catch (IOException e) {
					LOGGER.error("Failed to read meep output",e);
				} catch (DaoException e) {
					LOGGER.error("Failed to store result",e);
				}
			}).start();
			new Thread(()->{
				try (InputStreamReader reader = new InputStreamReader(meepProcess.getErrorStream());
						BufferedReader bufferedReader = new BufferedReader(reader)){
					String line;
					while ((line=bufferedReader.readLine())!=null) {
						LOGGER.warn(line);
					}
				} catch (IOException e) {
					LOGGER.error("Failed to read meep error output",e);
				}
			}).start();
			try (OutputStreamWriter writer = new OutputStreamWriter(meepProcess.getOutputStream(), StandardCharsets.UTF_8)){
				simulation.write(writer);
			} catch(IOException e) {
				LOGGER.error("Failed to write simulation to meep process",e);
			}
			try {
				meepProcess.waitFor(2, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				LOGGER.error("Thread interrupted before meep execution finished",e);
			} finally {
				meepProcess.destroyForcibly();
			}
		} catch (IOException e) {
			LOGGER.error("Failed to start meep process",e);
		}
	}

}
