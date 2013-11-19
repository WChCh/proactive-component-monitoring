package org.objectweb.proactive.examples.components.monitoring.nqueens.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEvent;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;

public class NumberOfTasksMetric extends Metric<IntWrapper> {

	private static final long serialVersionUID = 1L;

	public NumberOfTasksMetric() {
		subscribeTo(RemmosEventType.NEW_INCOMING_REQUEST_EVENT);
		subscribeTo(RemmosEventType.NEW_OUTGOING_REQUEST_EVENT);
		value = new IntWrapper(0);
	}

	@Override
	public IntWrapper calculate(Object[] args) {
		try {
			RemmosEvent event = (RemmosEvent) args[0];
			if (event.getType().equals(RemmosEventType.NEW_INCOMING_REQUEST_EVENT)) {
				value = new IntWrapper(0);
			} else if (event.getType().equals(RemmosEventType.NEW_OUTGOING_REQUEST_EVENT)) {
				value = new IntWrapper(value.getIntValue() + 1);
			}
		} catch(Exception e) {
			// Do nothing
		}
		return value;
	}
}
