package org.objectweb.proactive.examples.components.monitoring.nqueens.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEvent;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;

public class NumberOfTasksMetric extends Metric<Integer> {

	private static final long serialVersionUID = 1L;

	public NumberOfTasksMetric() {
		subscribeTo(RemmosEventType.NEW_INCOMING_REQUEST_EVENT);
		subscribeTo(RemmosEventType.NEW_OUTGOING_REQUEST_EVENT);
		value = 0;
	}

	@Override
	public Integer calculate(Object[] args) {
		try {
			RemmosEvent event = (RemmosEvent) args[0];
			if (event.getType().equals(RemmosEventType.NEW_INCOMING_REQUEST_EVENT)) {
				value = 0;
			} else if (event.getType().equals(RemmosEventType.NEW_OUTGOING_REQUEST_EVENT)) {
				value++;
			}
		} catch(Exception e) {
			// Do nothing
		}
		return value;
	}
}
