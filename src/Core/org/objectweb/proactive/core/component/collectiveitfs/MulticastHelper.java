/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.collectiveitfs;

import java.util.List;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;
import org.objectweb.proactive.core.component.group.ProxyForComponentInterfaceGroup;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;
import org.objectweb.proactive.core.mop.MethodCall;


/**
 * Simple helper class
 *
 * @author The ProActive Team
 *
 */
public class MulticastHelper {

    /**
     * Transforms an invocation on a multicast interface into a list of
     * invocations which will be transferred to client interfaces. These
     * invocations are inferred from the annotations of the multicast interface
     * and the number of connected server interfaces.
     *
     * @param mc
     *            method call on the multicast interface
     * @param delegatee
     *            the group delegatee which is connected to interfaces server of
     *            this multicast interface
     * @return the reified invocations to be transferred to connected server
     *         interfaces
     * @throws ParameterDispatchException
     *             if there is an error in the dispatch of the parameters
     */
    public static List<MethodCall> generateMethodCallsForMulticastDelegatee(ProActiveComponent owner,
            MethodCall mc, ProxyForComponentInterfaceGroup<?> delegatee) throws ParameterDispatchException {
        try {
            return Fractive.getMulticastController(owner).generateMethodCallsForMulticastDelegatee(mc,
                    delegatee);
        } catch (NoSuchInterfaceException e) {
            throw new ParameterDispatchException("no multicast controller ", e);
        }
    }
}
