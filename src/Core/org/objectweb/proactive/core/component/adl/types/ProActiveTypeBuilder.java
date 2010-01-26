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
package org.objectweb.proactive.core.component.adl.types;

import org.objectweb.fractal.adl.types.FractalTypeBuilder;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactory;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;


/**
 * @author The ProActive Team
 */
public class ProActiveTypeBuilder extends FractalTypeBuilder {
    @Override
    public Object createInterfaceType(final String name, final String signature, final String role,
            final String contingency, final String cardinality, final Object context) throws Exception {
        // TODO : cache already created types ?
        boolean client = "client".equals(role);
        boolean optional = "optional".equals(contingency);

        String checkedCardinality = (cardinality == null) ? ProActiveTypeFactory.SINGLETON_CARDINALITY
                : cardinality;

        // TODO_M should use bootstrap type factory with extended createFcItfType method
        return ProActiveTypeFactoryImpl.instance().createFcItfType(name, signature, client, optional,
                checkedCardinality);
    }
}
