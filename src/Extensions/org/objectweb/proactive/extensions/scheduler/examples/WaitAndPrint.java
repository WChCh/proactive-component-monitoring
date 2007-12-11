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
package org.objectweb.proactive.extensions.scheduler.examples;

import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.proactive.core.util.URIBuilder;
import org.objectweb.proactive.extensions.scheduler.common.task.TaskResult;
import org.objectweb.proactive.extensions.scheduler.common.task.executable.JavaExecutable;


public class WaitAndPrint extends JavaExecutable {

    /**  */
    private static final long serialVersionUID = 2518295052900092724L;
    public int sleepTime;
    public int number;

    @Override
    public Object execute(TaskResult... results) throws Throwable {
        String message;

        try {
            System.err.println("Task " + number + " : Test STDERR");
            System.out.println("Task " + number + " : Test STDOUT");

            //            for (TaskResult tRes : results) {
            //                if (tRes.hadException()) {
            //                    System.out.println("\t " + tRes.getTaskId() + " : " +
            //                        tRes.getException().getMessage());
            //                } else {
            //                    System.out.println("\t " + tRes.getTaskId() + " : " +
            //                        tRes.value());
            //                }
            //            }
            //
            //            message = URIBuilder.getLocalAddress().toString();
            //            //	            if (sleepTime == 5){
            //            //	            	Thread.sleep(sleepTime * 100);
            //            //	            	System.exit(1);
            //            //	            } else {
            Thread.sleep(sleepTime * 1000);

            //	            }
        } catch (Exception e) {
            message = "crashed";
            e.printStackTrace();
        }

        //        System.out.println("Terminaison de la tache numero " + number);

        //        return ("No." + this.number + " hi from " + message + "\t slept for " +
        //        sleepTime + "Seconds");
        return "Returned value";
    }

    @Override
    public void init(Map<String, Object> args) {
        sleepTime = Integer.parseInt((String) args.get("sleepTime"));
        number = Integer.parseInt((String) args.get("number"));

        //        for (Entry<String, Object> e : args.entrySet()) {
        //            System.out.println("INIT(" + number + ") : " + e.getKey() + "=" +
        //                e.getValue());
        //        }
        //
        //        for (String key : args.keySet()) {
        //            System.out.println("INIT(" + number + ") : " + key + "=" +
        //                args.get(key));
        //        }
    }
}