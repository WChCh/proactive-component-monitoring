package org.objectweb.proactive.examples.components.mmultiplier;

import java.io.Serializable;

/**
 * int[][] matrix wrapper
 *
 */
public class Matrix implements Serializable {

	private static final long serialVersionUID = 1L;
	private int[][] m;
	private int id;

	public Matrix(int[][] matrix, int id) {
		this.m = matrix;
		this.id = id;
	}

	public int[][] getMatrix() {
		return m;
	}

	public int getId() {
		return id;
	}

}
