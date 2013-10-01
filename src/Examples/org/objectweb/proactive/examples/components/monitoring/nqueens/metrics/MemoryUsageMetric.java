package org.objectweb.proactive.examples.components.monitoring.nqueens.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEvent;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;

/**
 * Metric de uso de momeria
 *
 */
public class MemoryUsageMetric extends Metric<MemoryInfo>{

	private static final long serialVersionUID = 1L;

	public MemoryUsageMetric() {
		value = new MemoryInfo();
	}

	public MemoryInfo getValue() {
		value.setCurrent(Runtime.getRuntime().totalMemory());
		value.setMax(Runtime.getRuntime().maxMemory());
		return value;
	}

	public String toString() {
		return "MemoryUsageMetric";
	}
}
