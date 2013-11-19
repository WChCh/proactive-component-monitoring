package org.objectweb.proactive.examples.components.monitoring.nqueens.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;

public class WorkerTasksCounterMetric extends Metric<IntWrapper> {

	private static final long serialVersionUID = 1L;

	public WorkerTasksCounterMetric() {
		subscribeTo(RemmosEventType.OUTGOING_REQUEST_EVENT);
		value = new IntWrapper(0);
	}

	public IntWrapper calculate(Object[] args) {
		value = new IntWrapper(value.getIntValue() + 1);
		return value;
	}
}
