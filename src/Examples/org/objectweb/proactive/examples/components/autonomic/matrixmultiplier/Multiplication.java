package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier;

import java.io.Serializable;

public class Multiplication implements Serializable {

	private static final long serialVersionUID = 1L;
	private int size, row1, col1, row2, col2;
	
	Multiplication(int m, int r1, int c1, int r2, int c2) {
		size = m;
		row1 = r1;
		col1 = c1;
		row2 = r2;
		col2 = c2;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getRow1() {
		return row1;
	}
	
	public int getRow2() {
		return row2;
	}
	
	public int getCol1() {
		return col1;
	}
	
	public int getCol2() {
		return col2;
	}

}
