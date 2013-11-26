package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;

public class WorkerImpl implements Worker, BindingController {

	private Repository repository;
	private int[][] matrix;
	private int base;


	@Override
	public void configure(int[][] matrix, int base) {
		this.matrix = matrix;
		this.base = base;
	}

	@Override
	public void multiply(Position position) {
		int[][] result = new int[base][base];
		for(int i = 0; i < base; i++) {
			for(int j = 0; j < base; j++) {
				for(int s = 0; s < matrix.length; s++) {
					int r1 = position.getX() + i;
					int c1 = s;
					int r2 = s;
					int c2 = position.getY() + j;
					result[i][j] += matrix[r1][c1] * matrix[r2][c2];
				}
			}
		}

		repository.store(result, position.getX(), position.getY());
	}

	@Override
	public void bindFc(String name, Object itf)
			throws NoSuchInterfaceException {
		if(name.equals("repository-itf")) {
			repository = (Repository) itf;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public String[] listFc() {
		return new String[] { "repository-itf" };
	}

	@Override
	public Object lookupFc(String name) throws NoSuchInterfaceException {
		if(name.equals("repository-itf")) {
			return repository;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public void unbindFc(String name) throws NoSuchInterfaceException {
		if(name.equals("repository-itf")) {
			repository = null;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}
}
