package com.pyhtag.appfx;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.ObjectMessage;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Screen;

public class SceneGrid {

	// logger
	private static final Logger logger = LogManager.getLogger();
	private static final Marker SCENE_GRID_CONTRUCTION = MarkerManager.getMarker("SCENE_GRID_CONTRUCTION");
	private static final Marker LINE_CREATION = MarkerManager.getMarker("LINE_CREATION")
			.setParents(SCENE_GRID_CONTRUCTION);

	//
	private static final int VLINE_Y_COORDINATE = 0;
	private static final int HLINE_X_COORDINATE = 0;
	private static final double LINE_WIDTH = 0.1;
	//
	private final AnchorPane gridLayer = new AnchorPane();
	private StackPane ground;
	private Rectangle2D gridBound;

	private double unitLength;

	public double getUnitLength() {
		return unitLength;
	}

	public void setUnitLength(double unitLength) {
		this.unitLength = unitLength;
	}

	private boolean isDraw = false;

	public boolean isDraw() {
		return isDraw;
	}

	public SceneGrid(double unitLength) {
		this.unitLength = unitLength;
		gridBound = getMaxScreen().getBounds();
		logger.debug("gridBound Width: {}", gridBound.getWidth());
		logger.debug("gridBound Height: {}", gridBound.getHeight());
		gridLayer.setPadding(new Insets(-800));
		draw();
	}

	private Screen getMaxScreen() {
		return Screen.getScreens().stream().max(Comparator.comparingDouble(new ToDoubleFunction<Screen>() {
			@Override
			public double applyAsDouble(Screen value) {
				return value.getBounds().getHeight() * value.getBounds().getWidth();
			}
		})).get();
	}

	private void draw() {
		logger.traceEntry();
		if (gridLayer.getChildren() != null) {
			gridLayer.getChildren().clear();
		}
		drawGridOnVSegment(new VSegment(gridBound.getHeight()));
		drawGridOnHSegment(new HSegment(gridBound.getWidth()));
		isDraw = true;
		logger.traceExit();
	}

	private void drawGridOnHSegment(Segment segment) {
		logger.traceEntry(new ObjectMessage(segment));
		if (segment.length() < unitLength) {
			return;
		}
		Separator vSep = new Separator(Orientation.VERTICAL);
		vSep.setLayoutY(VLINE_Y_COORDINATE);
		vSep.setLayoutX(segment.middle());
		Line vline = new Line();
		vline.setStrokeWidth(LINE_WIDTH);
		vline.setStroke(Color.GHOSTWHITE);
		vline.setStartX(segment.middle());
		vline.setEndX(segment.middle());
		vline.setStartY(VLINE_Y_COORDINATE);
		vline.setEndY(gridBound.getHeight());
		logger.debug(LINE_CREATION, "New vertical line At (x: {}, y: {})", segment.middle(), VLINE_Y_COORDINATE);
		gridLayer.getChildren().add(vline);
		drawGridOnHSegment(segment.getLeftPart());
		drawGridOnHSegment(segment.getRightPart());
		logger.traceExit();
	}

	private void drawGridOnVSegment(Segment segment) {
		logger.traceEntry(new ObjectMessage(segment));
		if (segment.length() < unitLength) {
			return;
		}
		Line hline = new Line();
		hline.setStrokeWidth(LINE_WIDTH);
		hline.setStroke(Color.GHOSTWHITE);
		hline.setStartX(HLINE_X_COORDINATE);
		hline.setEndX(gridBound.getWidth());
		hline.setStartY(segment.middle());
		hline.setEndY(segment.middle());
		logger.debug(LINE_CREATION, "New vertical line At (x: {}, y: {})", segment.middle(), VLINE_Y_COORDINATE);
		gridLayer.getChildren().add(hline);
		drawGridOnVSegment(segment.getLeftPart());
		drawGridOnVSegment(segment.getRightPart());
		logger.traceExit();
	}

	abstract class Segment {
		protected DoubleProperty start = new SimpleDoubleProperty();
		protected DoubleProperty end = new SimpleDoubleProperty();

		protected Segment(double start, double end) {
			this.start.set(start);
			this.end.set(end);
		}

		protected Segment(double length) {
			this.end.set(length);
		}

		public double middle() {
			return (end.get() + start.get()) / 2;
		}

		public double thirdPart() {
			return (end.get() + start.get()) / 3;
		}

		public double length() {
			return end.get() - start.get();
		}

		public abstract Segment getLeftPart();

		public abstract Segment getRightPart();

		@Override
		public String toString() {
			return String.format("Segment= {start: %f; end: %f; length: %f; middle: %f}", start.get(), end.get(),
					this.length(), this.middle());
		}
	}

	class HSegment extends Segment {

		public HSegment(double startX, double endX) {
			super(startX, endX);
		}

		public HSegment(double length) {
			super(length);
		}

		public double startX() {
			return start.get();
		}

		public double endX() {
			return end.get();
		}

		@Override
		public Segment getLeftPart() {
			return new HSegment(this.startX(), this.middle());
		}

		@Override
		public Segment getRightPart() {
			return new HSegment(this.middle(), this.endX());
		}
	}

	class VSegment extends Segment {

		public VSegment(double startY, double endY) {
			super(startY, endY);
		}

		public VSegment(double length) {
			super(length);
		}

		public double startY() {
			return start.get();
		}

		public double endY() {
			return end.get();
		}

		@Override
		public Segment getLeftPart() {
			return new HSegment(this.startY(), this.middle());
		}

		@Override
		public Segment getRightPart() {
			return new HSegment(this.middle(), this.endY());
		}
	}

	public void syncVisibility(BooleanProperty selectedProperty) {
		if (ground == null)
			throw new Error(
					"Ground not set. The method sceneGridObject.setGround(StackPane) must be called before\n scneGridObject.syncVisibility(BooleanProperty).");
		gridLayer.visibleProperty().bind(selectedProperty);
	}

	public void setGround(StackPane ground) {
		this.ground = ground;
		this.ground.getChildren().add(0, gridLayer);
	}
}