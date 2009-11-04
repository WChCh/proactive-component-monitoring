/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of 
 * 						   Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org
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
 * If needed, contact us to obtain a release under GPL Version 2. 
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.masterworker.basicordered;

import functionalTests.FunctionalTest;
import functionalTests.masterworker.A;
import static junit.framework.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.objectweb.proactive.extensions.masterworker.ProActiveMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.Master;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Test Master/Worker ordering
 */
public class TestBasicOrdered extends FunctionalTest {
    private URL descriptor = TestBasicOrdered.class
            .getResource("/functionalTests/masterworker/TestMasterWorker.xml");
    private Master<A, Integer> master;
    private List<A> tasks;
    public static final int NB_TASKS = 30;

    @org.junit.Test
    public void action() throws Exception {
        System.out.println(descriptor);
        tasks = new ArrayList<A>();
        for (int i = 0; i < NB_TASKS; i++) {
            A t = new A(i, (NB_TASKS - i) * 100, false);
            tasks.add(t);
        }

        master.solve(tasks);

        // We stress the ordering heavily by calling multiple wait methods
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(master.waitOneResult());
        int nbResults = master.countAvailableResults();
        ids.addAll(master.waitKResults(5));
        ids.add(master.waitOneResult());
        ids.addAll(master.waitAllResults());
        nbResults = master.countAvailableResults();
        assertTrue(nbResults == 0);
        assertTrue(master.isEmpty());

        // We check that the correct order is received
        Iterator<Integer> it = ids.iterator();
        int last = it.next();
        while (it.hasNext()) {
            int next = it.next();
            assertTrue("Results recieved in submission order", last < next);
            last = next;
        }
    }

    @Before
    public void initTest() throws Exception {
        master = new ProActiveMaster<A, Integer>();
        master.addResources(descriptor, super.vContract);
        master.setResultReceptionOrder(Master.SUBMISSION_ORDER);
    }

    @After
    public void endTest() throws Exception {
        master.terminate(true);
    }
}
