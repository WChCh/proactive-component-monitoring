package org.objectweb.proactive.examples.components.monitoring.nqueens;

import java.util.List;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

public interface WorkerManager {

	public List<LongWrapper> startCalculus(IntWrapper boardSize);
}
