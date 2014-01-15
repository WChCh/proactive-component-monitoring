package org.objectweb.proactive.examples.components.mmultiplier;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;

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
		if (name.equals("worker-multicast-itf")) {
			workers = (WorkerMulticast) itf;
		} else if (name.equals(ResultRepository.ITF_NAME)) {
			resultRepo = (ResultRepository) itf;
		} else if (name.equals(TaskRepository.ITF_NAME)) {
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
		return new String[] { "worker-multicast-itf",
				ResultRepository.ITF_NAME, TaskRepository.ITF_NAME };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object lookupFc(String name) throws NoSuchInterfaceException {
		if (name.equals("worker-multicast-itf")) {
			return workers;
		} else if (name.equals(ResultRepository.ITF_NAME)) {
			return resultRepo;
		} else if (name.equals(TaskRepository.ITF_NAME)) {
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
		if (name.equals("worker-multicast-itf")) {
			workers = null;
		} else if (name.equals(ResultRepository.ITF_NAME)) {
			resultRepo = null;
		} else if (name.equals(TaskRepository.ITF_NAME)) {
			taskRepo = null;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

}
