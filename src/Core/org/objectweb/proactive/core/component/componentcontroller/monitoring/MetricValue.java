package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;

public class MetricValue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2307647223100451000L;
	Object value;
	boolean located;
	
	MetricValue(Object value, boolean located) {
		this.value = value;
		this.located = located;
	}
	
	public Object getValue() { return value; }
	public boolean isLocated() { return located; }
}
