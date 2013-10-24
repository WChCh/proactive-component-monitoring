package org.objectweb.proactive.examples.components.monitoring.nqueens;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;


public class Main {

	static int WORKERS = 10;
	static int BOARD_SIZE = 15;

	public static void main(String[] args) {
		NQueensBuilder nqueens = new NQueensBuilder();
		nqueens.setUp();
		nqueens.setMonitorable(true);
		
		int tests = 0;
		LongWrapper result;
		
		try {
			nqueens.build(WORKERS);
			if(nqueens.init()) System.out.println("[NQUEENS] Initianted");
			Master master = ((Master) nqueens.getMaster().getFcInterface("master"));
			Thread gui = new Thread(new Gui(nqueens.getMaster(), WORKERS));
			gui.start();
			Thread.sleep(2000);
			
			System.out.println("testing... nqueens["+WORKERS+"][5]..." + master.calculate(new IntWrapper(5)).getLongValue());
					
			long total_time = 0, time, result_value = 0;
			time = System.currentTimeMillis();
			result = master.calculate(new IntWrapper(BOARD_SIZE));
			result_value = result.getLongValue();
			total_time += System.currentTimeMillis() - time;
			System.out.println(result_value + " in " + total_time + " ms.");
		
			/*for(int i = 17; i <= 17; i++) {
				long total_time = 0, time, result_value = 0;

				System.out.print("nqueens["+workers+"]["+i+"]... ");
				
				switch(i) {
					case 12: tests = 128;
					case 13: tests = 64;
					case 14: tests = 32;
					case 15: tests = 16;
					case 16: tests = 8;
					case 17: tests = 4;
					default: tests = 1;
				}
				for(int j = 0; j < 1; j++) {
					time = System.currentTimeMillis();
					result = master.calculate(new IntWrapper(i));
					result_value = result.getLongValue();
					total_time += System.currentTimeMillis() - time;
				}
				System.out.println(result_value + " in " + (total_time/tests) + " ms.");
			}*/
			
			nqueens.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
