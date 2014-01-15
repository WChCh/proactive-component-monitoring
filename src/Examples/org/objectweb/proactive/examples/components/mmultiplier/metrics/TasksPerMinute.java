package org.objectweb.proactive.examples.components.mmultiplier.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEvent;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

public class TasksPerMinute extends Metric<LongWrapper> {

	private static final long serialVersionUID = 1L;
	private static final int RECORDS = 3;
	long counter;
	long[] times;

	public TasksPerMinute() {
		subscribeTo(RemmosEventType.NEW_INCOMING_REQUEST_EVENT);
		counter = -1;
		times = new long[RECORDS];
		value = new LongWrapper(0);
	}

	@Override
	public LongWrapper calculate(final Object[] params) {
		counter++;
		RemmosEvent event = (RemmosEvent) params[0];
		long t = (long) event.getData();
		if(counter > 0) {
			int i = (int) counter%RECORDS;
			times[i] = t;
			if(counter >= RECORDS) {
				value = new LongWrapper(RECORDS*60000000L/(times[i] - times[(i+1)%RECORDS]));
			}
		}
		return getValue();
	}
	
	@Override
	public LongWrapper getValue() {
		return value;
	}
}
