package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier;


public interface Worker {

	public void configure(int[][] matrix, int base);
	
	public void multiply(Position position);

}
