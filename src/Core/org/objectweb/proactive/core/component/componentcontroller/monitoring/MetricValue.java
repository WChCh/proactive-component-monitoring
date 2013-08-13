package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;

/**
 * Utilizada para handlear los casos en que getMetricValue no es exitoso,
 * originalmente si no se encuentra la metrica se retorna null pero
 * debido a que el valor retornado es un FutureProxy se complican las cosas
 * a la hora de averiguar si se retorno o no null.
 *
 */
public class MetricValue implements Serializable {

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
