package org.objectweb.proactive.examples.components.mmultiplier.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEvent;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;
import org.objectweb.proactive.core.util.wrapper.DoubleWrapper;


public class WorkerTime extends Metric<DoubleWrapper>{
	private static final long serialVersionUID = 1L;
	private static final int RECORDS = 3; 
	long counter, lastTime;
	long[] times;

	
	public WorkerTime() {
		subscribeTo(RemmosEventType.NEW_INCOMING_REQUEST_EVENT);
		counter = 0;
		times = new long[RECORDS];
		value = new DoubleWrapper(0);
	}

	@Override
	public DoubleWrapper calculate(final Object[] params) {
		counter++;
		RemmosEvent event = (RemmosEvent) params[0];
		long t = (long) event.getData();
		if(counter <= 1) {
			lastTime = t;
		}
		else {
			times[(int) (counter%RECORDS)] = t - lastTime;
			lastTime = t;
			if(counter > RECORDS) {
				long total = 0;
				for(int i = 0; i < RECORDS; i++)
					total += times[i];
				value = new DoubleWrapper(total*1.0/RECORDS);
			}
		}
		return value;
	}
	
	@Override
	public DoubleWrapper getValue() {
		return value;
	}
}
