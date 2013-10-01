package org.objectweb.proactive.examples.components.monitoring.nqueens.metrics;

import java.io.Serializable;

/**
 * Contiene la informacion sobre el uso de momria actual, sirve como wrapper 
 *
 */
public class MemoryInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long current, max;
	
	MemoryInfo() {
		current = 0L;
		max = 1L;
	}
	protected void setCurrent(long c) {
		current = new Long(c);
	}
	
	protected void setMax(long m) {
		max = new Long(m);
	}
	
	public Long getCurrent() {
		return current;
	}
	
	public Long getMax() {
		return max;
	}
	
	public String toString() {
		return "" + current/1000000 +"M / " + max/1000000 + "M  (" + current*1.0/max + "%)";
	}
}
