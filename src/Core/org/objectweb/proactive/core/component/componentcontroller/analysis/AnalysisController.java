package org.objectweb.proactive.core.component.componentcontroller.analysis;

public interface AnalysisController {

	public void setDelay(long time);

	public void addRule(String name, Rule rule);
	
	public void removeRule(String name);

	public void analyze();

}
