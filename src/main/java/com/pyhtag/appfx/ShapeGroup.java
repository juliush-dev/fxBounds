package com.pyhtag.appfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class ShapeGroup {
	private static final Logger logger = LogManager.getLogger();
	private final AnchorPane group = new AnchorPane();
	private final Shape shape;
	private final Rectangle boundsInLocal, boundsInParent, layoutBounds;
	private final static int DEFAULT_STROKE_WIDTH = 1;
	private boolean toAdd;
	private BooleanProperty shapeStrokeVisible = new SimpleBooleanProperty();
	private BooleanProperty deleteMe = new SimpleBooleanProperty(false);
	private DoubleProperty boundsInParentLeftLowerCornerX = new SimpleDoubleProperty();
	private DoubleProperty boundsInParentLeftLowerCornerY = new SimpleDoubleProperty();

	public ShapeGroup(Shape shape) {
		logger.traceEntry(new ObjectMessage(shape));
		toAdd = true;
		this.shape = shape;
		setupStyle();
		boundsInLocal = getBoundingRectangle(this.shape.boundsInLocalProperty(), BoundsType.BOUNDS_IN_LOCAL);
		boundsInParent = getBoundingRectangle(this.shape.boundsInParentProperty(), BoundsType.BOUNDS_IN_PARENT);
		layoutBounds = getBoundingRectangle(this.shape.layoutBoundsProperty(), BoundsType.LAYOUT_BOUNDS);
		group.getChildren().addAll(layoutBounds, boundsInLocal, boundsInParent, shape);
		boundsInParentLeftLowerCornerX.bind(boundsInParent.xProperty());
		boundsInParentLeftLowerCornerY.bind(boundsInParent.yProperty().add(boundsInParent.heightProperty()));
		MouseInteractionUtility.makeMovable(this);
		/* makes this group eligible for the ContextSelection */
		ContextSelection.select(this);
		this.shape.setOnMouseClicked(mouseEvent -> {
			ContextSelection.select(this);
		});
		logger.traceExit();
	}

	private void setupStyle() {
		group.getStyleClass().add("group-root");
		shape.getStyleClass().add("group-shape");
	}

	private Rectangle getBoundingRectangle(ReadOnlyProperty<Bounds> bounds, BoundsType boundsType) {
		logger.traceEntry("Bounds(type: {}; context: {})", boundsType.getStyleClass(),
				BoundsContext.STROKE_CONTEXT.getContext());
		Bounds boundsValue = bounds.getValue();
		var boundingRectangle = new Rectangle(boundsValue.getMinX(), boundsValue.getMinY(), boundsValue.getWidth(),
				boundsValue.getHeight());
		String styleClass = boundsType.getStyleClass() + "-" + BoundsContext.STROKE_CONTEXT.getContext();
		logger.debug("style Class: {}", styleClass);
		boundingRectangle.getStyleClass().add(styleClass);
		/* boundingRectangle's update logic */
		bounds.addListener((o, a, n) -> {
			boundingRectangle.setX(n.getMinX());
			boundingRectangle.setY(n.getMinY());
			boundingRectangle.setWidth(n.getWidth());
			boundingRectangle.setHeight(n.getHeight());
		});
		return logger.traceExit(boundingRectangle);
	}

	/**
	 * Makes the visibility of bounding rectangle to depend on a particular boolean
	 * property. That means whenever the value of that property is true, the
	 * bounding rectangle is visible. Otherwise, it is not.
	 * 
	 * @param boundsType The bounds whose visibility depends on the trigger
	 * @param trigger    The trigger to depend on
	 */
	public void bindBoundingRectangleVisibilityTo(BoundsType boundsType, BooleanProperty trigger) {
		logger.traceEntry(boundsType.name());
		switch (boundsType) {
		case BOUNDS_IN_LOCAL:
			boundsInLocal.visibleProperty().bind(trigger);
			break;
		case BOUNDS_IN_PARENT:
			boundsInParent.visibleProperty().bind(trigger);
			break;
		case LAYOUT_BOUNDS:
			layoutBounds.visibleProperty().bind(trigger);
			break;
		default:
			shapeStrokeVisible.bind(trigger);
			shapeStrokeVisible.addListener((o, a, n) -> {
				shape.setStrokeWidth(n ? DEFAULT_STROKE_WIDTH : 0);
			});
			break;
		}
		logger.traceExit();
	}

	public void bindRelativeParentVisibility(BooleanProperty trigger) {
		if (trigger.get()) {
			group.getStyleClass().add("group-parent");
		}
		trigger.addListener((obsV, oldV, newV) -> {
			if (newV) {
				group.getStyleClass().add("group-parent");
			} else {
				group.getStyleClass().remove("group-parent");
			}
		});
	}

	public void bindShapeTransformsTo(DoubleProperty... RTxySxy) {
		logger.traceEntry("RTxySxy size: {}.", RTxySxy.length);
		shape.rotateProperty().bind(RTxySxy[0]);
		shape.translateXProperty().bind(RTxySxy[1]);
		shape.translateYProperty().bind(RTxySxy[2]);
		shape.scaleXProperty().bind(RTxySxy[3]);
		shape.scaleYProperty().bind(RTxySxy[4]);
		logger.traceExit();
	}

	public void unbindStrokeVisiblity() {
		shapeStrokeVisible.unbind();
	}

	// getters and setters
	public final Shape getShape() {
		return shape;
	}

	public final Rectangle getBoundsInLocal() {
		return boundsInLocal;
	}

	public final Rectangle getBoundsInParent() {
		return boundsInParent;
	}

	public final Rectangle getLayoutBounds() {
		return layoutBounds;
	}

	public final AnchorPane getParent() {
		return group;
	}

	public final boolean isToAdd() {
		return toAdd;
	}

	public final void setToAdd(boolean toAdd) {
		this.toAdd = toAdd;
	}

	public final DoubleProperty boundsInParentLeftLowerCornerXProperty() {
		return boundsInParentLeftLowerCornerX;
	}

	public final DoubleProperty boundsInParentLeftLowerCornerYProperty() {
		return boundsInParentLeftLowerCornerY;
	}

	// Nested classes definition
	public static enum BoundsType {
		BOUNDS_IN_LOCAL("bounds-in-local"), BOUNDS_IN_PARENT("bounds-in-parent"), LAYOUT_BOUNDS("layout-bounds"),
		STROKE("stroke");

		private String styleClass;

		BoundsType(String styleClass) {
			this.styleClass = styleClass;
		}

		public String getStyleClass() {
			return styleClass;
		}
	}

	public static enum BoundsContext {
		STROKE_CONTEXT("stroke-context"), BACKGROUND_CONTEXT("background-context");

		private String context;

		BoundsContext(String context) {
			this.context = context;
		}

		public String getContext() {
			return context;
		}

	}

	public void setDeleteMe(boolean value) {
		deleteMe.set(value);
	}

	public BooleanProperty deleteMeProperty() {
		return deleteMe;
	}

}
