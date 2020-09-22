package com.pyhtag.appfx;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.runestroicons.Runestroicons;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class ContextMenuController {

	private static final ContextMenu graphicLayerContextMenu = new ContextMenu();
	private static final int ICON_SIZE = 16;
	private static final MenuItem newRectangle;
	private static final MenuItem newCircle;
	private static final MenuItem newLine;
	private static final MenuItem newPolygon;

	static {
		newRectangle = new MenuItem("Rectangle", FontIcon.of(Runestroicons.SQUARE, ICON_SIZE));
		newRectangle.setOnAction(actionEvent -> {
			newRectangle();
		});
		newCircle = new MenuItem("Circle", FontIcon.of(Runestroicons.CIRCLE, ICON_SIZE));
		newCircle.setOnAction(actionEvent -> {
			newCircle();
		});

		newLine = new MenuItem("Line", FontIcon.of(Runestroicons.ZURB, ICON_SIZE));
		newLine.setOnAction(actionEvent -> {
			newLine();
		});
		newPolygon = new MenuItem("Polygon", FontIcon.of(Runestroicons.ANGULAR, ICON_SIZE));
		newPolygon.setOnAction(actionEvent -> {
			newPolygon();
		});
		graphicLayerContextMenu.getItems().addAll(newRectangle, newCircle, newLine, newPolygon);
	}

	public static void show(Node anchor, MouseEvent actionEvent) {
		double dx = actionEvent.getX();
		double dy = actionEvent.getY();
		graphicLayerContextMenu.show(anchor, Side.TOP, dx, dy);
		anchor.setOnMousePressed(mouseEvent -> {
			if (graphicLayerContextMenu.isShowing()) {
				graphicLayerContextMenu.hide();
			}
		});
	}

	private static void newRectangle() {
		new ShapeGroup(new Rectangle(150, 100));
	}

	private static void newCircle() {
		new ShapeGroup(new Circle(50));
	}

	private static void newLine() {
		new ShapeGroup(new Line(0, 0, 80, 0));
	}

	private static void newPolygon() {
		Polygon polygon = new Polygon();
		polygon.getPoints().addAll(new Double[] { 0.0, 0.0, 80.0, 100.0, 10.0, 80.0 });
		new ShapeGroup(polygon);
	}

}
