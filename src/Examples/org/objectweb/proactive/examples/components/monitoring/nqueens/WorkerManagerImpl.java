package org.objectweb.proactive.examples.components.monitoring.nqueens;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.control.PABindingController;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

public class WorkerManagerImpl implements WorkerManager, PABindingController {

	private WorkerMulticast workers;
	
	/** 
	 * finds and returns the nqueen problem solution for a chessboard of size
	 * boardSize.
	 * 
	 * @param boardSize
	 * @return
	 */
	public List<LongWrapper> startCalculus(IntWrapper boardSize) {
		
		List<IntWrapper> boardSections = new ArrayList<IntWrapper>();
		for(int i = 1; i <= boardSize.getIntValue(); i++)
			boardSections.add(new IntWrapper(i));
		
		workers.setUp(boardSize);
		List<LongWrapper> solutions = new ArrayList<LongWrapper>();
		for(LongWrapper result : workers.solve(boardSections))
			solutions.add(result);
		
		return solutions;
	}


	public void bindFc(String itfName, Object itf)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		if (itfName.equals("workers"))
			workers = (WorkerMulticast) itf;
		else throw new NoSuchInterfaceException(itfName);
		
	}

	public String[] listFc() {
		return new String[] { "workers" };
	}

	public Object lookupFc(String itfName) throws NoSuchInterfaceException {
		if (itfName.equals("workers"))
			return workers;
		else throw new NoSuchInterfaceException(itfName);
	}

	public void unbindFc(String itfName) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		if (itfName.equals("workers"))
			workers = null;
		else throw new NoSuchInterfaceException(itfName);
	}

	public Boolean isBound() {
		return workers != null;
	}

	public Boolean isBoundTo(Component component) {
		return null;
	}

}
