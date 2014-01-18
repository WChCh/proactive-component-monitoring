package org.objectweb.proactive.examples.components.mmultiplier.tasks;

import org.objectweb.proactive.examples.components.mmultiplier.Matrix;

/**
 * Calculates the multiplication of a block
 *
 */
public class MMultiplyTask extends MTask {

	private static final long serialVersionUID = 1L;

	public MMultiplyTask(int posX, int posY, int blockSize) {
		super(posX, posY, blockSize);
	}

	@Override
	public Matrix execute(Matrix m) {
		int[][] result = new int[getBlockSize()][getBlockSize()];
		int[][] matrix = m.getMatrix();
		for (int i = 0; i < getBlockSize(); i++) {
			for (int j = 0; j < getBlockSize(); j++) {
				for (int s = 0; s < matrix.length; s++) {
					int r1 = getX() + i;
					int c1 = s;
					int r2 = s;
					int c2 = getY() + j;
					result[i][j] += matrix[r1][c1] * matrix[r2][c2];
				}
			}
		}
		return new Matrix(result, m.getId());
	}

	@Override
	public boolean isNullTask() {
		return false;
	}

}
