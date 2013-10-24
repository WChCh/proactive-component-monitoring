package org.objectweb.proactive.examples.components.monitoring.nqueens.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;

public class WorkerTasksCounterMetric extends Metric<Integer> {

	private static final long serialVersionUID = 1L;

	public WorkerTasksCounterMetric() {
		subscribeTo(RemmosEventType.OUTGOING_REQUEST_EVENT);
		value = 0;
	}

	public Integer calculate(Object[] args) {
		value++;
		return value;
	}
}
