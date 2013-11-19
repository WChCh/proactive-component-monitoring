package org.objectweb.proactive.examples.components.monitoring.nqueens.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;

public class RemainingWorkMetric extends Metric<IntWrapper> {

	private static final long serialVersionUID = 1L;
	
	boolean isReceiving = false;
	
	public RemainingWorkMetric() {
		subscribeTo(RemmosEventType.OUTGOING_REQUEST_EVENT);
		value = new IntWrapper(0);
	}

	public IntWrapper calculate(Object[] args) {
		if (isReceiving) {
			value = new IntWrapper(value.getIntValue() + 1);
		} else {
			isReceiving = true;
		}
		return value;
	}
}
