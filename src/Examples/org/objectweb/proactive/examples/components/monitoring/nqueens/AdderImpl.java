package org.objectweb.proactive.examples.components.monitoring.nqueens;

import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.control.PABindingController;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

public class AdderImpl implements Solver, PABindingController {

	private WorkerManager manager;

	@Override
	public LongWrapper solve(IntWrapper boardSize) {
		List<LongWrapper> solutions = manager.startCalculus(boardSize);
		long total = 0;
		for(LongWrapper sol : solutions)
			total += sol.getLongValue();

		return new LongWrapper(total);
	}

	@Override
	public void bindFc(String itfName, Object itf)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		if (itfName.equals("manager"))
			manager = (WorkerManager) itf;
		else throw new NoSuchInterfaceException(itfName);
	}

	public String[] listFc() {
		return new String[] { "manager" };
	}

	public Object lookupFc(String itfName) throws NoSuchInterfaceException {
		if (itfName.equals("manager"))
			return manager;
		else throw new NoSuchInterfaceException(itfName);
	}

	public void unbindFc(String itfName) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		if (itfName.equals("manager"))
			manager = null;
		else throw new NoSuchInterfaceException(itfName);
	}

	public Boolean isBound() {
		return manager != null;
	}

	public Boolean isBoundTo(Component component) {
		return null;
	}

}
