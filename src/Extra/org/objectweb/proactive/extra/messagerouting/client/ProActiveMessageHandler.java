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
package org.objectweb.proactive.extra.messagerouting.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.messagerouting.remoteobject.message.MessageRoutingMessage;
import org.objectweb.proactive.extra.messagerouting.remoteobject.util.PamrMarshaller;


/** Executes a ProActive {@link Request} received and send the response.
 *
 * @since ProActive 4.1.0
 */
public class ProActiveMessageHandler implements MessageHandler {

    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT);

    /** {@link Request} are handled by a threadpool */
    final private ExecutorService tpe;

    /** Local agent */
    private Agent agent;

    public ProActiveMessageHandler(Agent agent) {
        this.agent = agent;

        /* DO NOT USE A FIXED THREAD POOL
         * 
         * Each time a message arrives, it is handled by a task submitted to 
         * this executor service. Each task can a perform remote calls. If 
         * the number of workers is fixed it can lead to deadlock.
         * 
         * Reentrant calls is the most obvious case of deadlock. But the same 
         * issue can occur with remote calls. 
         */
        tpe = Executors.newCachedThreadPool();

    }

    public void pushMessage(DataRequestMessage message) {
        if (logger.isTraceEnabled()) {
            logger.trace("pushing message " + message + " into the executor queue");
        }

        ProActiveMessageProcessor pmp = new ProActiveMessageProcessor(message, agent);
        tpe.submit(pmp);
    }

    /** Process one ProActive {@link Request} */
    private class ProActiveMessageProcessor implements Runnable {
        /** the request*/
        private final DataRequestMessage _toProcess;
        /** the local agent*/
        private final Agent agent;
        /** serialization*/
        private final PamrMarshaller marshaller;

        public ProActiveMessageProcessor(DataRequestMessage msg, Agent agent) {
            this._toProcess = msg;
            this.agent = agent;
            // get the runtime URL
            // if the local Agent has received a DataRequestMessage,
            // means that a ProActiveRuntime exists on this machine
            String runtimeUrl = ProActiveRuntimeImpl.getProActiveRuntime().getURL();
            this.marshaller = new PamrMarshaller(runtimeUrl);
        }

        public void run() {
            ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

                // Handle the message
                MessageRoutingMessage message = (MessageRoutingMessage) this.marshaller
                        .unmarshallObject(_toProcess.getData());

                if (logger.isTraceEnabled()) {
                    logger.trace("Processing message: " + message);
                }
                Object result = message.processMessage();

                byte[] resultBytes = this.marshaller.marshallObject(result);
                agent.sendReply(_toProcess, resultBytes);
            } catch (Exception e) {
                logger.warn("ProActive Message failed to serve a message", e);
                // TODO: Send an ERR_ ?
            } finally {
                Thread.currentThread().setContextClassLoader(savedClassLoader);
            }
        }
    }
}
