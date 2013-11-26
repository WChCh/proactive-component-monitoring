package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

public class MasterProgress extends Metric<LongWrapper> {

	private static final long serialVersionUID = 1L;
	long counter;

	public MasterProgress() {
		subscribeTo(RemmosEventType.NEW_INCOMING_REQUEST_EVENT);
		counter = 0;
	}

	@Override
	public LongWrapper calculate(final Object[] params) {
		counter++;
		return getValue();
	}
	
	@Override
	public LongWrapper getValue() {
		return new LongWrapper(counter);
	}
}
