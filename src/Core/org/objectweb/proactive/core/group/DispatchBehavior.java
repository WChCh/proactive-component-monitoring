/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.group;

import java.util.List;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.mop.MethodCall;


/**
 * Specifies the mapping between tasks and workers.
 * 
 * @author The ProActive Team
 *
 */
@PublicAPI
public interface DispatchBehavior {

    /**
     * Maps a list of method calls (corresponding to tasks to be executed on workers), to
     * indexes of the workers (no information is available from workers).
     * 
     * @param originalMethodCall the reified method call invoked on the group
     * @param generatedMethodCalls the reified method calls generated according to the partitioning scheme, and
     * that should be invoked on workers
     * @param nbWorkers the number of available workers
     * @return the mapping tasks --> worker index, for the given list of tasks
     */
    public List<Integer> getTaskIndexes(MethodCall originalMethodCall,
            final List<MethodCall> generatedMethodCalls, int nbWorkers);

}