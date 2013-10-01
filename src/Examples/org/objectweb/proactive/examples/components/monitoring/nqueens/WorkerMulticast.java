package org.objectweb.proactive.examples.components.monitoring.nqueens;

import java.util.List;

import org.objectweb.proactive.core.component.type.annotations.multicast.MethodDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMode;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

public interface WorkerMulticast {

	/**
	 * Sets the size of the board.
	 * 
	 * @param boardSize
	 */
	@MethodDispatchMetadata(mode = @ParamDispatchMetadata(mode = ParamDispatchMode.BROADCAST))
	void setUp(IntWrapper boardSize);
	
	/** 
	 * finds the number of solutions by searching on the given
	 * section of the board.
	 * 
	 * @param boardSection
	 * @return
	 */
	@MethodDispatchMetadata(mode = @ParamDispatchMetadata(mode = ParamDispatchMode.ROUND_ROBIN))
	List<LongWrapper> solve(List<IntWrapper> boardSection);
}
