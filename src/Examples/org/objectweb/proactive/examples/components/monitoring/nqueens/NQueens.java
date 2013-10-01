package org.objectweb.proactive.examples.components.monitoring.nqueens;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.Type;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactory;

public class NQueens {

	private static boolean
		client = PAGCMTypeFactory.CLIENT,
		server = PAGCMTypeFactory.SERVER,
		mandatory = PAGCMTypeFactory.MANDATORY;
		
	private static String
		singleton = PAGCMTypeFactory.SINGLETON_CARDINALITY,
		multicast = PAGCMTypeFactory.MULTICAST_CARDINALITY;
	
	private PAGCMTypeFactory patf;
	private PAGenericFactory pagf;
	private boolean monitorable = true, setup = false, built = false, initiated = false;
	
	private Component master = null, solver = null;
	
	
	/**
	 * Sets the default building configuration
	 */
	public void setUp() {
		Component boot;
		try {
			boot = Utils.getBootstrapComponent();
			patf = (PAGCMTypeFactory) Utils.getPAGCMTypeFactory(boot);
	        pagf = (PAGenericFactory) Utils.getPAGenericFactory(boot);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
        setup = true;
	}
	
	/**
	 * Sets user building configurations
	 * @param patf
	 * @param pagf
	 */
	public void setUp(PAGCMTypeFactory patf, PAGenericFactory pagf) {
		this.patf = patf;
		this.pagf = pagf;
		setup = true;
	}

	/**
	 * Turns On/Off the monitors
	 */
	public void setMonitorable(boolean monitorable) {
		this.monitorable = monitorable;
	}

	/**
	 * Builds the complete NQueens component structure and returns a reference
	 * to the Master component.
	 * 
	 * @param monitored
	 * @return
	 * @throws InstantiationException 
	 * @throws IllegalLifeCycleException 
	 * @throws IllegalBindingException 
	 * @throws NoSuchInterfaceException 
	 * @throws IllegalContentException 
	 */
	public void build(int numberOfWorkers) throws Exception {

		if(!setup) setUp();

		master = createMaster();
		solver = createSolver();
		Component workerManager, adder, worker;
		workerManager = createWorkerManager();
		adder = createAdder();
			
		Utils.getPABindingController(master).bindFc("solver", solver.getFcInterface("solver"));
		
		Utils.getPAContentController(solver).addFcSubComponent(adder);
		Utils.getPABindingController(solver).bindFc("solver", adder.getFcInterface("solver"));

		Utils.getPAContentController(solver).addFcSubComponent(workerManager);
		Utils.getPABindingController(adder).bindFc("manager", workerManager.getFcInterface("manager"));
		
		for(int i = 1; i <= numberOfWorkers; i++) {
			worker = createWorker(i);
			Utils.getPAContentController(solver).addFcSubComponent(worker);
			Utils.getPABindingController(workerManager).bindFc("workers", worker.getFcInterface("worker"));
		}
		
		built = true;
	}
	
	public boolean init() {
		if(built && !initiated) {
			try {
				Utils.getPAGCMLifeCycleController(master).startFc();
				Utils.getPAGCMLifeCycleController(solver).startFc();
				if(monitorable) {
					Remmos.enableMonitoring(master);
					MonitorControl monControl = (MonitorControl) master.getFcInterface(Constants.MONITOR_CONTROLLER);
					monControl.startMonitoring();
				}
				return (initiated = true);
			} catch (IllegalLifeCycleException e) {
				e.printStackTrace();
			} catch (NoSuchInterfaceException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean stop() {
		if(built && initiated) {
			try {
				//Utils.getPAGCMLifeCycleController(master).stopFc();
				//Utils.getPAGCMLifeCycleController(solver).stopFc();
				Utils.getPAGCMLifeCycleController(master).stopFc();
				if(monitorable) {
					((MonitorControl) master.getFcInterface(Constants.MONITOR_CONTROLLER)).stopGCMMonitoring();
					Utils.getPAMembraneController(master).stopMembrane();
				}
				initiated = false;
				return true;
			} catch (IllegalLifeCycleException e) {
				e.printStackTrace();
			} catch (NoSuchInterfaceException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private Component createMaster() throws InstantiationException, IllegalContentException, IllegalLifeCycleException, NoSuchInterfaceException, IllegalBindingException {
		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
			(PAGCMInterfaceType) patf.createGCMItfType("master", Master.class.getName(), server, mandatory, singleton),
			(PAGCMInterfaceType) patf.createGCMItfType("solver", Solver.class.getName(), client, mandatory, singleton),
		};
		Component prim = primTemp("MasterPrimitive", MasterImpl.class.getName(), fItfType, monitorable);
		Component comp = compTemp("Master", fItfType, monitorable);
		Utils.getPAContentController(comp).addFcSubComponent(prim);
		Utils.getPABindingController(comp).bindFc("master", prim.getFcInterface("master"));
		Utils.getPABindingController(prim).bindFc("solver", comp.getFcInterface("solver"));
		return comp;
	}
	
	private Component createSolver() throws InstantiationException {
		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
			(PAGCMInterfaceType) patf.createGCMItfType("solver", Solver.class.getName(), server, mandatory, singleton)
		};
		return compTemp("Solver", fItfType, monitorable);
	}
	
	private Component createAdder() throws InstantiationException, IllegalContentException, IllegalLifeCycleException, NoSuchInterfaceException, IllegalBindingException {
		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
			(PAGCMInterfaceType) patf.createGCMItfType("solver", Solver.class.getName(), server, mandatory, singleton),
			(PAGCMInterfaceType) patf.createGCMItfType("manager", WorkerManager.class.getName(), client, mandatory, singleton),
		};
		Component prim = primTemp("AdderPrimitive", AdderImpl.class.getName(), fItfType, monitorable);
		Component comp = compTemp("Adder", fItfType, monitorable);
		Utils.getPAContentController(comp).addFcSubComponent(prim);
		Utils.getPABindingController(comp).bindFc("solver", prim.getFcInterface("solver"));
		Utils.getPABindingController(prim).bindFc("manager", comp.getFcInterface("manager"));
		return comp;
	}

	private Component createWorkerManager() throws InstantiationException, IllegalContentException, IllegalLifeCycleException, NoSuchInterfaceException, IllegalBindingException {
		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
			(PAGCMInterfaceType) patf.createGCMItfType("manager", WorkerManager.class.getName(), server, mandatory, singleton),
			(PAGCMInterfaceType) patf.createGCMItfType("workers", WorkerMulticast.class.getName(), client, mandatory, multicast),
		};
		Component prim = primTemp("WorkerManager", WorkerManagerImpl.class.getName(), fItfType, monitorable);
		return prim;
	}

	private Component createWorker(int i) throws InstantiationException, NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException, IllegalContentException {
		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
			(PAGCMInterfaceType) patf.createGCMItfType("worker", Worker.class.getName(), server, mandatory, singleton)
		};
		Component prim = primTemp("WorkerPrimitive" + i, WorkerImpl.class.getName(), fItfType, monitorable);
		Component comp = compTemp("Worker" + i, fItfType, monitorable);
		Utils.getPAContentController(comp).addFcSubComponent(prim);
		Utils.getPABindingController(comp).bindFc("worker", prim.getFcInterface("worker"));
		return comp;
	}

	private Component compTemp(String name, PAGCMInterfaceType[] fItfType, boolean isMonitorable) throws InstantiationException {
		PAGCMInterfaceType[] nfItfType = isMonitorable ?
				Remmos.createMonitorableNFType(patf, fItfType, Constants.COMPOSITE)
				: new PAGCMInterfaceType[] {};
		Component comp = pagf.newFcInstance(
				patf.createFcType(fItfType, nfItfType),
				new ControllerDescription(name, Constants.COMPOSITE),
				null);
		if(isMonitorable) setMonitorable(comp);
		return comp;
	}
	
	private Component primTemp(String name, String content, PAGCMInterfaceType[] fItfType, boolean isMonitorable) throws InstantiationException {
		PAGCMInterfaceType[] nfItfType = isMonitorable ?
				Remmos.createMonitorableNFType(patf, fItfType, Constants.PRIMITIVE)
				: new PAGCMInterfaceType[] {};
		Component comp = pagf.newFcInstance(
				patf.createFcType(fItfType, nfItfType),
				new ControllerDescription(name, Constants.PRIMITIVE),
				new ContentDescription(content));
		if(isMonitorable) setMonitorable(comp);
		return comp;
	}

	private static void setMonitorable(Component component) {
		try {
			Remmos.addObjectControllers((PAComponent) component);
			Utils.getPAMembraneController(component).startMembrane();
			Remmos.addMonitoring(component);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Component getMaster() {
		return master;
	}
}
