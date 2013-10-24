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
	 * The first two queens positions are give as the positions in the first
	 * and second column. For example:
	 * 	  __1___2___3_
	 *  1 |_Q_|___|___  
	 *  2 |___|___|___
	 *  2 |___|_Q_|___
	 *  3 |   |   |
	 *  
	 *  The parameters must be col1 = 1, col2 = 3.
	 *  
	 * @param section
	 * @param subsection
	 * @return
	 */
	public LongWrapper solve(IntWrapper col1, IntWrapper col2) {
		int[] board = new int[boardSize + 1]; 	
		for(int i = 0; i < board.length; i++) board[i] = 0;
		board[1] = col1.getIntValue();
		board[2] = col2.getIntValue();
		if(safe(board[2], 2, board)) {
			return new LongWrapper(place(3, board));
		}
		return new LongWrapper(0);
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
