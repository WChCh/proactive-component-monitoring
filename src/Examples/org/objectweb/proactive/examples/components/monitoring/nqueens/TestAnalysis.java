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
import org.objectweb.proactive.core.component.componentcontroller.analysis.AnalysisController;
import org.objectweb.proactive.core.component.componentcontroller.analysis.Rule;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorController;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactory;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;
import org.objectweb.proactive.examples.components.monitoring.nqueens.metrics.NumberOfTasksMetric;
import org.objectweb.proactive.examples.components.monitoring.nqueens.metrics.WorkerTasksCounterMetric;
import org.objectweb.proactive.examples.components.monitoring.nqueens.rules.Finished;
import org.objectweb.proactive.examples.components.monitoring.nqueens.rules.HalfFinished;

public class TestAnalysis {

	private static boolean
	CLIENT = PAGCMTypeFactory.CLIENT,
	SERVER = PAGCMTypeFactory.SERVER,
	MANDATORY = PAGCMTypeFactory.MANDATORY;

	private static String
	SINGLETON = PAGCMTypeFactory.SINGLETON_CARDINALITY,
	MULTICAST = PAGCMTypeFactory.MULTICAST_CARDINALITY;

	PAGCMTypeFactory patf;
	PAGenericFactory pagf;
	boolean monitorable;

	static int WORKERS = 4;
	static int BOARD_SIZE = 14;
	static boolean MONITORABLE = true;

	TestAnalysis(boolean mon) throws NoSuchInterfaceException, InstantiationException {
		Component boot = Utils.getBootstrapComponent();
		patf = (PAGCMTypeFactory) Utils.getPAGCMTypeFactory(boot);
		pagf = (PAGenericFactory) Utils.getPAGenericFactory(boot);
		monitorable = mon;
	}

	private Component createMaster(Node node) throws Exception {
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

	private Component createSolver(Node node) throws Exception {
		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType("solver", Solver.class.getName(), SERVER, MANDATORY, SINGLETON)
		};
		return compTemp("Solver", fItfType, node, monitorable);
	}


	private Component createAdder(Node node) throws Exception {
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


	private Component createWorkerManager(Node node) throws Exception {

		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType("manager", WorkerManager.class.getName(), SERVER, MANDATORY, SINGLETON),
				(PAGCMInterfaceType) patf.createGCMItfType("workers", WorkerMulticast.class.getName(), CLIENT, MANDATORY, MULTICAST),
		};
		Component prim = primTemp("WorkerManager", WorkerManagerImpl.class.getName(), fItfType, node, monitorable);
		return prim;
	}


	private Component createWorker(int i, Node node) throws Exception {

		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType("worker", Worker.class.getName(), SERVER, MANDATORY, SINGLETON)
		};
		Component prim = primTemp("WorkerPrimitive" + i, WorkerImpl.class.getName(), fItfType, node, monitorable);
		Component comp = compTemp("Worker" + i, fItfType, node, monitorable);
		Utils.getPAContentController(comp).addFcSubComponent(prim);
		Utils.getPABindingController(comp).bindFc("worker", prim.getFcInterface("worker"));
		return comp;
	}

	private Component compTemp(String name, PAGCMInterfaceType[] fItfType,
			Node node, boolean isMonitorable) throws Exception {

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

	
	private Component primTemp(String name, String content,
			PAGCMInterfaceType[] fItfType, Node node, boolean isMonitorable)
					throws Exception {

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
	
	public static void main(String[] args) throws Exception {
		
		TestAnalysis test = new TestAnalysis(MONITORABLE);
		Component master = test.createMaster(null),
			solver = test.createSolver(null);
		Component workerManager, adder, worker;
		workerManager = test.createWorkerManager(null);
		adder = test.createAdder(null);
			
		Utils.getPABindingController(master).bindFc("solver", solver.getFcInterface("solver"));
		
		Utils.getPAContentController(solver).addFcSubComponent(adder);
		Utils.getPABindingController(solver).bindFc("solver", adder.getFcInterface("solver"));

		Utils.getPAContentController(solver).addFcSubComponent(workerManager);
		Utils.getPABindingController(adder).bindFc("manager", workerManager.getFcInterface("manager"));
		
		for(int i = 1; i <= WORKERS; i++) {
			worker = test.createWorker(i, null);
			Utils.getPAContentController(solver).addFcSubComponent(worker);
			Utils.getPABindingController(workerManager).bindFc("workers", worker.getFcInterface("worker"));
		}
		
		Utils.getPAGCMLifeCycleController(master).startFc();
		Utils.getPAGCMLifeCycleController(solver).startFc();

		if(MONITORABLE) {
			Remmos.enableMonitoring(master);
			MonitorController monControl = (MonitorController) master.getFcInterface(Constants.MONITOR_CONTROLLER);
			monControl.startMonitoring();
			AnalysisController analysis = (AnalysisController) workerManager.getFcInterface(Constants.ANALYSIS_CONTROLLER);
			analysis.analyze();
			
			// METRICAS
			MonitorController monitor = (MonitorController) master.getFcInterface(Constants.MONITOR_CONTROLLER);
			for(int i = 0; i < WORKERS; i++) {
				monitor.addMetric("counter", new WorkerTasksCounterMetric(), "/Solver/Adder/WorkerManager/Worker" + (i+1));
			}
	
			// REGLAS
			analysis.addRule("half-finished", new HalfFinished(WORKERS, BOARD_SIZE));
			analysis.addRule("finished", new Finished(WORKERS, BOARD_SIZE));
		}
	
		Master nqueen = ((Master) master.getFcInterface("master"));
		System.out.println("testing... nqueens["+WORKERS+"][5]..." + nqueen.calculate(new IntWrapper(5)).getLongValue());
	
		// Comienzo el calculo
		long total_time = 0, time, result_value = 0;
		System.out.println("nqueens["+WORKERS+"]["+BOARD_SIZE+"]...");
		time = System.currentTimeMillis();
		LongWrapper result = nqueen.calculate(new IntWrapper(BOARD_SIZE));

		// Espero el resultado antes de terminar
		result_value = result.getLongValue();
		total_time += System.currentTimeMillis() - time;
		System.out.println("[" + System.currentTimeMillis() + "]" + result_value + " in " + total_time + " ms.");
		Thread.sleep(100000);
	}

}
