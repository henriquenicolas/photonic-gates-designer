package br.ufmg.dcc.nanocomp.analysis;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import br.ufmg.dcc.nanocomp.model.Execution;
import br.ufmg.dcc.nanocomp.model.Result;
import br.ufmg.dcc.nanocomp.model.RobustnessAnalysis;
import br.ufmg.dcc.nanocomp.model.Value;

@ClientEndpoint()
public class AnalysisEndpoint {
	
	private Execution execution;

	public AnalysisEndpoint(RobustnessAnalysis robustnessAnalysis, boolean original) {
		execution = new Execution();
		execution.setDate(new Date());
		execution.setOriginal(original);
		execution.setRobustnessAnalysis(robustnessAnalysis);
		execution.setResults(new ArrayList<>());
	}
	
	public static void main(String[] args) throws IOException, DeploymentException, InterruptedException {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		String uri = "ws://localhost:8080/meep-server/analysis/2?id=3";
		AnalysisEndpoint endPoint = new AnalysisEndpoint(null, false);
		for(int i = 0; i<10000; i++) {
			Session session = container.connectToServer(endPoint, URI.create(uri));
			session.getAsyncRemote().sendText("1,2,3");
		}
	}
	
	@OnMessage
	public void onMessage(String text, Session session) {
		try {
			session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] numbers = text.split(",");
		double frequency = Double.parseDouble(numbers[0]);
		Result r = new Result();
		execution.getResults().add(r);
		r.setFrequency(frequency);
		r.setValues(new ArrayList<>(numbers.length-1));
		for(int i = 1;i<numbers.length;i++) {
			Value v = new Value();
			v.setResult(r);
			v.setValue(Double.parseDouble(numbers[i]));
			r.getValues().add(v);
		}
	}
	
	@OnClose
	public void onClose(Session session) {
		
	}
}
