package org.objectweb.proactive.examples.components.mmultiplier;

public interface ResultRepository {

	public final static String ITF_NAME = "result-repo-itf";

	public void configure(int matrixSize, int nOfTasks);

	public void store(MTask task, Matrix result);

}
