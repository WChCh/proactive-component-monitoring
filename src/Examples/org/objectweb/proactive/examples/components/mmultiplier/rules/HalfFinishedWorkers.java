package org.objectweb.proactive.examples.components.mmultiplier.rules;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.component.componentcontroller.analysis.Rule;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;

public class HalfFinishedWorkers implements Rule {

	private static final long serialVersionUID = 1L;
	
	private int WORKERS, TASKS;
	private boolean[] done;
	
	private String report = "", path, metricName;
	
	
	public HalfFinishedWorkers(String path, String metricName, int workers, int tasks) {
		WORKERS = workers;
		TASKS = tasks;
		done = new boolean[WORKERS];
		this.path = path;
		this.metricName = metricName;
	}
	
	@Override
	public boolean isSatisfied(MonitorControl monitor) {
		
		for(int i = 0; i < WORKERS; i++) {
			if(done[i]) continue;
			String metricValue = monitor.getMetricValue(metricName, path + i).toString();
			if(Integer.parseInt(metricValue) > TASKS/2) {
				done[i] = true;
				report = "Worker" + i + " is half finished.";
				return false;
			}
		}
		return true;
	}

	@Override
	public String getLastReport() {
		return report;
	}

	@Override
	public List<String> getExecutionCommands() {
		return new ArrayList<String>();
	}

}
