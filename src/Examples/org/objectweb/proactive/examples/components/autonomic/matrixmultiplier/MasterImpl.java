package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;


public class MasterImpl implements MatrixMultiplier, BindingController {

	private WorkerMulticast workers;
	private Repository repository;

	@Override
	public void multiply(int[][] matrix, int base) {
		repository.configure(matrix.length, base);
		workers.configure(matrix, base);

		List<Position> list = 
				new ArrayList<Position>();
		
		int k = matrix.length/base;
		for(int i = 0; i < k; i++) {
			for(int j = 0; j < k; j++) {
				list.add(new Position(i*base, j*base));
			}
		}
		
		workers.multiply(list);
	}

	@Override
	public void bindFc(String name, Object itf)
			throws NoSuchInterfaceException {
		if(name.equals("worker-multicast-itf")) {
			workers = (WorkerMulticast) itf;
		} else if(name.equals("repository-itf")) {
			repository = (Repository) itf;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public String[] listFc() {
		return new String[] { "worker-multicast-itf", "repository-itf" };
	}

	@Override
	public Object lookupFc(String name) throws NoSuchInterfaceException {
		if(name.equals("worker-multicast-itf")) {
			return workers;
		} else if(name.equals("repository-itf")) {
			return repository;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

	@Override
	public void unbindFc(String name) throws NoSuchInterfaceException {
		if(name.equals("worker-multicast-itf")) {
			workers = null;
		} else if(name.equals("repository-itf")) {
			repository = null;
		} else {
			throw new NoSuchInterfaceException(name);
		}
	}

}
