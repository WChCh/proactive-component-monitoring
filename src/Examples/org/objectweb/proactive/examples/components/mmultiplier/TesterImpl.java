package org.objectweb.proactive.examples.components.mmultiplier;

import java.util.Random;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;


public class TesterImpl implements Tester, ResultReceiver, BindingController {

	private MatrixMultiplier matrixMultiplier;
	private int iN, N, fN, iB, B, fB;
	private long[][][] times;
	private int tests, testsCounter;


	@Override
	public void start(int initSize, int endSize, int initBase, int endBase, int nOfTests) {
		iN = initSize;
		fN = endSize;
		iB = initBase;
		fB = endBase;
		tests = nOfTests;
		if(fN >= iN && fB >= iB) {
			N = iN;
			B = iB;
			times = new long[fN-iN + 1][fB - iB + 1][tests];
			testsCounter = 0;
			System.out.println("[TESTER] N=["+iN+","+fN+"] B=["+iB+","+fB+"] t="+tests+" ...");
			test();
		} else {
			System.out.println("[TESTER] Nothing to do...");
		}
	}

	private void test() {
		int size = (int) Math.pow(2, N);
		int[][] matrix = new int[size][size];
		Random random = new Random();
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				matrix[i][j] = random.nextInt(9);
			}
		}
		int base = (int) Math.pow(2, B);
		times[N-iN][B-iB][testsCounter] = System.currentTimeMillis();
		matrixMultiplier.multiply(matrix, base);
	}

	@Override
	public void receive(int[][] matrix) {
		times[N-iN][B-iB][testsCounter] = System.currentTimeMillis() - times[N-iN][B-iB][testsCounter];
		testsCounter++;
		if(testsCounter >= tests) {
			double avg = 0.0;
			for(int k = 0; k < times[0][0].length; k++) {
				avg += times[N-iN][B-iB][k];
			}
			avg = avg/times[0][0].length;
			System.out.println("[TESTER][RESULTS][N=" + (int) Math.pow(2, N) 
					+ "][B=" + (int) Math.pow(2, B) + "] " + (long) avg);
			
			testsCounter = 0;
			B++;
		}
		if(B > fB) {
			B = iB;
			N++;
		}
		if(N > fN) {
			System.out.println("[TESTER] Done.");
			return;
		}
		test();
	}


	@Override
	public void bindFc(String name, Object itf)
			throws NoSuchInterfaceException {
		if(name.equals("matrix-multiplier-itf")) {
			matrixMultiplier = (MatrixMultiplier) itf;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public String[] listFc() {
		return new String[] { "matrix-multiplier-itf" };
	}

	@Override
	public Object lookupFc(String name) throws NoSuchInterfaceException {
		if(name.equals("matrix-multiplier-itf")) {
			return matrixMultiplier;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public void unbindFc(String name) throws NoSuchInterfaceException {
		if(name.equals("matrix-multiplier-itf")) {
			matrixMultiplier = null;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

}
