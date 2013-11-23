package org.objectweb.proactive.examples.components.autonomic.matrixmultiplier;

import java.io.Serializable;

public class Position implements Serializable {

	private static final long serialVersionUID = 1L;

	private int x, y;
	
	Position(int posX, int posY) {
		x = posX;
		y = posY;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
