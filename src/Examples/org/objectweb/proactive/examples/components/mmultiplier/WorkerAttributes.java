package org.objectweb.proactive.examples.components.mmultiplier;

import org.objectweb.fractal.api.control.AttributeController;

public interface WorkerAttributes extends AttributeController {

	public void setNumOfTasks(String tasks);
	public String getNumOfTasks();

}
