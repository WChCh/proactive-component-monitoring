package org.objectweb.proactive.examples.components.mmultiplier;

/**
 * An empty task, means that there are no more tasks
 *
 */
public class MNullTask extends MTask {

	private static final long serialVersionUID = 1L;

	MNullTask() {
		super(-1, -1, -1);
	}

	@Override
	public Matrix execute(Matrix matrix) {
		return null;
	}

	@Override
	public boolean isNullTask() {
		return true;
	}

}
