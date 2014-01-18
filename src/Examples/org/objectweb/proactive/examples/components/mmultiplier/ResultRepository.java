package org.objectweb.proactive.examples.components.mmultiplier;

import org.objectweb.proactive.examples.components.mmultiplier.tasks.MTask;

public interface ResultRepository {

	public final static String ITF_NAME = "result-repo-itf";

	public void configure(int matrixSize, int nOfTasks);

	public void store(MTask task, Matrix result);

}
