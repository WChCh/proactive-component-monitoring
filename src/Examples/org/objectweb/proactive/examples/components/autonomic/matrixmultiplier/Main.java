package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier;

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
import org.objectweb.proactive.examples.components.autonomic.matrixmultiplier.metrics.RepositoryProgress;
import org.objectweb.proactive.examples.components.autonomic.matrixmultiplier.metrics.WorkerProgress;
import org.objectweb.proactive.examples.components.autonomic.matrixmultiplier.rules.HalfFinishedWorkers;
import org.objectweb.proactive.examples.components.autonomic.matrixmultiplier.rules.ResponseTimeLimit;


public class Main {

	/*
	 * CONFIGURACION
	 * ----------------------------------------------------------------------*/
	public static final int WORKERS = 1;

	public static final boolean AUTONOMIC = true;
	public static final boolean MONITORING = false;
	public static final boolean ANALYSIS = false;

	public static final int N_INIT = 7;
	public static final int N_END = 8;
	public static final int B_INIT = 5;
	public static final int B_END = 7;
	public static final int TESTS = 8;
	/* ----------------------------------------------------------------------*/
	

	public static final String MATRIX_MULTIPLIER_ITF = "matrix-multiplier-itf";
	public static final String REPOSITORY_ITF = "repository-itf";
	public static final String WORKER_ITF = "worker-itf";
	public static final String WORKER_MULTICAST_ITF = "worker-multicast-itf";
	public static final String RESULT_RECEIVER_ITF = "result-reciver-itf";
	public static final String TESTER_ITF = "tester-itf";
	
	
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
		Component boot = Utils.getBootstrapComponent();
		PAGCMTypeFactory patf = (PAGCMTypeFactory) Utils.getPAGCMTypeFactory(boot);
		PAGenericFactory pagf = (PAGenericFactory) Utils.getPAGenericFactory(boot);

		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType(MATRIX_MULTIPLIER_ITF, MatrixMultiplier.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(RESULT_RECEIVER_ITF, ResultReceiver.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY)
		};		
		PAGCMInterfaceType[] nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.COMPOSITE) : new PAGCMInterfaceType[] {};
		Component matrixMultiplier = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("MatrixMultiplier", Constants.COMPOSITE), null, null);

		// MASTER
		fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType(MATRIX_MULTIPLIER_ITF, MatrixMultiplier.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(REPOSITORY_ITF, Repository.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(WORKER_MULTICAST_ITF, WorkerMulticast.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.MULTICAST_CARDINALITY),
		};		
		nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.PRIMITIVE) : new PAGCMInterfaceType[] {};
		Component master = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("Master", Constants.PRIMITIVE), new ContentDescription(MasterImpl.class.getName()), null);
		
		//WORKERS
		Component[] workerComps = new Component[WORKERS];
		Component[] workers = new Component[WORKERS];
		for(int i = 0; i < WORKERS; i++) {
			fItfType = new PAGCMInterfaceType[] {
					(PAGCMInterfaceType) patf.createGCMItfType(WORKER_ITF, Worker.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
					(PAGCMInterfaceType) patf.createGCMItfType(REPOSITORY_ITF, Repository.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
			};		
			nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.COMPOSITE) : new PAGCMInterfaceType[] {};
			workerComps[i] = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("WorkerComp" + i, Constants.COMPOSITE), null, null);
		
			
			fItfType = new PAGCMInterfaceType[] {
					(PAGCMInterfaceType) patf.createGCMItfType(WORKER_ITF, Worker.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
					(PAGCMInterfaceType) patf.createGCMItfType(REPOSITORY_ITF, Repository.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
			};		
			nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.PRIMITIVE) : new PAGCMInterfaceType[] {};
			workers[i] = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("Worker" + i, Constants.PRIMITIVE), new ContentDescription(WorkerImpl.class.getName()), null);
		}

		// REPOSITORY
		fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType(REPOSITORY_ITF, Repository.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(RESULT_RECEIVER_ITF, ResultReceiver.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY),
		};
		nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.COMPOSITE) : new PAGCMInterfaceType[] {};
		Component repoComp = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("RepoComp", Constants.COMPOSITE), null, null);
		
		fItfType = new PAGCMInterfaceType[] {
				(PAGCMInterfaceType) patf.createGCMItfType(REPOSITORY_ITF, Repository.class.getName(), PAGCMTypeFactory.SERVER, PAGCMTypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
				(PAGCMInterfaceType) patf.createGCMItfType(RESULT_RECEIVER_ITF, ResultReceiver.class.getName(), PAGCMTypeFactory.CLIENT, PAGCMTypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY),
		};		
		nfItfType = AUTONOMIC ? Remmos.createMonitorableNFType(patf, fItfType, Constants.PRIMITIVE) : new PAGCMInterfaceType[] {};
		Component repo = pagf.newFcInstance(patf.createFcType(fItfType, nfItfType), new ControllerDescription("Repo", Constants.PRIMITIVE), new ContentDescription(RepositoryImpl.class.getName()), null);


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
			for(int i = 0; i < WORKERS; i++) {
				setMonitorable(workerComps[i]);
				setMonitorable(workers[i]);
			}
			setMonitorable(repoComp);
			setMonitorable(repo);
			setMonitorable(tester);
		}


		// Binding 
		System.out.println("[MAIN] Binding componentes...");
		Utils.getPAContentController(matrixMultiplier).addFcSubComponent(master);
		Utils.getPABindingController(matrixMultiplier).bindFc(MATRIX_MULTIPLIER_ITF, master.getFcInterface(MATRIX_MULTIPLIER_ITF));

		Utils.getPAContentController(repoComp).addFcSubComponent(repo);
		Utils.getPABindingController(repoComp).bindFc(REPOSITORY_ITF, repo.getFcInterface(REPOSITORY_ITF));
		Utils.getPABindingController(repo).bindFc(RESULT_RECEIVER_ITF, repoComp.getFcInterface(RESULT_RECEIVER_ITF));
		
		Utils.getPAContentController(matrixMultiplier).addFcSubComponent(repoComp);
		Utils.getPABindingController(repoComp).bindFc(RESULT_RECEIVER_ITF, matrixMultiplier.getFcInterface(RESULT_RECEIVER_ITF));
		Utils.getPABindingController(master).bindFc(REPOSITORY_ITF, repoComp.getFcInterface(REPOSITORY_ITF));


		for(int i = 0; i < WORKERS; i++) {
			Utils.getPAContentController(workerComps[i]).addFcSubComponent(workers[i]);
			Utils.getPABindingController(workerComps[i]).bindFc(WORKER_ITF, workers[i].getFcInterface(WORKER_ITF));
			Utils.getPABindingController(workers[i]).bindFc(REPOSITORY_ITF, workerComps[i].getFcInterface(REPOSITORY_ITF));
			
			Utils.getPAContentController(matrixMultiplier).addFcSubComponent(workerComps[i]);
			Utils.getPABindingController(master).bindFc(WORKER_MULTICAST_ITF, workerComps[i].getFcInterface(WORKER_ITF));
			Utils.getPABindingController(workerComps[i]).bindFc(REPOSITORY_ITF, repoComp.getFcInterface(REPOSITORY_ITF));
		}
		
		Utils.getPABindingController(tester).bindFc(MATRIX_MULTIPLIER_ITF, matrixMultiplier.getFcInterface(MATRIX_MULTIPLIER_ITF));
		Utils.getPABindingController(matrixMultiplier).bindFc(RESULT_RECEIVER_ITF, tester.getFcInterface(RESULT_RECEIVER_ITF));
	

		// LyfeCycle
		Utils.getPAGCMLifeCycleController(tester).startFc();
		Utils.getPAGCMLifeCycleController(matrixMultiplier).startFc();
	

		
		// Mini test, para asegurarse que las cosas esten en orden.
		Tester theTester = (Tester) tester.getFcInterface(TESTER_ITF);
		System.out.println("[MAIN] testing...");
		theTester.start(4, 4, 2, 2, 1);

		// todas las tareas de calculo que los workers deben calcular
		int total_tasks = 0;
		for(int N = N_INIT; N <= N_END; N++) {
			for(int B = B_INIT; B <= B_END; B++) {
				total_tasks += TESTS*((int) Math.pow(2, 2*(N-B)));
			}
		}


		// Monitoreo y Analysis
		MonitorControl monControl;
		if(MONITORING || ANALYSIS) {
			Remmos.enableMonitoring(matrixMultiplier);
			monControl = (MonitorControl) matrixMultiplier.getFcInterface(Constants.MONITOR_CONTROLLER);
			monControl.startMonitoring();
			
			for(int i = 0; i < WORKERS; i++) {
				monControl.addMetric("workerProgress", new WorkerProgress(), "/Master/WorkerComp" + i);
				monControl.getMetricValue("workerProgress", "/Master/WorkerComp" + i).toString();
			}
			
			if(MONITORING) {
				monControl.addMetric("masterProgress", new RepositoryProgress(), "/Master");
				monControl.getMetricValue("masterProgress", "/Master").toString();
				
				monControl.addMetric("repoProgress", new RepositoryProgress(), "/Master/WorkerComp0/RepoComp");
				monControl.getMetricValue("repoProgress", "/Master/WorkerComp0/RepoComp").toString();
			}
			if(ANALYSIS) {
				AnalysisController mmAnalysis = (AnalysisController) matrixMultiplier.getFcInterface(Constants.ANALYSIS_CONTROLLER);
				mmAnalysis.addRule("halfFinishedWorker", new HalfFinishedWorkers("/Master/WorkerComp", "workerProgress", WORKERS, total_tasks/WORKERS));
				mmAnalysis.start();
				for(int i = 0; i < WORKERS; i++) {
					AnalysisController analysis = (AnalysisController) workerComps[i].getFcInterface(Constants.ANALYSIS_CONTROLLER);
					analysis.addRule("responseTime", new ResponseTimeLimit("/", "workerProgress", 5000));
					analysis.start();
				}
			}
		}
		
		// Muy necesario... la asincronia hace que los test partan mientras las
		// metricas se estan reciena comodando... con dos segundos funciona
		// bien, coloco cinco para estar seguro
		System.out.println("Starting in...");
		for(int i = 5; i > 0; i--) {
			System.out.println("" + i);
			Thread.sleep(1000);
		}

		
		System.out.println("[MAIN] There are " + WORKERS + " workers... GO!");
		theTester.start(N_INIT, N_END, B_INIT, B_END, TESTS);

		
		if(AUTONOMIC) {
			int total_tests = 0;
			for(int N = N_INIT; N <= N_END; N++) {
				for(int B = B_INIT; B <= B_END; B++) {
					total_tests += TESTS;
				}
			}
			int counter = 0, masterProgress = 0, repoProgress = 0, workerProgress = 0;
			int wp_total_counter = 0; // suma de tareas realizadas por workers hasta el momento

			while(true) {
				String head = "[GUI][" + counter + "] ";
	
				if(masterProgress >= total_tests) {
					System.out.println("Este es el ultimo test, asumo que el"
							+" calculo ya debe haber terminado, no imprimire mas.");
					break;
				}
				if(MONITORING) {
					masterProgress = Integer.parseInt(monControl.getMetricValue("masterProgress", "/Master").toString());
					System.out.println(head + "Master Progress = " + masterProgress + "/" + total_tests );
	
					
					// calculo de "wp_total_counter"
					wp_total_counter = 0;
					String w_msg = "Workers Progress: ";
					for(int i = 0; i < WORKERS; i++) {
						workerProgress = Integer.parseInt(monControl.getMetricValue("workerProgress", "/Master/WorkerComp" + i).toString());
						wp_total_counter += workerProgress;
						w_msg += workerProgress + ", ";
					}
					System.out.println(head + w_msg);
	
					
					// Cada vez que se ejecuta master, se genera un evento en
					// progress que corresponde al metodo que le envia la matriz.
					// no me interesa y la resto del total de eventos
					repoProgress = Integer.parseInt(monControl.getMetricValue("repoProgress", "/Master/WorkerComp0/RepoComp").toString()) - masterProgress;
					// Hay dos problemas:
					// - El repositorio recibe eventos de mas, ocurre en los casos pequenos
					// - El repositorio recibe eventos de menos, ocurre cuando cuando hay muchas tareas (data-race?? desincronizacion??? )
					System.out.println(head + "Repo Progress = " + repoProgress + " (" + wp_total_counter + "/" + total_tasks + ")");
				}

				Thread.sleep(5000);
				counter++;
			}
		}
	}
}
