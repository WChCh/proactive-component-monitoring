package org.objectweb.proactive.examples.components.mmultiplier;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.proactive.examples.components.mmultiplier.tasks.MMultiplyTask;
import org.objectweb.proactive.examples.components.mmultiplier.tasks.MTask;

/**
 * Generate the blocks multiplication tasks and configures the system for a new
 * multiplication.
 * 
 * @author mnip91
 * 
 */
public class MasterImpl implements MatrixMultiplier, BindingController {

	private WorkerMulticast workers;
	private ResultRepository resultRepo;
	private TaskRepository taskRepo;
	private static int matrixId = 0;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void multiply(int[][] matrix, int bs) {
		List<MTask> tasks = new ArrayList<MTask>();
		int k = matrix.length / bs;
		// generate tasks
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < k; j++) {
				tasks.add(new MMultiplyTask(i * bs, j * bs, bs));
			}
		}
		taskRepo.configure(tasks, new Matrix(matrix, matrixId++));
		resultRepo.configure(matrix.length, tasks.size());
		workers.work();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bindFc(String name, Object itf) throws NoSuchInterfaceException {
		if (name.equals(MMConstants.WORKER_MULTICAST_ITF)) {
			workers = (WorkerMulticast) itf;
		} else if (name.equals(MMConstants.RESULT_REPOSITORY_ITF)) {
			resultRepo = (ResultRepository) itf;
		} else if (name.equals(MMConstants.TASK_REPOSITORY_ITF)) {
			taskRepo = (TaskRepository) itf;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] listFc() {
		return new String[] { MMConstants.WORKER_MULTICAST_ITF,
				MMConstants.RESULT_REPOSITORY_ITF, MMConstants.TASK_REPOSITORY_ITF };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object lookupFc(String name) throws NoSuchInterfaceException {
		if (name.equals(MMConstants.WORKER_MULTICAST_ITF)) {
			return workers;
		} else if (name.equals(MMConstants.RESULT_REPOSITORY_ITF)) {
			return resultRepo;
		} else if (name.equals(MMConstants.TASK_REPOSITORY_ITF)) {
			return taskRepo;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unbindFc(String name) throws NoSuchInterfaceException {
		if (name.equals(MMConstants.WORKER_MULTICAST_ITF)) {
			workers = null;
		} else if (name.equals(MMConstants.RESULT_REPOSITORY_ITF)) {
			resultRepo = null;
		} else if (name.equals(MMConstants.TASK_REPOSITORY_ITF)) {
			taskRepo = null;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

}
