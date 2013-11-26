package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier.metrics;

import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

public class RepositoryProgress extends Metric<LongWrapper> {

	private static final long serialVersionUID = 1L;
	AtomicInteger counter;

	public RepositoryProgress() {
		subscribeTo(RemmosEventType.NEW_INCOMING_REQUEST_EVENT);
		counter = new AtomicInteger(0);
	}

	@Override
	public LongWrapper calculate(final Object[] params) {
		counter.incrementAndGet();
		return getValue();
	}
	
	@Override
	public LongWrapper getValue() {
		return new LongWrapper(counter.get());
	}
}
