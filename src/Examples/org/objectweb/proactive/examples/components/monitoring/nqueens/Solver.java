package org.objectweb.proactive.examples.components.monitoring.nqueens;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

public interface Solver {

	/** 
	 * finds and returns the nqueen problem solution for a chessboard of size
	 * boardSize.
	 * 
	 * @param boardSize
	 * @return
	 */
	LongWrapper solve(IntWrapper boardSize);

}
