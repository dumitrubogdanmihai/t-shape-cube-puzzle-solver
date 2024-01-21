package com.bogdan;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import com.bogdan.renderer.RenderingEngine;
import com.bogdan.renderer.Triangle;
import com.bogdan.renderer.Vertex;

class MapRenderer {

	private RenderingEngine renderingEngine = new RenderingEngine();

	private static final Color[] COLORS = new Color[] {
			Color.WHITE,
			Color.LIGHT_GRAY,
			Color.GRAY,
			Color.DARK_GRAY,
			Color.RED,
			Color.PINK,
			Color.ORANGE,
			Color.YELLOW,
			Color.GREEN,
			Color.MAGENTA,
			Color.CYAN,
			Color.BLUE
	};

	public void render(Map map) {

		List<Triangle> triangles = new ArrayList<>();

		int min = 0 - 120 - 1;
		int max = 6 * 25 + 1 - 120;

		Color c = new Color(152, 152, 152, 125);

		triangles.add(new Triangle(new Vertex(min, min, min + 120),
				new Vertex(max, min, min + 120),
				new Vertex(min, max, min + 120),
				c));


		triangles.add(new Triangle(new Vertex(min, min, min + 120),
				new Vertex(min, min, max + 120),
				new Vertex(max, min, min + 120),
				c));

		triangles.add(new Triangle(new Vertex(min, min, min + 120),
				new Vertex(min, max, min + 120),
				new Vertex(min, min, max + 120),
				c));



		int[][][] mapMatrix = map.getMap();
		for (int i = 0; i < mapMatrix.length; i++) {
			for (int j = 0; j < mapMatrix[i].length; j++) {
				for (int k = 0; k < mapMatrix[i][j].length; k++) {
					if (mapMatrix[i][j][k] != 0) {
						int pieceIndex = mapMatrix[i][j][k];
						Color color = COLORS[pieceIndex % COLORS.length];
						triangles.addAll(renderTriangles(i, j, k, color));
					}
				}
			}
		}
		
		try {
			EventQueue.invokeAndWait(() -> renderingEngine.printTriangles(triangles));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private List<Triangle> renderTriangles(int x, int y, int z, Color color) {
		int s = 25;

		x = x * s;
		y = y * s;
		z = z * s;

		x-=120;
		y-=120;

		int xs = x + s;
		int ys = y + s;
		int zs = z + s;

		List<Triangle> triangles = new ArrayList<>();
		triangles.add(new Triangle(new Vertex(x , y , z ),
				new Vertex(xs, y , z ),
				new Vertex(x , ys, z ),
				color));
		triangles.add(new Triangle(new Vertex(xs, ys, z ),
				new Vertex(xs, y , z ),
				new Vertex(x , ys, z ),
				color));

		triangles.add(new Triangle(new Vertex(x , y , zs),
				new Vertex(xs, y , zs),
				new Vertex(x , ys, zs),
				color));
		triangles.add(new Triangle(new Vertex(xs, ys, zs),
				new Vertex(xs, y , zs),
				new Vertex(x , ys, zs),
				color));



		triangles.add(new Triangle(new Vertex(x , y , z ),
				new Vertex(x , y , zs),
				new Vertex(x , ys, z ),
				color));
		triangles.add(new Triangle(new Vertex(x , ys, zs),
				new Vertex(x , y , zs),
				new Vertex(x , ys, z),
				color));

		triangles.add(new Triangle(new Vertex(xs, y , z ),
				new Vertex(xs, y , zs),
				new Vertex(xs, ys, z ),
				color));
		triangles.add(new Triangle(new Vertex(xs, ys, zs),
				new Vertex(xs, y , zs),
				new Vertex(xs, ys, z),
				color));



		triangles.add(new Triangle(new Vertex(x , y , z ),
				new Vertex(xs, y , z ),
				new Vertex(x , y , zs),
				color));
		triangles.add(new Triangle(new Vertex(xs, y , zs),
				new Vertex(xs, y , z ),
				new Vertex(x , y , zs),
				color));

		triangles.add(new Triangle(new Vertex(x , ys, z ),
				new Vertex(xs, ys, z ),
				new Vertex(x , ys, zs),
				color));
		triangles.add(new Triangle(new Vertex(xs, ys, zs),
				new Vertex(xs, ys, z ),
				new Vertex(x , ys, zs),
				color));

		return triangles;
	}
}