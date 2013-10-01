package org.objectweb.proactive.examples.components.monitoring.nqueens.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEvent;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;

public class NumberOfWorkersMetric extends Metric<Integer> {

	private static final long serialVersionUID = 1L;

	public NumberOfWorkersMetric() {
		subscribeTo(RemmosEventType.NEW_INCOMING_REQUEST_EVENT);
		subscribeTo(RemmosEventType.NEW_OUTGOING_REQUEST_EVENT);
		value = 0;
	}

	public Integer calculate(RemmosEvent e) {
		if (e.getType().equals(RemmosEventType.NEW_INCOMING_REQUEST_EVENT)) {
			value = 0;
		} else if (e.getType().equals(RemmosEventType.NEW_OUTGOING_REQUEST_EVENT)) {
			value++;
		}
		return value;
	}
}
