package com.pyhtag.appfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.layout.AnchorPane;

public class MouseInteractionUtility {
	private static final Logger logger = LogManager.getLogger();
	private static double mouseX, mouseY;

	public static void makeMovable(ShapeGroup group) {
		Delta delta = new Delta();
		group.getShape().setOnMousePressed(mouseEvent -> {
			mouseX = mouseEvent.getSceneX();
			mouseY = mouseEvent.getSceneY();
		});

		group.getShape().setOnMouseDragged(mouseEvent -> {
			delta.x = mouseEvent.getSceneX() - mouseX;
			delta.y = mouseEvent.getSceneY() - mouseY;
			group.getParent().relocate(group.getParent().getLayoutX() + delta.x,
					group.getParent().getLayoutY() + delta.y);
			mouseX = mouseEvent.getSceneX();
			mouseY = mouseEvent.getSceneY();
		});

		group.getShape().setOnMouseReleased(mouseEvent -> {
			logger.debug("Mouse released");
//			relocateOntheGround(group, App.mainWindowController.getGraphicLayer());
		});

	}

	public static void relocateOntheGround(ShapeGroup group, AnchorPane ground) {
		if (group != null && ground != null) {
			double groupShapeMinX = group.getShape().getBoundsInParent().getMinX();
			double groupShapeMinY = group.getShape().getBoundsInParent().getMinY();
			double groupShapeWidth = group.getShape().getBoundsInParent().getWidth();
			double groupShapeHeight = group.getShape().getBoundsInParent().getHeight();

			double groupParentMinX = group.getParent().getBoundsInParent().getMinX();
			double groupParentMinY = group.getParent().getBoundsInParent().getMinY();

			double groundMinX = ground.getLayoutBounds().getMinX();
			double groundMinY = ground.getLayoutBounds().getMinY();
			double groundWidth = ground.getLayoutBounds().getWidth();
			double groundHeight = ground.getLayoutBounds().getHeight();
			logger.debug("GroundMinX: {}; GroundMinY: {}; GroundWidth: {}; GroundHeight: {}.", groundMinX, groundMinY,
					groundWidth, groundHeight);
			/*
			 * position of the shape's bounds in parent relative to the graphic layer
			 */ double leftTopCornerX = groupParentMinX + groupShapeMinX;
			double leftTopCornerY = groupParentMinY + groupShapeMinY;
			double leftBottomCornerY = leftTopCornerY + groupShapeHeight;
			double rightTopCornerX = leftTopCornerX + groupShapeWidth;
			// stroke offset
			double strokeOffset = group.getShape().getStrokeWidth();
			double offsetX = 0;
			double offsetY = 0;
			// out left
			if (leftTopCornerX < groundMinX) {
				offsetX = strokeOffset;
				logger.debug("Out left. OffsetX: {}", offsetX);
				group.getParent().setLayoutX(offsetX);
			}
			// out right
			if (rightTopCornerX > groundWidth) {
				offsetX = groundWidth - (rightTopCornerX - leftTopCornerX);
				offsetX = offsetX < 0 ? offsetX : -offsetX;
				logger.debug("Out right. OffsetX: {}", -offsetX);
				group.getParent().setLayoutX(-offsetX);
			}
			// top
			if (leftTopCornerY < groundMinY) { // < 0
				offsetY = strokeOffset;
				logger.debug("Out top. OffsetY: {}", offsetY);
				group.getParent().setLayoutY(offsetY);
			}
			// out bottom
			if (leftBottomCornerY > groundHeight) {
				offsetY = groundHeight - (leftBottomCornerY - leftTopCornerY);
				offsetY = offsetY < 0 ? offsetY : -offsetY;
				logger.debug("Out bottom. OffsetY: {}", offsetY);
				group.getParent().setLayoutY(-offsetY);
			}
		}
	}

	// intern classes definition

	private static class Delta {
		double x, y;

		@Override
		public String toString() {
			return String.format("Alpha {x: %.1f, y: %.1f}", x, y);
		}
	}

}
