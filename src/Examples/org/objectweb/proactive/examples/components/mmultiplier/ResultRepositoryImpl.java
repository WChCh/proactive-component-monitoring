package org.objectweb.proactive.examples.components.mmultiplier;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;

public class ResultRepositoryImpl implements ResultRepository, BindingController {

	private int[][] matrix;
	private int nOfTasks;

	private ResultReceiver resultReceiver;

	
	@Override
	public void configure(int matrixSize, int nOfTasks) {
		this.matrix = new int[matrixSize][matrixSize];
		this.nOfTasks = nOfTasks;
	}

	@Override
	public void store(MTask task, Matrix result) {
		int[][] r = result.getMatrix();
		for(int i = 0; i < r.length; i++) {
			for(int j = 0; j < r.length; j++) {
				matrix[i + task.getX()][j + task.getY()] = r[i][j];
			}
		}
		nOfTasks--;
		if(nOfTasks <= 0) {
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
		return new String[] { "result-reciver-itf" };
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
