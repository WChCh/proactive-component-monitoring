package org.objectweb.proactive.examples.components.monitoring.nqueens;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;


public class Main {

	static int WORKERS = 2;
	static int BOARD_SIZE = 16;
	static boolean MONITORABLE = true;

	public static void main(String[] args) {
		NQueensBuilder nqueens = new NQueensBuilder(MONITORABLE);
		LongWrapper result;
		
		try {
			nqueens.build(WORKERS);
			if(nqueens.init()) System.out.println("[NQUEENS] Initianted");
			Master master = ((Master) nqueens.getMaster().getFcInterface("master"));
			if(MONITORABLE) {
				System.out.println("MONITORABLE");
				Thread gui = new Thread(new Gui(nqueens.getMaster(), WORKERS));
				gui.start();
				Thread.sleep(2000);
			}
			
			// Test diminuto para inicializar los componentes
			System.out.println("testing... nqueens["+WORKERS+"][5]..." + master.calculate(new IntWrapper(5)).getLongValue());
					
			long total_time = 0, time, result_value = 0;
			System.out.println("nqueens["+WORKERS+"]["+BOARD_SIZE+"]...");
			
			// Comienzo el calculo
			time = System.currentTimeMillis();
			result = master.calculate(new IntWrapper(BOARD_SIZE));
			
			// Espero el resultado antes de terminar
			result_value = result.getLongValue();
			total_time += System.currentTimeMillis() - time;
			System.out.println(result_value + " in " + total_time + " ms.");
			
			nqueens.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
