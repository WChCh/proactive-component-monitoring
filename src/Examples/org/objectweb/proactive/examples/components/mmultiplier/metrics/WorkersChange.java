package org.objectweb.proactive.examples.components.mmultiplier.metrics;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

public class WorkersChange extends Metric<BooleanWrapper> {

	private static final long serialVersionUID = 1L;
	private long time, lastNotification;

	/**
	 * 
	 * @param time
	 *            Tiempo en milisegundos
	 */
	public WorkersChange(long time) {
		this.time = time;
		this.lastNotification = -1;
	}

	@Override
	public BooleanWrapper calculate(final Object[] params) {
		this.lastNotification = System.currentTimeMillis();
		return getValue();
	}

	@Override
	public BooleanWrapper getValue() {
		if (this.lastNotification < 0
				|| System.currentTimeMillis() - this.lastNotification < time) {
			return new BooleanWrapper(true);
		}
		return new BooleanWrapper(false);
	}

}
