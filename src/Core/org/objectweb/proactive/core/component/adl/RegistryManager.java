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
package org.objectweb.proactive.core.component.adl;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.api.Component;


/**
 * This interface defines facilities for using a shared static registry storing component
 * instances according to their name.
 *
 * @author The ProActive Team
 */
public interface RegistryManager {

    /**
     * Adds a component instance.
     * (The name is retreived automatically from the NameController)
     *
     * @param component the instance of the component
     * @throws ADLException
     */
    public void addComponent(Component component) throws ADLException;

    /**
     * Retreives an instance of a  component according to the name (from its NameController controller)
     * @param name the name of the instance of the component
     * @return the selected component
     */
    public Component getComponent(String name);

    /**
     * Empties the registry
     *
     */
    public void clear();
}
