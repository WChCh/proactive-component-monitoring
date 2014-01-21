package org.objectweb.proactive.examples.components.mmultiplier;

import java.text.DecimalFormat;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.componentcontroller.analysis.AnalysisController;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorController;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;
import org.objectweb.proactive.core.component.control.PAContentController;
import org.objectweb.proactive.core.component.control.PAGCMLifeCycleController;
import org.objectweb.proactive.core.component.control.PAMembraneController;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactory;
import org.objectweb.proactive.examples.components.mmultiplier.metrics.TasksPerMinute;
import org.objectweb.proactive.examples.components.mmultiplier.metrics.WorkerTime;
import org.objectweb.proactive.examples.components.mmultiplier.metrics.WorkersChange;
import org.objectweb.proactive.examples.components.mmultiplier.rules.NumberOfWorkers;
import org.objectweb.proactive.examples.components.mmultiplier.rules.TaskSolvingTimeThreshold;
import org.objectweb.proactive.extra.component.fscript.control.PAReconfigurationController;


public class Main {

	/*
	 * CONFIGURACION
	 * ----------------------------------------------------------------------*/
	public static final int WORKERS = 1;
	private static boolean MANAGED = true;
	
	public static final int N_INIT = 13;
	public static final int N_END = 13;
	public static final int B_INIT = 7;
	public static final int B_END = 7;
	public static final int TESTS = 1;


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

		Component mmultiplier = MatrixMultiplierBuilder.build(patf, pagf, null, MANAGED);
		Component master = MasterBuilder.build(patf, pagf, null, MANAGED);
		Component resultRepo = ResultRepositoryBuilder.build(patf, pagf, null, MANAGED);
		Component taskRepo = TaskRepositoryBuilder.build(patf, pagf, null, MANAGED);
		Component tester = TesterBuilder.build(patf, pagf, null, MANAGED);
		Component[] workers = new Component[WORKERS];
		for(int i = 0; i < WORKERS; i++)
			workers[i] = WorkerBuilder.build(i, patf, pagf, null, MANAGED);

		if (MANAGED) {
			setMonitorable(mmultiplier);
			setMonitorable(master);
			for (int i = 0; i < WORKERS; i++)
				setMonitorable(workers[i]);
			setMonitorable(taskRepo);
			setMonitorable(resultRepo);
			setMonitorable(tester);
		}

		PAContentController mmCC = Utils.getPAContentController(mmultiplier);
		mmCC.addFcSubComponent(master);
		mmCC.addFcSubComponent(taskRepo);
		mmCC.addFcSubComponent(resultRepo);
		for(int i = 0; i < WORKERS; i++)
			mmCC.addFcSubComponent(workers[i]);

		Utils.getPABindingController(mmultiplier).bindFc(MMConstants.MATRIX_MULTIPLIER_ITF, master.getFcInterface(MMConstants.MATRIX_MULTIPLIER_ITF));
		Utils.getPABindingController(master).bindFc(MMConstants.TASK_REPOSITORY_ITF, taskRepo.getFcInterface(MMConstants.TASK_REPOSITORY_ITF));
		Utils.getPABindingController(master).bindFc(MMConstants.RESULT_REPOSITORY_ITF, resultRepo.getFcInterface(MMConstants.RESULT_REPOSITORY_ITF));
		for(int i = 0; i < WORKERS; i++) {
			PAMembraneController memW = Utils.getPAMembraneController(workers[i]);
			memW.stopMembrane();
			memW.nfBindFc(Remmos.MONITOR_SERVICE_COMP + ".parent"+"-external-"+Remmos.MONITOR_SERVICE_ITF, "parent"+"-external-"+Constants.MONITOR_CONTROLLER);
			PAMembraneController memMM = Utils.getPAMembraneController(mmultiplier);
			memMM.stopMembrane();
			memMM.nfBindFc(MMConstants.WORKER_COMP + i + ".parent"+"-external-"+Constants.MONITOR_CONTROLLER, "internal-server-"+Constants.MONITOR_CONTROLLER);
			memMM.startMembrane();
			memW.startMembrane();
			Utils.getPABindingController(master).bindFc(MMConstants.WORKER_MULTICAST_ITF, workers[i].getFcInterface(MMConstants.WORKER_ITF));
			Utils.getPABindingController(workers[i]).bindFc(MMConstants.LOOPBACK_ITF, workers[i].getFcInterface(MMConstants.WORKER_ITF));
			Utils.getPABindingController(workers[i]).bindFc(MMConstants.TASK_REPOSITORY_ITF, taskRepo.getFcInterface(MMConstants.TASK_REPOSITORY_ITF)); 
			Utils.getPABindingController(workers[i]).bindFc(MMConstants.RESULT_REPOSITORY_ITF, resultRepo.getFcInterface(MMConstants.RESULT_REPOSITORY_ITF));
		}
		Utils.getPABindingController(resultRepo).bindFc(MMConstants.RESULT_RECEIVER_ITF, mmultiplier.getFcInterface(MMConstants.RESULT_RECEIVER_ITF));
		Utils.getPABindingController(mmultiplier).bindFc(MMConstants.RESULT_RECEIVER_ITF, tester.getFcInterface(MMConstants.RESULT_RECEIVER_ITF));		
		Utils.getPABindingController(tester).bindFc(MMConstants.MATRIX_MULTIPLIER_ITF, mmultiplier.getFcInterface(MMConstants.MATRIX_MULTIPLIER_ITF));
		

		// LyfeCycle
		Utils.getPAGCMLifeCycleController(tester).startFc();
		Utils.getPAGCMLifeCycleController(mmultiplier).startFc();
		Utils.getPAGCMLifeCycleController(master).startFc();
		Utils.getPAGCMLifeCycleController(taskRepo).startFc();
		Utils.getPAGCMLifeCycleController(resultRepo).startFc();
		for(int i = 0; i < WORKERS; i++)
			Utils.getPAGCMLifeCycleController(workers[i]).startFc();
		
		// Mini test, para asegurarse que las cosas esten en orden.
		Tester theTester = (Tester) tester.getFcInterface(MMConstants.TESTER_ITF);
		System.out.println("[MAIN] testing...");
		theTester.start(4, 4, 2, 2, 1);

		/** MONITORING **/
		MonitorController monControl = null;
		if (MANAGED) {
			Remmos.enableMonitoring(mmultiplier);
			monControl = (MonitorController) mmultiplier.getFcInterface(Constants.MONITOR_CONTROLLER);
			monControl.startMonitoring();
			monControl.addMetric("workers-change", new WorkersChange(25000));
			monControl.addMetric("tasksPerMinute", new TasksPerMinute(), "/" + MMConstants.MASTER_COMP + "/" + MMConstants.RESULT_REPOSITORY_COMP);
			for(int i = 0; i < WORKERS; i++)
				monControl.addMetric("workerTime", new WorkerTime(), "/" + MMConstants.MASTER_COMP + "/" + MMConstants.WORKER_COMP + i);
		
			Thread.sleep(5000);
		}
		
		System.out.println("[MAIN] There are " + WORKERS + " workers... GO!");
		theTester.start(N_INIT, N_END, B_INIT, B_END, TESTS);


		if(MANAGED) {
			for(int i = 0; i < WORKERS; i++) {
				AnalysisController analysis = (AnalysisController) workers[i].getFcInterface(Constants.ANALYSIS_CONTROLLER);
				analysis.addRule("timeThreshold", new TaskSolvingTimeThreshold("/", "workerTime", 1.8));
				analysis.setDelay(20000);
				analysis.analyze();
			}	
			AnalysisController analysis = (AnalysisController) mmultiplier.getFcInterface(Constants.ANALYSIS_CONTROLLER);
			analysis.addRule("number-of-workers", new NumberOfWorkers(250));
			analysis.setDelay(40000);
			analysis.analyze();	
	
			int counter = 0;
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(3);
			while(true) {
				String head = "[GUI][" + counter + "] ";
				for(int i = 0; i < WORKERS; i++) {
					Double time = Double.parseDouble(monControl.getMetricValue("workerTime", "/"+MMConstants.MASTER_COMP+"/" + MMConstants.WORKER_COMP + i).toString());
					time = time/1000000; //nanosec to sec
					System.out.println(head + "Worker" + i + " is taken " + df.format(time) + " seconds.");
				}

				Double tpm = Double.parseDouble(monControl.getMetricValue("tasksPerMinute", "/"+MMConstants.MASTER_COMP+"/" + MMConstants.RESULT_REPOSITORY_COMP).toString());
				System.out.println(head + "Tasks solved per minute: " + tpm + ".");

				counter++;
				Thread.sleep(2000);
			}
		}
	}

}
