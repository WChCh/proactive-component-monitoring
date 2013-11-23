package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier.rules;

import org.objectweb.proactive.core.component.componentcontroller.analysis.Rule;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;

public class ResponseTimeLimit implements Rule {

	private static final long serialVersionUID = 1L;

	long limit;
	long lastTime;
	int lastResponse;
	
	String path, metricName, report = "";

	
	public ResponseTimeLimit(String path, String metricName, int limit) {
		this.path = path;
		this.metricName = metricName;
		this.limit = limit;
		lastTime = -1;
		lastResponse = 0;
		
	}

	@Override
	public boolean isSatisfied(MonitorControl monitor) {
		int events = Integer.parseInt(monitor.getMetricValue(metricName, path).toString());
		int diff = events - lastResponse;
		lastResponse = events;
	
		if(diff == 0) {
			return true;
		}

		if(lastTime == -1) {
			lastTime = System.currentTimeMillis();
			return true;
		}
		
		long time = System.currentTimeMillis();
		long time_diff = time - lastTime;
		lastTime = time;
		if(time_diff/diff > limit) {
			report = "ResponseTime > " + limit/1000 + " [secs] !";
			return false;
		}
		return true;
	}

	@Override
	public String getLastReport() {
		return report;
	}

}
