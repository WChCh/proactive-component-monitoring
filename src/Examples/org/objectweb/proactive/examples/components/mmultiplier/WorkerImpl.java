package org.objectweb.proactive.examples.components.mmultiplier;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;

public class WorkerImpl implements Worker, BindingController,
		LifeCycleController, WorkerAttributes {

	private ResultRepository resultRepo;
	private TaskRepository taskRepo;
	private Worker mySelf;

	private Matrix matrix;
	private int numOfTasks = 1;
	private boolean started;

	/**
	 * Get a task form the TaskRepository and execute it. Repeat until no more
	 * tasks on TaskRepository.
	 */
	@Override
	public void work() {
		if (!started)
			return;
		List<MTask> tasks = new ArrayList<MTask>();
		boolean hasTasksToDo = false;
		for (int i = 0; i < numOfTasks; i++) {
			MTask t = taskRepo.getTask();
			if (t.isNullTask())
				continue;
			hasTasksToDo = true;
			tasks.add(t);
		}
		if (!hasTasksToDo) {
			System.out.println(this.toString() + " I'm done. Going to sleep.");
			return;
		}
		// Check if the matrix is ok, otherwise it needs to be updated
		if (matrix == null
				|| !taskRepo.checkMatrixId(matrix.getId()).getBooleanValue()) {
			matrix = taskRepo.getMatrix();
		}
		// System.out.println(this.toString() + " I'm working!");
		for (int i = 0; i < tasks.size(); i++) {
			Matrix result = tasks.get(i).execute(matrix);
			resultRepo.store(tasks.get(i), result);
		}
		mySelf.work();
	}

	@Override
	public void bindFc(String name, Object itf) throws NoSuchInterfaceException {
		if (name.equals(ResultRepository.ITF_NAME)) {
			resultRepo = (ResultRepository) itf;
		} else if (name.equals(TaskRepository.ITF_NAME)) {
			taskRepo = (TaskRepository) itf;
		} else if (name.equals("loopback")) {
			mySelf = (Worker) itf;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public String[] listFc() {
		return new String[] { ResultRepository.ITF_NAME,
				TaskRepository.ITF_NAME, "loopback" };
	}

	@Override
	public Object lookupFc(String name) throws NoSuchInterfaceException {
		if (name.equals(ResultRepository.ITF_NAME)) {
			return resultRepo;
		} else if (name.equals(TaskRepository.ITF_NAME)) {
			return taskRepo;
		} else if (name.equals("loopback")) {
			return mySelf;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public void unbindFc(String name) throws NoSuchInterfaceException {
		if (name.equals(ResultRepository.ITF_NAME)) {
			resultRepo = null;
		} else if (name.equals(TaskRepository.ITF_NAME)) {
			taskRepo = null;
		} else if (name.equals("loopback")) {
			mySelf = null;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public String getFcState() {
		return null;
	}

	@Override
	public void startFc() throws IllegalLifeCycleException {
		started = true;
		// Because it's an easier way to put the worker to work when it's
		// created by the GCMScript
		work();
	}

	@Override
	public void stopFc() throws IllegalLifeCycleException {
		started = false;
	}

	@Override
	public void setNumOfTasks(String tasks) {
		numOfTasks = Integer.parseInt(tasks);
	}

	@Override
	public String getNumOfTasks() {
		return numOfTasks + "";
	}
}
