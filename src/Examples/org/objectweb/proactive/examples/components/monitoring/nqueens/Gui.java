package org.objectweb.proactive.examples.components.monitoring.nqueens;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.examples.components.monitoring.nqueens.metrics.MemoryUsageMetric;
import org.objectweb.proactive.examples.components.monitoring.nqueens.metrics.NumberOfTasksMetric;
import org.objectweb.proactive.examples.components.monitoring.nqueens.metrics.RemainingWorkMetric;
import org.objectweb.proactive.examples.components.monitoring.nqueens.metrics.WorkerTasksCounterMetric;

public class Gui extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	
	private JTextField interval, memInfo, numInfo, remInfo, status;
	
	// Para monitorear los workers
	private JTextField[] workersInfo;
	private int N; // numero de workers
	
	// Monitor control de entrada
	private MonitorControl monitor;
	private long DELAY = 5000;

	
	/**
	 * 
	 * @param master
	 */
	public Gui(Component master, int workers) {
		
		this.setTitle("NQueens Monitors GUI");
		try {
			monitor = (MonitorControl) master.getFcInterface(Constants.MONITOR_CONTROLLER);
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
			monitor = null;
		}

        JPanel panel = new JPanel(new GridLayout(5 + workers, 2));
        panel.add(new JLabel("Monitoring interval (in millis)"));
        panel.add((interval = new JTextField("" + DELAY)));
        panel.add(new JLabel("Memory Usage"));
        panel.add((memInfo = new JTextField("")));
        panel.add(new JLabel("Number of Tasks"));
        panel.add((numInfo = new JTextField("")));
        panel.add(new JLabel("Finished tasks"));
        panel.add((remInfo = new JTextField("")));
        
		workersInfo = new JTextField[workers];
		for(int i = 0; i < workers; i++) {
			workersInfo[i] = new JTextField("0");
			panel.add(new JLabel("Worker " + (i +1)));
			panel.add(workersInfo[i]);
		}
        
        panel.add(new JLabel("Status"));
        panel.add((status = new JTextField("starting...")));
        getContentPane().add(panel);
        interval.addActionListener(setInterval);

        this.N = workers;
        
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
	}
	
	private ActionListener setInterval = new ActionListener() {  
        @Override
        public void actionPerformed(ActionEvent evnt) {
        	DELAY = Long.parseLong(interval.getText());
        }
	};

	@Override
	public void run() {
		/** Master memory usage **/
		status.setText("adding memUsage metric on Master...");
		monitor.addMetric("memUsage", new MemoryUsageMetric());
		
		status.setText("adding numOfWorkers metric on Solver...");
		monitor.addMetric("numOfWorkers", new NumberOfTasksMetric(), "/Solver/Adder/WorkerManager");
		
		status.setText("adding remWork metric on WorkerManager...");
		monitor.addMetric("remWork", new RemainingWorkMetric(), "/Solver/Adder");
		
		status.setText("adding metrics to Workers...");
		for(int i = 0; i < N; i++) {
			monitor.addMetric("counter", new WorkerTasksCounterMetric(), "/Solver/Adder/WorkerManager/Worker" + (i+1));
			workersInfo[i].setText("0");
		}
		
		while(true) {
			try {
				Object mem = monitor.getMetricValue("memUsage"),
						num = monitor.getMetricValue("numOfWorkers", "/Solver/Adder/WorkerManager"),
						rem = monitor.getMetricValue("remWork", "/Solver/Adder");
				
				Thread.sleep(DELAY);
				
				status.setText("getting value of memUsage metric from Master...");
				memInfo.setText(mem.toString());
				
				status.setText("getting value of numOfTasks metric from Solver...");
				numInfo.setText(num.toString());
				
				status.setText("getting value of remWork metric from WorkerManager...");
				remInfo.setText(rem.toString() + "/" + numInfo.getText());
				
				status.setText("getting value of metrics from Workers...");
				for(int i = 0; i < N; i++) {
					workersInfo[i].setText(monitor.getMetricValue("counter", "/Solver/Adder/WorkerManager/Worker" + (i+1)).toString());
				}
				
				//if(Integer.parseInt(remInfo.getText()) >= N)
				//	break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
