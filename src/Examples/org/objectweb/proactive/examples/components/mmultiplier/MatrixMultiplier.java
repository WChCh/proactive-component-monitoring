package org.objectweb.proactive.examples.components.mmultiplier;

public interface MatrixMultiplier {

	/**
	 * Starts the calculation of the multiplication of matrix*matrix. The
	 * processes is achieved by multiplying little blocks of the matrix until
	 * cover all the matrix.
	 * 
	 * @param matrix
	 *            Square matrix to multiply
	 * @param blockSize
	 *            Size of the square blocks to use during the process. The value
	 *            of blockSize should be less or equals to matrix's size.
	 */
	public void multiply(int[][] matrix, int blockSize);

}
