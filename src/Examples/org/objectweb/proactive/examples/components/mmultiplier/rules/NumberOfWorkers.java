package org.objectweb.proactive.examples.components.mmultiplier.rules;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.component.componentcontroller.analysis.Rule;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorController;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;
import org.objectweb.proactive.examples.components.mmultiplier.MMConstants;
import org.objectweb.proactive.examples.components.mmultiplier.Main;

public class NumberOfWorkers implements Rule {

	private static final long serialVersionUID = 1L;
	private String report;
	private double tasksPerMinute;

	public NumberOfWorkers(double tpm) {
		tasksPerMinute = tpm;
	}

	@Override
	public boolean isSatisfied(MonitorController monitor) {
		boolean hadChanges = Boolean.parseBoolean(monitor.getMetricValue(
				"workers-change").toString());
		double tpm = Double.parseDouble(monitor.getMetricValue(
				"tasksPerMinute",
				"/" + MMConstants.MASTER_COMP + "/"
						+ MMConstants.RESULT_REPOSITORY_COMP).toString());
		if (tasksPerMinute > tpm && !hadChanges) {
			report = "WE NEED TO ACCOMPLISH " + (tasksPerMinute - tpm)
					+ " TASKS MORE PER MINUTE\n"
					+ "NO IMPROVEMENTS ON WORKERS RECENTLY.";
			monitor.runMetric("workers-change");
			return false;
		}
		return true;
	}

	@Override
	public String getLastReport() {
		return report;
	}

	@Override
	public List<String> getExecutionCommands() {
		List<String> commands = new ArrayList<String>();
		commands.add("gcma = deploy-gcma(\""
				+ Main.class.getResource("GCMA.xml").getPath() + "\");");
		commands.add("worker = gcm-new(\"org.objectweb.proactive.examples.components.mmultiplier.adl.Worker\", $gcma);");
		commands.add("set-name($worker, \"WorkerN\");");
		commands.add("stop($this);");
		commands.add("add($this, $worker);");
		commands.add("start($this);");
		commands.add("bind($worker/interface::task-repo-itf, $this/child::"
				+ MMConstants.TASK_REPOSITORY_COMP
				+ "/interface::task-repo-itf);");
		commands.add("bind($worker/interface::result-repo-itf, $this/child::"
				+ MMConstants.RESULT_REPOSITORY_COMP
				+ "/interface::result-repo-itf);");
		commands.add("bind($worker/interface::loopback-itf, $worker/interface::worker-itf);");
		commands.add("start($worker);");
		return commands;
	}

}
