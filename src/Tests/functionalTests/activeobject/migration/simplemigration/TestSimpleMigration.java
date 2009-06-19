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
package functionalTests.activeobject.migration.simplemigration;

import junit.framework.Assert;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;

import functionalTests.GCMFunctionalTestDefaultNodes;


/**
 * Test AO simple migration
 */

public class TestSimpleMigration extends GCMFunctionalTestDefaultNodes {

    public TestSimpleMigration() {
        super(1, 1);
    }

    @org.junit.Test
    public void action() throws Exception {
        Node node = super.getANode();

        A a = (A) PAActiveObject.newActive(A.class.getName(), new Object[] { "toto" });
        a.moveTo(node);

        Assert.assertEquals("toto", a.getName());
        Assert.assertEquals(a.getNodeUrl(), node.getNodeInformation().getURL());
    }
}
