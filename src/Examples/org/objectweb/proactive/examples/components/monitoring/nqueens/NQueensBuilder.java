package org.objectweb.proactive.examples.components.monitoring.nqueens;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
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
import org.objectweb.proactive.core.node.Node;


/**
 * A Class to build all the components needed for the NQueens Problems' example.
 * 
 * @author matias
 */
public class NQueensBuilder {

	private static boolean
		CLIENT = PAGCMTypeFactory.CLIENT,
		SERVER = PAGCMTypeFactory.SERVER,
		MANDATORY = PAGCMTypeFactory.MANDATORY;
		
	private static String
		SINGLETON = PAGCMTypeFactory.SINGLETON_CARDINALITY,
		MULTICAST = PAGCMTypeFactory.MULTICAST_CARDINALITY;
	
	private PAGCMTypeFactory patf;
	private PAGenericFactory pagf;
	private boolean monitorable = true, built = false, initiated = false;
	
	private Component master = null, solver = null;


	/**
	 * Turns On/Off the monitors
	 */
	public NQueensBuilder(boolean monitorable) {
		try {
			Component boot = Utils.getBootstrapComponent();
			patf = (PAGCMTypeFactory) Utils.getPAGCMTypeFactory(boot);
	        pagf = (PAGenericFactory) Utils.getPAGenericFactory(boot);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		
		this.monitorable = monitorable;
	}

	/**
	 * Builds the complete NQueens component structure and returns a reference
	 * to the Master component.
	 * 
	 * @param monitored
	 * @return
	 */
	public void build(int numberOfWorkers) throws Exception {
		master = createMaster(null);
		solver = createSolver(null);
		Component workerManager, adder, worker;
		workerManager = createWorkerManager(null);
		adder = createAdder(null);
			
		Utils.getPABindingController(master).bindFc("solver", solver.getFcInterface("solver"));
		
		Utils.getPAContentController(solver).addFcSubComponent(adder);
		Utils.getPABindingController(solver).bindFc("solver", adder.getFcInterface("solver"));

		Utils.getPAContentController(solver).addFcSubComponent(workerManager);
		Utils.getPABindingController(adder).bindFc("manager", workerManager.getFcInterface("manager"));
		
		for(int i = 1; i <= numberOfWorkers; i++) {
			worker = createWorker(i, null);
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

	/**
	 * MASTER
	 * @param node
	 * @return
	 */
	private Component createMaster(Node node) throws InstantiationException,
			IllegalContentException, IllegalLifeCycleException,
			NoSuchInterfaceException, IllegalBindingException {
		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
			(PAGCMInterfaceType) patf.createGCMItfType("master", Master.class.getName(), SERVER, MANDATORY, SINGLETON),
			(PAGCMInterfaceType) patf.createGCMItfType("solver", Solver.class.getName(), CLIENT, MANDATORY, SINGLETON),
		};
		Component prim = primTemp("MasterPrimitive", MasterImpl.class.getName(), fItfType, node, monitorable);
		Component comp = compTemp("Master", fItfType, node, monitorable);
		Utils.getPAContentController(comp).addFcSubComponent(prim);
		Utils.getPABindingController(comp).bindFc("master", prim.getFcInterface("master"));
		Utils.getPABindingController(prim).bindFc("solver", comp.getFcInterface("solver"));
		return comp;
	}
	
	/**
	 * SOLVER
	 * @return
	 */
	private Component createSolver(Node node) throws InstantiationException {
		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
			(PAGCMInterfaceType) patf.createGCMItfType("solver", Solver.class.getName(), SERVER, MANDATORY, SINGLETON)
		};
		return compTemp("Solver", fItfType, node, monitorable);
	}
	
	/**
	 * ADDER
	 * @param node
	 * @return
	 */
	private Component createAdder(Node node) throws InstantiationException,
			IllegalContentException, IllegalLifeCycleException,
			NoSuchInterfaceException, IllegalBindingException {
		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
			(PAGCMInterfaceType) patf.createGCMItfType("solver", Solver.class.getName(), SERVER, MANDATORY, SINGLETON),
			(PAGCMInterfaceType) patf.createGCMItfType("manager", WorkerManager.class.getName(), CLIENT, MANDATORY, SINGLETON),
		};
		Component prim = primTemp("AdderPrimitive", AdderImpl.class.getName(), fItfType, node, monitorable);
		Component comp = compTemp("Adder", fItfType, node, monitorable);
		Utils.getPAContentController(comp).addFcSubComponent(prim);
		Utils.getPABindingController(comp).bindFc("solver", prim.getFcInterface("solver"));
		Utils.getPABindingController(prim).bindFc("manager", comp.getFcInterface("manager"));
		return comp;
	}

	/**
	 * WORKMANAGER
	 * @param node
	 * @return
	 */
	private Component createWorkerManager(Node node) throws InstantiationException,
			IllegalContentException, IllegalLifeCycleException,
			NoSuchInterfaceException, IllegalBindingException {

		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
			(PAGCMInterfaceType) patf.createGCMItfType("manager", WorkerManager.class.getName(), SERVER, MANDATORY, SINGLETON),
			(PAGCMInterfaceType) patf.createGCMItfType("workers", WorkerMulticast.class.getName(), CLIENT, MANDATORY, MULTICAST),
		};
		Component prim = primTemp("WorkerManager", WorkerManagerImpl.class.getName(), fItfType, node, monitorable);
		return prim;
	}

	/**
	 * WORKER
	 * @param i
	 * @param node
	 * @return
	 */
	private Component createWorker(int i, Node node) throws InstantiationException,
			NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException, IllegalContentException {
	
		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
			(PAGCMInterfaceType) patf.createGCMItfType("worker", Worker.class.getName(), SERVER, MANDATORY, SINGLETON)
		};
		Component prim = primTemp("WorkerPrimitive" + i, WorkerImpl.class.getName(), fItfType, node, monitorable);
		Component comp = compTemp("Worker" + i, fItfType, node, monitorable);
		Utils.getPAContentController(comp).addFcSubComponent(prim);
		Utils.getPABindingController(comp).bindFc("worker", prim.getFcInterface("worker"));
		return comp;
	}

	/**
	 * Template para componentes compuestos
	 * 
	 * @param name				Nombre del componente
	 * @param fItfType			Arreglo de interfaces funcionales
	 * @param node				Nodo para ser desplayado
	 * @param isMonitorable		Si True, entonces se hara monitoreable
	 * @return					Un componente compuesto
	 */
	private Component compTemp(String name, PAGCMInterfaceType[] fItfType,
			Node node, boolean isMonitorable) throws InstantiationException {

		PAGCMInterfaceType[] nfItfType;
		if (isMonitorable) {
			nfItfType = Remmos.createMonitorableNFType(patf, fItfType, Constants.COMPOSITE);
		} else {
			nfItfType = new PAGCMInterfaceType[] {};
		}
		Component comp = pagf.newFcInstance(
			patf.createFcType(fItfType, nfItfType),
			new ControllerDescription(name, Constants.COMPOSITE),
			null,
			node
		);
		
		if(isMonitorable) {
			setMonitorable(comp);
		}
		return comp;
	}
	
	/**
	 * Template para componentes primitivos
	 * 
	 * @param name				Nombre del componente
	 * @param content			Nombre de la clase que la implementa
	 * @param fItfType			Arreglo de interfaces funcionales
	 * @param node				Nodo para ser desplayado
	 * @param isMonitorable		Si True, entonces se hara monitoreable
	 * @return					Un componente Primitivo
	 */
	private Component primTemp(String name, String content,
			PAGCMInterfaceType[] fItfType, Node node, boolean isMonitorable)
					throws InstantiationException {
	
		PAGCMInterfaceType[] nfItfType;
		if(isMonitorable) {
			nfItfType = Remmos.createMonitorableNFType(patf, fItfType, Constants.PRIMITIVE);
		} else {
			nfItfType = new PAGCMInterfaceType[] {};
		}
		Component comp = pagf.newFcInstance(
			patf.createFcType(fItfType, nfItfType),
			new ControllerDescription(name, Constants.PRIMITIVE),
			new ContentDescription(content),
			node
		);

		if(isMonitorable) {
			setMonitorable(comp);
		}
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
