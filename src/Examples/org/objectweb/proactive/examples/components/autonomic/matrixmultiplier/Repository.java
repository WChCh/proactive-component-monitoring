package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier;

public interface Repository {

	public void configure(int n, int base);

	public void store(int[][] subMatrix, int i, int j);

}
