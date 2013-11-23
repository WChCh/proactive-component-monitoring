package org.objectweb.proactive.core.component.componentcontroller.analysis;

import java.io.Serializable;
import java.util.Map;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;


/**
 * Thread encargado de hacer la revision de las reglas
 *
 */
public class Analyzer extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;

	private long delay = 5000;
	private Map<String, Rule> rules;
	private MonitorControl monitor;
	String hostComponent;


	void init(MonitorControl monitor, Map<String, Rule> rules, String host) {
		this.monitor = monitor;
		this.rules = rules;
		this.hostComponent = host;
	}

	@Override
	public void run() {
		while(true) {
			for(String name : rules.keySet()) {
				Rule rule = rules.get(name);
				if(!rule.isSatisfied(monitor)) {
					System.out.println("[" + System.currentTimeMillis() + "]" + "[" + hostComponent + "][Analyzer]"
							+ " rule " + name + "unsatisfied: "
							+ rule.getLastReport());
				}
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
