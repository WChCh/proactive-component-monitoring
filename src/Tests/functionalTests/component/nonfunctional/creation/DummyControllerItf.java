/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
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
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.component.nonfunctional.creation;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;


/**
 *
 * @author The ProActive Team
 * The server interface for the dummyController non functional component
 *
 */
public interface DummyControllerItf {

    /**
     * This is a dummy method that can be called on the server interface of the DummyController component
     * @return a dummy value
     */
    public String dummyMethodWithResult();

    /**
     *  A void dummy method
     * @param message The message you want the controller to display
     */
    public void dummyVoidMethod(String message);

    public IntWrapper result(IntWrapper param);
}
