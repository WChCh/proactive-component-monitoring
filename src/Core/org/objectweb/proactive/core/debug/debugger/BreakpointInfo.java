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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.debug.debugger;

import java.io.Serializable;

import org.objectweb.proactive.core.body.request.Request;


public class BreakpointInfo implements Serializable {

    private static final long serialVersionUID = -696438917043366141L;

    private BreakpointType type;

    private Thread thread;

    private Request request;

    public BreakpointInfo(BreakpointType type, Thread thread, Request request) {
        this.type = type;
        this.thread = thread;
        this.request = request;
    }

    public BreakpointType getType() {
        return type;
    }

    public Thread getThread() {
        return thread;
    }

    public Request getRequest() {
        return request;
    }

    public long getBreakpointId() {
        return thread.getId();
    }

}
