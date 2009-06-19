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
package org.objectweb.proactive.core.body.ft.servers.util;

import java.io.IOException;
import java.rmi.RemoteException;

import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.internalmsg.Killer;
import org.objectweb.proactive.core.body.ft.servers.FTServer;


/**
 * A job that kill a given active object
 * @author The ProActive Team
 * @since 2.2
 */
public class KillerJob implements ActiveQueueJob {
    private FTServer server;
    private UniversalBody toKill;
    private long toWait;

    /**
     *
     */
    public KillerJob(FTServer server, UniversalBody toKill, long toWait) {
        this.server = server;
        this.toKill = toKill;
        this.toWait = toWait;
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.util.ActiveQueueJob#doTheJob()
     */
    public void doTheJob() {
        try {
            // wait before killing
            Thread.sleep(toWait);
            toKill.receiveFTMessage(new Killer());
        } catch (IOException e) {
            //nothing
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            this.server.forceDetection();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }
}
