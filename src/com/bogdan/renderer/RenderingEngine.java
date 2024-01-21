package com.bogdan.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class RenderingEngine {

	private double xAngle = -110;
	private double yAngle = 175;
	private double zAngle = 134;

	private JFrame frame = new JFrame();
	private JSlider zSlider = new JSlider(SwingConstants.VERTICAL, -180, 180, (int) zAngle);
	private JSlider xSlider = new JSlider(SwingConstants.HORIZONTAL, -180, 180, (int) xAngle);
	private JSlider ySlider = new JSlider(SwingConstants.VERTICAL, -180, 180, (int) yAngle);

	public RenderingEngine() {

	}
	public void printTriangles(List<Triangle> triangles) {
		frame.getContentPane().removeAll();

		Container pane = frame.getContentPane();
		pane.setLayout(new BorderLayout());

		pane.add(zSlider, BorderLayout.WEST);
		pane.add(xSlider, BorderLayout.SOUTH);
		pane.add(ySlider, BorderLayout.EAST);

		JPanel renderContainerPanel = new JPanel();
		renderContainerPanel.setLayout(new BorderLayout());
		pane.add(renderContainerPanel, BorderLayout.CENTER);

		JPanel renderPanel = new JPanel() {
			private static final long serialVersionUID = 8654356374988218552L;

			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, getWidth(), getHeight());

				List<Triangle> tris = new ArrayList<>();
				tris.addAll(triangles);

				for (int i = 0; i < 4; i++) {
					tris = inflate(tris);
				}

				double xPitch = Math.toRadians(xSlider.getValue());
				Matrix3d pitchTransform = new Matrix3d(new double[] {
						1, 0, 0,
						0, Math.cos(xPitch), -Math.sin(xPitch),
						0, Math.sin(xPitch), Math.cos(xPitch)
				});

				double yPitch = Math.toRadians(ySlider.getValue());
				Matrix3d yTransform = new Matrix3d(new double[] {
						Math.cos(yPitch), 0, Math.sin(yPitch),
						0, 1, 0,
						-Math.sin(yPitch), 0, Math.cos(yPitch)
				});


				double zPitch = Math.toRadians(zSlider.getValue());
				Matrix3d headingTransform = new Matrix3d(new double[] {
						Math.cos(zPitch), -Math.sin(zPitch), 0,
						Math.sin(zPitch), Math.cos(zPitch), 0,
						0, 0, 1,
				});

				Matrix3d transform = headingTransform.multiply(pitchTransform).multiply(yTransform);

				BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

				double[] zBuffer = new double[img.getWidth() * img.getHeight()];
				for (int q = 0; q < zBuffer.length; q++) {
					zBuffer[q] = Double.NEGATIVE_INFINITY;
				}

				for (Triangle t : tris) {
					Vertex v1 = transform.transform(t.v1);
					v1.x += getWidth() / 2;
					v1.y += getHeight() / 2;
					Vertex v2 = transform.transform(t.v2);
					v2.x += getWidth() / 2;
					v2.y += getHeight() / 2;
					Vertex v3 = transform.transform(t.v3);
					v3.x += getWidth() / 2;
					v3.y += getHeight() / 2;

					Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
					Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
					Vertex norm = new Vertex(
							ab.y * ac.z - ab.z * ac.y,
							ab.z * ac.x - ab.x * ac.z,
							ab.x * ac.y - ab.y * ac.x
							);
					double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
					norm.x /= normalLength;
					norm.y /= normalLength;
					norm.z /= normalLength;

					double angleCos = Math.abs(norm.z);

					int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
					int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
					int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
					int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

					double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

					for (int y = minY; y <= maxY; y++) {
						for (int x = minX; x <= maxX; x++) {
							double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
							double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
							double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
							if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
								double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
								int zIndex = y * img.getWidth() + x;
								if (zBuffer[zIndex] < depth) {
									img.setRGB(x, y, getShade(t.color, angleCos).getRGB());
									zBuffer[zIndex] = depth;
								}
							}
						}
					}

				}

				g2.drawImage(img, 0, 0, null);
			}
		};
		renderContainerPanel.add(renderPanel, BorderLayout.CENTER);

		xSlider.addChangeListener(e -> {
			renderPanel.repaint();
		});
		ySlider.addChangeListener(e -> {
			renderPanel.repaint();
		});
		zSlider.addChangeListener(e -> {
			renderPanel.repaint();
		});


		frame.setSize(600, 600);
		frame.setVisible(true);
	}

	private Color getShade(Color color, double shade) {
		double redLinear = Math.pow(color.getRed(), 2.4) * shade;
		double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
		double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

		int red = (int) Math.pow(redLinear, 1/2.4);
		int green = (int) Math.pow(greenLinear, 1/2.4);
		int blue = (int) Math.pow(blueLinear, 1/2.4);

		return new Color(red, green, blue);
	}

	private List<Triangle> inflate(List<Triangle> tris) {
		List<Triangle> result = new ArrayList<>();
		for (Triangle t : tris) {
			Vertex m1 = new Vertex((t.v1.x + t.v2.x)/2, (t.v1.y + t.v2.y)/2, (t.v1.z + t.v2.z)/2);
			Vertex m2 = new Vertex((t.v2.x + t.v3.x)/2, (t.v2.y + t.v3.y)/2, (t.v2.z + t.v3.z)/2);
			Vertex m3 = new Vertex((t.v1.x + t.v3.x)/2, (t.v1.y + t.v3.y)/2, (t.v1.z + t.v3.z)/2);
			result.add(new Triangle(t.v1, m1, m3, t.color));
			result.add(new Triangle(t.v2, m1, m2, t.color));
			result.add(new Triangle(t.v3, m2, m3, t.color));
			result.add(new Triangle(m1, m2, m3, t.color));
		}
		return result;
	}
}