package org.objectweb.proactive.examples.components.mmultiplier.tasks;

import java.io.Serializable;

import org.objectweb.proactive.examples.components.mmultiplier.Matrix;

/**
 * Tareas ejecutables por los workers.
 * 
 */
public abstract class MTask implements Serializable {

	private static final long serialVersionUID = 1L;
	private int x, y, bs;

	MTask(int posX, int posY, int blockSize) {
		x = posX;
		y = posY;
		bs = blockSize;
	}

	public abstract Matrix execute(Matrix matrix);

	public abstract boolean isNullTask();

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getBlockSize() {
		return bs;
	}

}
