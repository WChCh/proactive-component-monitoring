package org.objectweb.proactive.examples.components.mmultiplier.rules;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.component.componentcontroller.analysis.Rule;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorController;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;

public class TaskSolvingTimeThreshold implements Rule {

	private static final long serialVersionUID = 1L;

	private double threshold;
	private String path, metricName, report;
	private int numOfTasks;
	
	public TaskSolvingTimeThreshold(String path, String metricName, double threshold) {
		this.path = path;
		this.metricName = metricName;
		this.threshold = threshold;
		this.report = "";
		this.numOfTasks = 1;
	}

	@Override
	public boolean isSatisfied(MonitorController monitor) {
		double avgTime = Double.parseDouble(monitor.getMetricValue(metricName, path).toString());
		if(avgTime != 0 && avgTime/1000000 < threshold) {
			report = "AVG TASK SOLVING TIME IS < " + threshold + " [secs] !";
			monitor.runMetric("workers-change", "parent"+"-external-"+Remmos.MONITOR_SERVICE_ITF);
			return false;
		}
		return true;
	}

	@Override
	public String getLastReport() {
		return report;
	}

	@Override
	public List<String> getExecutionCommands() {
		numOfTasks = numOfTasks*2;
		List<String> commands = new ArrayList<String>();
		commands.add("set-value($this/attribute::numOfTasks, \"" + numOfTasks + "\")");
		return commands;
	}

}
