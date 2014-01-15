package org.objectweb.proactive.core.component.componentcontroller.analysis;

import java.io.Serializable;
import java.util.List;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;

public interface Rule extends Serializable {

	/**
	 * Verifica si la regla se cumple
	 * 
	 * @param monitor
	 * @return	True si se cumple, de lo contrario, False.
	 */
	public boolean isSatisfied(MonitorControl monitor);
	
	/**
	 * Retorna informacion obtenida en la ultima revision de la regla
	 * con isSatisfied()
	 * 
	 * @return
	 */
	public String getLastReport();
	public List<String> getExecutionCommands();
}
