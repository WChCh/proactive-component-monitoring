package org.objectweb.proactive.examples.components.monitoring.nqueens.rules;

import org.objectweb.proactive.core.component.componentcontroller.analysis.Rule;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;

public class Finished implements Rule {

	private static final long serialVersionUID = 1L;
	
	private int WORKERS, BOARD_SIZE;
	private boolean[] done;
	
	private String report = "";
	
	
	public Finished(int workers, int board_size) {
		WORKERS = workers;
		BOARD_SIZE = board_size;
		done = new boolean[WORKERS];
	}
	
	@Override
	public boolean isSatisfied(MonitorControl monitor) {
		
		for(int i = 0; i < WORKERS; i++) {
			if(done[i]) continue;
			if(Integer.parseInt(monitor.getMetricValue("counter", "/Worker" + (i+1)).toString()) >= BOARD_SIZE*BOARD_SIZE/WORKERS) {
				done[i] = true;
				report = "Worker" + (i+1) + " is finished.";
				return false;
			}
		}
		return true;
	}

	@Override
	public String getLastReport() {
		return report;
	}

}
