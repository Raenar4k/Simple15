package com.RaenarApps.Game15;

public class Point {
	private int x;
	private int y;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point p = (Point) obj;
			return (this.x == p.x) && (this.y == p.y);
		}
		return false;
	}
}
