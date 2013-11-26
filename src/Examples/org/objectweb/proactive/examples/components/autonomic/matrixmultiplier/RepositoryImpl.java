package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;

public class RepositoryImpl implements Repository, BindingController {

	private int[][] matrix;
	private int tasks;

	private ResultReceiver resultReceiver;

	
	@Override
	public void configure(int n, int base) {
		matrix = new int[n][n];
		tasks = (matrix.length/base) * (matrix.length/base);
	}

	@Override
	public void store(int[][] subMatrix, int i, int j) {
		for(int x = 0; x < subMatrix.length; x++) {
			for(int y = 0; y < subMatrix.length; y++) {
				matrix[i + x][j + y] = subMatrix[x][y];
			}
		}
		tasks--;
		if(tasks <= 0) {
			/*for(int x = 0; x < matrix.length; x++) {
				for(int y = 0; y < matrix.length; y++) {
					System.out.print(matrix[x][y]+ "\t");
				}
				System.out.println(";");
			}
			System.out.println("--->" + matrix.length + " " + matrix[0].length);
			System.out.println("END TIME: " + System.currentTimeMillis());
			*/
			resultReceiver.receive(matrix);
		}
	}

	@Override
	public void bindFc(String name, Object itf)
			throws NoSuchInterfaceException {
		if(name.equals("result-reciver-itf")) {
			resultReceiver = (ResultReceiver) itf;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public String[] listFc() {
		return new String[] { "result-reciver-itf", "repository-itf" };
	}

	@Override
	public Object lookupFc(String name) throws NoSuchInterfaceException {
		if(name.equals("result-reciver-itf")) {
			return resultReceiver;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public void unbindFc(String name) throws NoSuchInterfaceException {
		if(name.equals("result-reciver-itf")) {
			resultReceiver = null;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

}
