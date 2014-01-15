package org.objectweb.proactive.examples.components.mmultiplier;

import java.util.List;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

/**
 * A tasks queue that represent the pending tasks to be executed by the
 * MatrixMultiplier
 * 
 */
public interface TaskRepository {

	public final static String ITF_NAME = "task-repo-itf";

	/**
	 * 
	 * @param positions
	 * @param blockSize
	 * @param matrix
	 */
	public void configure(List<MTask> positions, Matrix matrix);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public BooleanWrapper checkMatrixId(int id);

	/**
	 * Returns a new tasks
	 * 
	 * @return
	 */
	public MTask getTask();
	
	public Matrix getMatrix();	

}
