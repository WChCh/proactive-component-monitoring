package org.objectweb.proactive.examples.components.monitoring.nqueens.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEvent;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;

public class RemainingWorkMetric extends Metric<Integer> {

	private static final long serialVersionUID = 1L;
	
	boolean isReceiving = false;
	
	public RemainingWorkMetric() {
		subscribeTo(RemmosEventType.OUTGOING_REQUEST_EVENT);
		value = 0;
	}

	public Integer calculate(RemmosEvent e) {
		if (isReceiving) {
			value++;
		} else {
			isReceiving = true;
		}
		return value;
	}
}
