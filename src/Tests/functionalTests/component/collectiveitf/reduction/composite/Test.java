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
package functionalTests.component.collectiveitf.reduction.composite;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;

import functionalTests.ComponentTest;


public class Test extends ComponentTest {
    public Test() {
        super("Multicast reduction mixing composite and primitive components",
                "Multicast reduction mixing composite and primitive components");
    }

    /*
     * @see testsuite.test.FunctionalTest#action()
     */
    @org.junit.Test
    public void action() throws Exception {
        try {
            Factory f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();

            Map<String, Object> context = new HashMap<String, Object>();

            Component root = (Component) f.newComponent(
                    "functionalTests.component.collectiveitf.reduction.composite.adl.testcase", context);
            Fractal.getLifeCycleController(root).startFc();
            Reduction reductionItf = ((Reduction) root.getFcInterface("mcast"));

            IntWrapper rval = reductionItf.doIt();
            Assert.assertEquals(new IntWrapper(123), rval);

            rval = reductionItf.doItInt(new IntWrapper(321));
            Assert.assertEquals(new IntWrapper(123), rval);

            reductionItf.voidDoIt();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}