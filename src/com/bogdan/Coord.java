package com.bogdan;

class Coord {
	final int x;
	final int y;
	final int z;
	public Coord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	Coord relative(Coord relCoord) {
		return new Coord(this.x + relCoord.x, this.y + relCoord.y, this.z + relCoord.z);
	}
	public Coord invert() {
		return new Coord(-x, -y, -z);
	}
	public boolean equal(Coord c) {
		return c != null && x == c.x && y == c.y && z == c.z;
	}

	public String toString() {
		return "x=" + x + ", y=" + y + ", z=" + z;
	}
}