package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier;

import java.io.Serializable;
import java.util.Random;

public class Matrix implements Serializable {

	private static final long serialVersionUID = 1L;
	private static int LIMIT = 10;
	private int SIZE;
	private int[][] MATRIX;


	/**
	 * Contruye una matriz cuadrada del tamaño indicado.
	 * @param size
	 */
	public Matrix(int size) {
		SIZE = size;
		MATRIX = new int[SIZE][SIZE];
	}

	public Matrix(int[][] matrix) {
		MATRIX = matrix;
		SIZE = matrix.length;
	}

	/**
	 * 
	 */
	public void fillRandom() {
		Random random = new Random();
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++) {
				MATRIX[i][j] = random.nextInt(LIMIT);
			}
		}
	}

	public int getSize() {
		return SIZE;
	}
	
	public int[][] getMatrix() {
		return MATRIX;
	}
}
