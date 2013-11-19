package org.objectweb.proactive.core.component.componentcontroller.analysis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.proactive.core.component.componentcontroller.AbstractPAComponentController;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;

/**
 * 
 * @author mnip91
 *
 */
public class AnalysisControllerImpl extends AbstractPAComponentController
		implements AnalysisController, BindingController {

	private static final long serialVersionUID = 1L;

	private MonitorControl monitor;

	private Map<String, Rule> rules = new ConcurrentHashMap<String, Rule>();
	private Analyzer analyzer = new Analyzer();


	@Override
	public void setDelay(long time) {
		return;
	}

	@Override
	public void addRule(String name, Rule rule) {
		rules.put(name, rule);
	}

	@Override
	public void removeRule(String name) {
		rules.remove(name);
	}

	@Override
	public void start() {
		analyzer.init(monitor, rules, hostComponent.getComponentParameters().getName());
		analyzer.start();
	}

	@Override
	public void stop() {
		analyzer.interrupt();
	}


	@Override
	public void bindFc(String name, Object itf)
			throws NoSuchInterfaceException {
		if (name.equals(Remmos.MONITOR_SERVICE_ITF)) {
			monitor = (MonitorControl) itf;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public String[] listFc() {
		return new String[] { Remmos.MONITOR_SERVICE_ITF };
	}

	@Override
	public Object lookupFc(String name) throws NoSuchInterfaceException {
		if(name.equals(Remmos.MONITOR_SERVICE_ITF)) {
			return monitor;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public void unbindFc(String name) throws NoSuchInterfaceException {
		if(name.equals(Remmos.MONITOR_SERVICE_ITF)) {
			monitor = null;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

}
