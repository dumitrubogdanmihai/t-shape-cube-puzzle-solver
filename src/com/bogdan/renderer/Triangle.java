package com.bogdan.renderer;

import java.awt.Color;

public class Triangle {
	public final Vertex v1;
	public final Vertex v2;
	public final Vertex v3;
	public final Color color;
	
	public Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.color = color;
	}
}