package org.objectweb.proactive.examples.components.monitoring.nqueens;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

public interface Master {

	/**
	 * Calculates and returns the nqueen problem for a chessboard of size
	 * boardSize using numberOfWorkers workers.
	 * 
	 * @param boardSize
	 * @param numberOfWorkers
	 * @return
	 */
	LongWrapper calculate(IntWrapper boardSize);
	
}
