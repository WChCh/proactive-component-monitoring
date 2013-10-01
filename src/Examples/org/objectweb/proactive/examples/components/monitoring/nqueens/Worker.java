package org.objectweb.proactive.examples.components.monitoring.nqueens;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

public interface Worker {

	/**
	 * Sets the size of the board.
	 * 
	 * @param boardSize
	 */
	void setUp(IntWrapper boardSize);
	
	/** 
	 * finds the number of solutions by searching on the given
	 * section of the board.
	 * 
	 * @param boardSection
	 * @return
	 */
	LongWrapper solve(IntWrapper boardSection);

}
