package org.objectweb.proactive.core.component.componentcontroller.analysis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.proactive.core.component.componentcontroller.AbstractPAComponentController;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;
import org.objectweb.proactive.extra.component.fscript.control.PAReconfigurationController;


public class AnalysisControllerImpl extends AbstractPAComponentController
		implements AnalysisController, BindingController {

	private static final long serialVersionUID = 1L;

	private MonitorControl monitor;
	private AnalysisController mySelf;
	private PAReconfigurationController reconfiguration;
	
	private Map<String, Rule> rules = new ConcurrentHashMap<String, Rule>();


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
	public void analyze() {
		for(String name : rules.keySet()) {
			Rule rule = rules.get(name);
			if(!rule.isSatisfied(monitor)) {
				System.out.println("[" + System.currentTimeMillis() + "]" + "["
						+ hostComponent.getComponentParameters().getName()
						+ "][Analyzer] rule " + name + "unsatisfied: "
						+ rule.getLastReport());
				for(String s : rule.getExecutionCommands())
					reconfiguration.execute(s);
			}
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mySelf.analyze();
	}


	@Override
	public void bindFc(String name, Object itf) throws NoSuchInterfaceException {
		if (name.equals(Remmos.MONITOR_SERVICE_ITF)) {
			monitor = (MonitorControl) itf;
		} else if (name.equals(Remmos.ACTIONS_ITF)) {
			reconfiguration = (PAReconfigurationController) itf;
		} else if (name.equals("loopback")) {
			mySelf = (AnalysisController) itf;
		} else {
			throw new NoSuchInterfaceException(name + " ctm ------! ");
		}
	}

	@Override
	public String[] listFc() {
		return new String[] { Remmos.MONITOR_SERVICE_ITF, Remmos.ACTIONS_ITF, "loopback" };
	}

	@Override
	public Object lookupFc(String name) throws NoSuchInterfaceException {
		if(name.equals(Remmos.MONITOR_SERVICE_ITF)) {
			return monitor;
		} else if (name.equals(Remmos.ACTIONS_ITF)) {
			return reconfiguration;
		} else if (name.equals("loopback")) {
			return mySelf;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public void unbindFc(String name) throws NoSuchInterfaceException {
		if(name.equals(Remmos.MONITOR_SERVICE_ITF)) {
			monitor = null;
		} else if (name.equals(Remmos.ACTIONS_ITF)) {
			reconfiguration = null;
		} else if (name.equals("loopback")) {
			mySelf = null;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

}
