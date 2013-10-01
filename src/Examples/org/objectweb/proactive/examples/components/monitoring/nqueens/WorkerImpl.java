package org.objectweb.proactive.examples.components.monitoring.nqueens;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;

/**
 * NQueens Algorithm: http://elvis.rowan.edu/~hartley/OSusingSR/J/nQueens.java
 * 
 */
public class WorkerImpl implements Worker {

	private int boardSize = 0;
	
	/**
	 * Sets the size of the board.
	 * 
	 * @param boardSize
	 */
	public void setUp(IntWrapper boardSize) {
		this.boardSize = boardSize.getIntValue();
	}

	/** 
	 * finds the number of solutions by searching on the given
	 * section of the board.
	 * 
	 * @param boardSection
	 * @return
	 */
	public LongWrapper solve(IntWrapper boardSection) {
		int[] board = new int[boardSize + 1];
		board[1] = boardSection.getIntValue();
		return new LongWrapper(place(2, board));
	}

	private long place(int column, int board[]) {
		long solutions = 0;
		for (int row = 1; row <= boardSize; row++) {
			board[column] = row;
			if (safe(row, column, board)) {
				if (column == boardSize) solutions++;
				else solutions += place(column+1, board);
			}
			board[column] = 0;
		}
		return solutions;
	}
	
	private boolean safe(int row, int column, int board[]) {
		for (int j=1; j<column; j++) {
			if (board[column-j] == row   ||
				board[column-j] == row-j ||
				board[column-j] == row+j) {
				return false;
			}
		}
		return true;
	}
}
