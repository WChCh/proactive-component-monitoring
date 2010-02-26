package org.objectweb.proactive.examples.documentation.timit;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAException;
import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.api.PASPMD;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extensions.timitspmd.util.BenchmarkStatistics;
import org.objectweb.proactive.extensions.timitspmd.util.Startable;
import org.objectweb.proactive.extensions.timitspmd.util.TimItManager;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


// @snippet-start TimIt_Example_Class
public class Example implements Startable {

    private GCMApplication pad;
    private Worker workers;

    /** TimIt needs a noarg constructor (can be implicit) **/
    public Example() {
    }

    /** The main method is not used by TimIt **/
    public static void main(String[] args) {
        new Example().start(args);
    }

    /** Invoked by TimIt to start your application **/
    public void start(String[] args) {

        // Common stuff about ProActive deployment
        // ...
        // Gets an array of node called 'arrayNode'

        //@snippet-break TimIt_Example_Class
        try {
            this.pad = PAGCMDeployment.loadApplicationDescriptor(new File(args[0]));
        } catch (ProActiveException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        int n = Integer.valueOf(args[1]).intValue();

        this.pad.startDeployment();
        GCMVirtualNode vnode = this.pad.getVirtualNode("Workers");

        vnode.waitReady();

        List<Node> nodes = vnode.getCurrentNodes();
        System.out.println(nodes.size() + " nodes found, " + n + " wanted. ");

        // Creates empty parameters
        Object[] param = new Object[] {};
        Object[][] params = new Object[n][];
        for (int i = 0; i < n; i++) {
            params[i] = param;
        }

        Node[] nodeArray = (Node[]) nodes.toArray(new Node[] {});
        //@snippet-resume TimIt_Example_Class

        // Creation of the Timed object(s)
        // It can be by example :
        //   - a classic java object
        //   - an active object
        //   - a group of objects
        try {
            this.workers = (Worker) PASPMD.newSPMDGroup(Worker.class.getName(), params, nodeArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // You have to create an instance of TimItManager and
        // give it to the Timed objects
        TimItManager tManager = TimItManager.getInstance();

        tManager.setTimedObjects(this.workers);

        // Timed objects start their jobs
        this.workers.start();

        // At the end of your application, you must invoke
        // the getBenchmarkStatistics to retrieve the results
        // from the Timed objects
        BenchmarkStatistics bStats = tManager.getBenchmarkStatistics();

        // Then, you can modify or print out the results
        System.out.println(bStats);
    }

    //@snippet-break TimIt_Example_Class
    public void kill() {
        Group<Worker> gWorkers = PAGroup.getGroup(workers);
        Iterator<Worker> it = gWorkers.iterator();

        while (it.hasNext()) {
            PAActiveObject.terminateActiveObject(it.next(), true);
        }
        PAException.waitForPotentialException();

        this.pad.kill();
    }

    public void masterKill() {
    }
    //@snippet-resume TimIt_Example_Class
}
//@snippet-end TimIt_Example_Class