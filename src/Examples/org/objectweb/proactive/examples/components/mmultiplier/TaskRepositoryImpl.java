package org.objectweb.proactive.examples.components.mmultiplier;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

public class TaskRepositoryImpl implements TaskRepository {

	private Queue<MTask> q = new LinkedBlockingQueue<MTask>();
	private Matrix m;

	@Override
	public void configure(List<MTask> tasks, Matrix matrix) {
		q.addAll(tasks);
		m = matrix;
	}

	@Override
	public MTask getTask() {
		return q.isEmpty() ? new MNullTask() : q.poll();
	}

	@Override
	public BooleanWrapper checkMatrixId(int id) {
		return id == m.getId() ? new BooleanWrapper(true) : new BooleanWrapper(false);
	}

	@Override
	public Matrix getMatrix() {
		return m;
	}

}
