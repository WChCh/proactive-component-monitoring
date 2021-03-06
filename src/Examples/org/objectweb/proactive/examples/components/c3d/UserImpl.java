/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.examples.components.c3d;

import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.examples.c3d.C3DUser;
import org.objectweb.proactive.examples.c3d.User;
import org.objectweb.proactive.examples.c3d.gui.NameAndHostDialog;


/** The component container for a User. */

public class UserImpl extends C3DUser implements BindingController, User {

    /** Mandatory ProActive empty no-arg constructor */
    public UserImpl() {
    }

    /** Tells what are the operations to perform before starting the activity of the AO.
     * Registering the component and some empty fields filling in is done here.
     * We also state that if migration asked, procedure  is : saveData, migrate, rebuild */
    @Override
    public void initActivity(Body body) {
        // Maybe 'binding to dispatcher' has been done before
        if (this.c3ddispatcher == null) {
            logger.error("User component could not find a dispatcher. Performing lookup");

            // ask user through Dialog for userName & host
            NameAndHostDialog userAndHostNameDialog = new NameAndHostDialogForComponent();
            this.c3ddispatcher = userAndHostNameDialog.getValidatedDispatcher();
            setUserName(userAndHostNameDialog.getValidatedUserName());

            if (this.c3ddispatcher == null) {
                logger.error("Could not find a dispatcher. Closing.");
                System.exit(-1);
            }
        }

        if (getUserName() == null) { // just in case it was not yet set.
            setUserName("Bob");
        }

        // Register the User in the Registry.
        try {
            Fractive.registerByName(Fractive.getComponentRepresentativeOnThis(), "User");
        } catch (ProActiveException e) {
            logger.error("Registering 'User' for future lookup failed");
            e.printStackTrace();
        }

        super.initActivity(body);
    }

    /** returns all the possible bindings, here just user2dispatcher .
     * @return the only posible binding "user2dispatcher" */
    public String[] listFc() {
        return new String[] { "user2dispatcher" };
    }

    /** Returns the dispatcher currently bound to the client interface of this component
     * @return null if no component bound, otherwise returns the bound component */
    public Object lookupFc(final String interfaceName) {
        if (interfaceName.equals("user2dispatcher")) {
            return c3ddispatcher;
        }

        return null;
    }

    //@snippet-start C3D_Components_1
    /** Binds to this UserImpl component the dispatcher which should be used. */
    public void bindFc(final String interfaceName, final Object serverInterface) {
        if (interfaceName.equals("user2dispatcher")) {
            c3ddispatcher = (org.objectweb.proactive.examples.c3d.Dispatcher) serverInterface;

            // Registering back to the dispatcher is done in the go() method
        }
    }

    //@snippet-end C3D_Components_1

    /** Detaches the user from its dispatcher.
     * Notice how it has not been called in terminate() ?
     * This is due to the fact that unbinding only sets a reference to null,
     * and does no cleaning up. */
    public void unbindFc(final String interfaceName) {
        if (interfaceName.equals("user2dispatcher")) {
            c3ddispatcher = null;
        }
    }
}
