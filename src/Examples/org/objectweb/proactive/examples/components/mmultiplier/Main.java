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
import org.objectweb.proactive.core.component.control.PAContentController;
import org.objectweb.proactive.core.component.control.PAGCMLifeCycleController;
import org.objectweb.proactive.core.component.control.PAMembraneController;
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
	private static boolean MANAGED = true;
	
	public static final int N_INIT = 12;
	public static final int N_END = 12;
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
		//System.out.println("[MAIN] testing...");
		//theTester.start(4, 4, 2, 2, 1);
		final PAReconfigurationController reconfiguration = (PAReconfigurationController) mmultiplier.getFcInterface(Constants.RECONFIGURATION_CONTROLLER);
	
		/** MONITORING **/
		MonitorControl monControl = null;
		if (MANAGED) {
			Remmos.enableMonitoring(mmultiplier);
			monControl = (MonitorControl) mmultiplier.getFcInterface(Constants.MONITOR_CONTROLLER);
			monControl.startMonitoring();
			monControl.addMetric("tasksPerMinute", new TasksPerMinute(), "/" + MMConstants.MASTER_COMP + "/" + MMConstants.RESULT_REPOSITORY_COMP);
			for(int i = 0; i < WORKERS; i++) {
				monControl.addMetric("workerTime", new WorkerTime(), "/" + MMConstants.MASTER_COMP + "/" + MMConstants.WORKER_COMP + i);
				AnalysisController analysis = (AnalysisController) workers[i].getFcInterface(Constants.ANALYSIS_CONTROLLER);
				analysis.addRule("timeThreshold", new TaskSolvingTimeThreshold("/", "workerTime", 0.5));
				analysis.analyze();
			}
			// Wait for monitors (necessary)
			Thread.sleep(5000);
		}

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
					reconfiguration.execute("bind($worker/interface::loopback-itf, $worker/interface::worker-itf);");
					reconfiguration.execute("start($worker)");
					//+ "add($MatrixMultiplier, $worker);");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		})).start();

		if(MANAGED) {
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
