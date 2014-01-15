package org.objectweb.proactive.examples.components.mmultiplier;

import java.text.DecimalFormat;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.componentcontroller.analysis.AnalysisController;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactory;
import org.objectweb.proactive.examples.components.mmultiplier.metrics.TasksPerMinute;
import org.objectweb.proactive.examples.components.mmultiplier.metrics.WorkerTime;
import org.objectweb.proactive.examples.components.mmultiplier.rules.TaskSolvingTimeThreshold;
import org.objectweb.proactive.extra.component.fscript.control.PAReconfigurationController;


public class Main {

	/*
	 * CONFIGURACION
	 * ----------------------------------------------------------------------*/
	public static final int WORKERS = 1;
	public static final boolean AUTONOMIC = true;
	public static final boolean MONITORING = true;
	public static final boolean ANALYSIS = true;

	public static final int N_INIT = 12;
	public static final int N_END = 12;
	public static final int B_INIT = 7;
	public static final int B_END = 7;
	public static final int TESTS = 1;
	/* ----------------------------------------------------------------------*/
	

	public static final String MATRIX_MULTIPLIER_ITF = "matrix-multiplier-itf";
	public static final String TASK_REPOSITORY_ITF = TaskRepository.ITF_NAME;
	public static final String RESULT_REPOSITORY_ITF = ResultRepository.ITF_NAME;
	public static final String WORKER_ITF = "worker-itf";
	public static final String WORKER_MULTICAST_ITF = "worker-multicast-itf";
	public static final String RESULT_RECEIVER_ITF = "result-reciver-itf";
	public static final String TESTER_ITF = "tester-itf";
	
	
	private static void setMonitorable(Component component) {
		try {
			Remmos.addObjectControllers((PAComponent) component);
			Utils.getPAMembraneController(component).startMembrane();
			Remmos.addMonitoring(component);
			Remmos.addReconfiguration(component);
			Remmos.bindAnalysisWithReconfiguration(component);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Component boot = Utils.getBootstrapComponent();
		PAGCMTypeFactory patf = (PAGCMTypeFactory) Utils.getPAGCMTypeFactory(boot);
		PAGenericFactory pagf = (PAGenericFactory) Utils.getPAGenericFactory(boot);

		// MATRIX MULTIPLIER
		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType(MATRIX_MULTIPLIER_ITF, MatrixMultiplier.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(RESULT_RECEIVER_ITF, ResultReceiver.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY)
		};		
		PAGCMInterfaceType[] nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.COMPOSITE) : new PAGCMInterfaceType[] {};
		final Component matrixMultiplier = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("MatrixMultiplier", Constants.COMPOSITE), null, null);

		// MASTER
		fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType(MATRIX_MULTIPLIER_ITF, MatrixMultiplier.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(RESULT_REPOSITORY_ITF, ResultRepository.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(TASK_REPOSITORY_ITF, TaskRepository.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(WORKER_MULTICAST_ITF, WorkerMulticast.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.MULTICAST_CARDINALITY),
		};		
		nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.PRIMITIVE) : new PAGCMInterfaceType[] {};
		Component master = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("Master", Constants.PRIMITIVE), new ContentDescription(MasterImpl.class.getName()), null);
		
		//WORKERS
		final Component[] workers = new Component[WORKERS];
		for(int i = 0; i < WORKERS; i++) {
			fItfType = new PAGCMInterfaceType[] {
					(PAGCMInterfaceType) patf.createGCMItfType(WORKER_ITF, Worker.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
					(PAGCMInterfaceType) patf.createGCMItfType("loopback", Worker.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
					(PAGCMInterfaceType) patf.createGCMItfType(RESULT_REPOSITORY_ITF, ResultRepository.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
					(PAGCMInterfaceType) patf.createGCMItfType(TASK_REPOSITORY_ITF, TaskRepository.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
					(PAGCMInterfaceType) patf.createGCMItfType(Constants.ATTRIBUTE_CONTROLLER, WorkerAttributes.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY),
			};		
			nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.PRIMITIVE) : new PAGCMInterfaceType[] {};
			workers[i] = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("Worker" + i, Constants.PRIMITIVE), new ContentDescription(WorkerImpl.class.getName()), null);
		}

		// TASK RESPOSITORY
		fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType(TASK_REPOSITORY_ITF, TaskRepository.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY)
		};
		nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.PRIMITIVE) : new PAGCMInterfaceType[] {};
		Component taskRepo = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("TaskRepo", Constants.PRIMITIVE), new ContentDescription(TaskRepositoryImpl.class.getName()), null);
	
		// RESULT REPOSITORY
		fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType(RESULT_REPOSITORY_ITF, ResultRepository.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(RESULT_RECEIVER_ITF, ResultReceiver.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY),
		};		
		nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.PRIMITIVE) : new PAGCMInterfaceType[] {};
		Component resultRepo = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("ResultRepo", Constants.PRIMITIVE), new ContentDescription(ResultRepositoryImpl.class.getName()), null);

		// TESTER
		fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType(TESTER_ITF, Tester.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(RESULT_RECEIVER_ITF, ResultReceiver.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(MATRIX_MULTIPLIER_ITF, MatrixMultiplier.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY)
		};		
		nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.PRIMITIVE) : new PAGCMInterfaceType[] {};
		Component tester = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("Tester", Constants.PRIMITIVE), new ContentDescription(TesterImpl.class.getName()), null);

		
		// Turtining monitorables
		if(AUTONOMIC) {
			System.out.println("[MAIN] Setting componentes monitoreables...");
			setMonitorable(matrixMultiplier);
			setMonitorable(master);
			for(int i = 0; i < WORKERS; i++)
				setMonitorable(workers[i]);
			setMonitorable(taskRepo);
			setMonitorable(resultRepo);
			setMonitorable(tester);
		}

		// Binding 
		System.out.println("[MAIN] Binding componentes...");
		Utils.getPAContentController(matrixMultiplier).addFcSubComponent(master);
		Utils.getPABindingController(matrixMultiplier).bindFc(MATRIX_MULTIPLIER_ITF, master.getFcInterface(MATRIX_MULTIPLIER_ITF));
		
		Utils.getPAContentController(matrixMultiplier).addFcSubComponent(resultRepo);
		Utils.getPABindingController(resultRepo).bindFc(RESULT_RECEIVER_ITF, matrixMultiplier.getFcInterface(RESULT_RECEIVER_ITF));
		Utils.getPABindingController(master).bindFc(RESULT_REPOSITORY_ITF, resultRepo.getFcInterface(RESULT_REPOSITORY_ITF));

		Utils.getPAContentController(matrixMultiplier).addFcSubComponent(taskRepo);
		Utils.getPABindingController(master).bindFc(TaskRepository.ITF_NAME, taskRepo.getFcInterface(TaskRepository.ITF_NAME));

		for(int i = 0; i < WORKERS; i++) {
			Utils.getPAContentController(matrixMultiplier).addFcSubComponent(workers[i]);
			Utils.getPABindingController(master).bindFc(WORKER_MULTICAST_ITF, workers[i].getFcInterface(WORKER_ITF));
			Utils.getPABindingController(workers[i]).bindFc("loopback", workers[i].getFcInterface(WORKER_ITF));
			Utils.getPABindingController(workers[i]).bindFc(RESULT_REPOSITORY_ITF, resultRepo.getFcInterface(RESULT_REPOSITORY_ITF));
			Utils.getPABindingController(workers[i]).bindFc(TaskRepository.ITF_NAME, taskRepo.getFcInterface(TaskRepository.ITF_NAME)); 
		}
		
		Utils.getPABindingController(tester).bindFc(MATRIX_MULTIPLIER_ITF, matrixMultiplier.getFcInterface(MATRIX_MULTIPLIER_ITF));
		Utils.getPABindingController(matrixMultiplier).bindFc(RESULT_RECEIVER_ITF, tester.getFcInterface(RESULT_RECEIVER_ITF));
	

		// LyfeCycle
		Utils.getPAGCMLifeCycleController(tester).startFc();
		Utils.getPAGCMLifeCycleController(matrixMultiplier).startFc();
		Utils.getPAGCMLifeCycleController(master).startFc();
		Utils.getPAGCMLifeCycleController(taskRepo).startFc();
		Utils.getPAGCMLifeCycleController(resultRepo).startFc();
		for(int i = 0; i < WORKERS; i++)
			Utils.getPAGCMLifeCycleController(workers[i]).startFc();
		
		// Mini test, para asegurarse que las cosas esten en orden.
		Tester theTester = (Tester) tester.getFcInterface(TESTER_ITF);
		//System.out.println("[MAIN] testing...");
		//theTester.start(4, 4, 2, 2, 1);

		// -----------------------------------------------------------------------------------
		// -----------------
		// ----------
		// ----
		// ----  RECONFIGURATION
		// ----
		// ----------
		// -------------------------------------------
		final PAReconfigurationController reconfiguration = (PAReconfigurationController) matrixMultiplier.getFcInterface(Constants.RECONFIGURATION_CONTROLLER);
	
		
		
		
		// todas las tareas de calculo que los workers deben calcular
		int total_tasks = 0;
		for(int N = N_INIT; N <= N_END; N++) {
			for(int B = B_INIT; B <= B_END; B++) {
				total_tasks += TESTS*((int) Math.pow(2, 2*(N-B)));
			}
		}

		// Monitoreo y Analysis
		MonitorControl monControl;
		if(AUTONOMIC && (MONITORING || ANALYSIS)) {
			Remmos.enableMonitoring(matrixMultiplier);
			monControl = (MonitorControl) matrixMultiplier.getFcInterface(Constants.MONITOR_CONTROLLER);
			monControl.startMonitoring();
			
			for(int i = 0; i < WORKERS; i++) {
				monControl.addMetric("workerTime", new WorkerTime(), "/Master/Worker" + i);
				monControl.getMetricValue("workerTime", "/Master/Worker" + i).toString();
			}
			
			if(MONITORING) {
				monControl.addMetric("tasksPerMinute", new TasksPerMinute(), "/Master/ResultRepo");
				monControl.getMetricValue("tasksPerMinute", "/Master/ResultRepo").toString();
			}
			if(ANALYSIS) {
				for(int i = 0; i < WORKERS; i++) {
					AnalysisController analysis = (AnalysisController) workers[i].getFcInterface(Constants.ANALYSIS_CONTROLLER);
					analysis.addRule("timeThreshold", new TaskSolvingTimeThreshold("/", "workerTime", 0.5));
					analysis.analyze();
				}
			}
		}
		
		// Muy necesario... la asincronia hace que los test partan mientras las
		// metricas se estan reciena comodando... con dos segundos funciona
		// bien, coloco cinco para estar seguro
		System.out.println("Starting in 5 secs..");
		Thread.sleep(5000);


		System.out.println("[MAIN] There are " + WORKERS + " workers... GO!");
		theTester.start(N_INIT, N_END, B_INIT, B_END, TESTS);

		(new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(25000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					reconfiguration.execute("gcma = deploy-gcma(\"" + this.getClass().getResource("GCMA.xml").getPath() + "\");");
					reconfiguration.execute("worker = gcm-new(\"org.objectweb.proactive.examples.components.mmultiplier.adl.Worker\", $gcma);");
					reconfiguration.execute("set-name($worker, \"WorkerN\");");
					reconfiguration.execute("stop($this);");
					reconfiguration.execute("add($this, $worker);");
					reconfiguration.execute("start($this);");
					reconfiguration.execute("bind($worker/interface::task-repo-itf, $this/child::TaskRepo/interface::task-repo-itf);");
					reconfiguration.execute("bind($worker/interface::result-repo-itf, $this/child::ResultRepo/interface::result-repo-itf);");
					reconfiguration.execute("bind($worker/interface::loopback, $worker/interface::worker-itf);");
					reconfiguration.execute("start($worker)");
					//+ "add($MatrixMultiplier, $worker);");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		})).start();

		if(AUTONOMIC) {
			int total_tests = 0;
			for(int N = N_INIT; N <= N_END; N++) {
				for(int B = B_INIT; B <= B_END; B++) {
					total_tests += TESTS;
				}
			}
			int counter = 0;
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(3);
			while(true) {
				String head = "[GUI][" + counter + "] ";
				if(MONITORING) {
					for(int i = 0; i < WORKERS; i++) {
						Double time = Double.parseDouble(monControl.getMetricValue("workerTime", "/Master/Worker" + i).toString());
						time = time/1000000; //nanosec to sec
						System.out.println(head + "Worker" + i + " is taken " + df.format(time) + " seconds.");
					}
	
					Double tpm = Double.parseDouble(monControl.getMetricValue("tasksPerMinute", "/Master/ResultRepo").toString());
					// Hay dos problemas:
					// - El repositorio recibe eventos de mas, ocurre en los casos pequenos
					// - El repositorio recibe eventos de menos, ocurre cuando cuando hay muchas tareas (data-race?? desincronizacion??? )
					System.out.println(head + "Tasks solved per minute: " + tpm + ".");
				}

				Thread.sleep(2000);
				counter++;
			}
		}
	}
}
