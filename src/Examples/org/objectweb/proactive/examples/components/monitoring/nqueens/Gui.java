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
import org.objectweb.proactive.examples.components.monitoring.nqueens.metrics.NumberOfWorkersMetric;
import org.objectweb.proactive.examples.components.monitoring.nqueens.metrics.RemainingWorkMetric;

public class Gui extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	
	private JTextField interval, memInfo, numInfo, remInfo, status;
	private MonitorControl monitor;
	private long DELAY = 2000;

	
	/**
	 * 
	 * @param master
	 */
	public Gui(Component master) {
		
		this.setTitle("NQueens Monitors GUI");
		try {
			monitor = (MonitorControl) master.getFcInterface(Constants.MONITOR_CONTROLLER);
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
			monitor = null;
		}

        JPanel panel = new JPanel(new GridLayout(5,2));
        panel.add(new JLabel("Monitoring interval (in millis)"));
        panel.add((interval = new JTextField("2000")));
        panel.add(new JLabel("Memory Usage"));
        panel.add((memInfo = new JTextField("")));
        panel.add(new JLabel("Number of Workers"));
        panel.add((numInfo = new JTextField("")));
        panel.add(new JLabel("Remaining Work"));
        panel.add((remInfo = new JTextField("")));
        panel.add(new JLabel("Status"));
        panel.add((status = new JTextField("starting...")));
        getContentPane().add(panel);
        
        interval.addActionListener(setInterval);

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
		long counter = 0;
		int num = 0;
	
		/** Master memory usage **/
		status.setText("adding memUsage metric on Master...");
		monitor.addMetric("memUsage", new MemoryUsageMetric());
		
		status.setText("adding numOfWorkers metric on Solver...");
		monitor.addMetric("numOfWorkers", new NumberOfWorkersMetric(), "/Solver/Adder/WorkerManager");
		
		status.setText("adding remWork metric on WorkerManager...");
		monitor.addMetric("remWork", new RemainingWorkMetric(), "/Solver/Adder");
		
		while(true) {
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			status.setText("getting value of memUsage metric from Master...");
			memInfo.setText(monitor.getMetricValue("memUsage").getObject().toString());
			
			status.setText("getting value of numbOfWorkers metric from Solver...");
			num = (Integer) monitor.getMetricValue("numOfWorkers", "/Solver/Adder/WorkerManager").getObject();
			numInfo.setText("" + num);
			
			status.setText("getting value of remWork metric from WorkerManager...");
			remInfo.setText(monitor.getMetricValue("remWork", "/Solver/Adder").getObject().toString() + "/" + num);
		}
	}
}
