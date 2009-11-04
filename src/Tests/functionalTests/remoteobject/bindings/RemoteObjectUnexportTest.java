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
package functionalTests.remoteobject.bindings;

import java.net.URI;

import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.RemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;

import functionalTests.FunctionalTest;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;


/**
 * Remote objects : unexport test case
 */
public class RemoteObjectUnexportTest extends FunctionalTest {

    @org.junit.Test
    public void unexport() throws Exception {
        // get an object
        ProActiveRuntime p = ProActiveRuntimeImpl.getProActiveRuntime();

        // create a remote object exposer for this object
        RemoteObjectExposer<ProActiveRuntime> roe = PARemoteObject.newRemoteObject(ProActiveRuntime.class
                .getName(), p);

        // generate an uri where to rebind the runtime
        URI uri = RemoteObjectHelper.generateUrl("myruntime");

        // let's bind the object on the given endpoint
        PARemoteObject.bind(roe, uri);

        // looking for the remote object
        RemoteObject<ProActiveRuntime> ro = RemoteObjectHelper.lookup(uri);

        assertNotNull(ro);

        // unexport the object located at uri
        roe.unexport(uri);

        // looking for the remote object should fail
        try {
            RemoteObjectHelper.lookup(uri);
        } catch (ProActiveException e) {
            // this is the expected exception, let's continue
        } catch (Exception e) {
            fail("the exception's type is not the one expected, caught " + e.getClass().getName() +
                ",should be " + ProActiveException.class.getName());
        }
    }

}
