package org.objectweb.proactive.examples.components.monitoring.nqueens;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

public class MasterImpl implements Master, BindingController {

	private Solver solver;
	
	/**
	 * Calculates and returns the nqueen problem for a chessboard of size
	 * boardSize using numberOfWorkers workers.
	 * 
	 * @param boardSize
	 * @param numberOfWorkers
	 * @return
	 */
	public LongWrapper calculate(IntWrapper boardSize) {
		return solver.solve(boardSize);
	}

	@Override
	public void bindFc(String itfName, Object itf)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		if (itfName.equals("solver"))
			solver = (Solver) itf;
		else throw new NoSuchInterfaceException(itfName);
	}

	public String[] listFc() {
		return new String[] { "solver" };
	}

	public Object lookupFc(String itfName) throws NoSuchInterfaceException {
		if (itfName.equals("solver"))
			return solver;
		else throw new NoSuchInterfaceException(itfName);
	}

	public void unbindFc(String itfName) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		if (itfName.equals("solver"))
			solver = null;
		else throw new NoSuchInterfaceException(itfName);
	}
}
