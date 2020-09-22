package com.pyhtag.appfx.views;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.runestroicons.Runestroicons;

import com.pyhtag.appfx.ShapeGroup;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ContextSelectionView {

	private final static HBox ROOT = new HBox();
	private final static VBox BODY = new VBox();
	private final static HBox CUSTOM_CONTEXT_MENU = new HBox();
	private final static StackPane MENUITEM_DELETE_STACK = new StackPane();
	private final static StackPane MENUITEM_MORE_STACK = new StackPane();
	
	private final static Label remove = new Label("Remove",
			FontIcon.of(Runestroicons.CIRCLEDELETE, 16, Color.web("#ef5350")));
	
	private final static Label more = new Label("More",
			FontIcon.of(Runestroicons.INFO_CIRCLE, 16, Color.web("#29b6f6")));

	private final static BooleanProperty moreExpanded = new SimpleBooleanProperty(false);
	static {
		setupStyling();
		setLayouts();
	}

	private static void setLayouts() {
		ROOT.getChildren().add(BODY);
		BODY.getChildren().add(CUSTOM_CONTEXT_MENU);
		CUSTOM_CONTEXT_MENU.getChildren().addAll(MENUITEM_DELETE_STACK, MENUITEM_MORE_STACK);
		MENUITEM_DELETE_STACK.getChildren().add(remove);
		MENUITEM_MORE_STACK.getChildren().add(more);
	}

	/*
	 * The Actions that are set and ready to be performed, with the given shapeGroup
	 * being selected.
	 */
	public static void setOnActionWith(ObjectProperty<ShapeGroup> shapeGroup) {
		remove.setOnMouseEntered(mouseEvent -> {
			onMenuItemEntered(MENUITEM_DELETE_STACK, ItemAction.REMOVE);
		});
		remove.setOnMouseExited(mouseEvent -> {
			onMenuItemExited(MENUITEM_DELETE_STACK);
		});
		remove.setOnMouseClicked(mouseEvent -> {
			shapeGroup.get().setDeleteMe(true);
		});
		more.setOnMouseEntered(mouseEvent -> {
			onMenuItemEntered(MENUITEM_MORE_STACK, ItemAction.EXPLORE);
		});
		more.setOnMouseExited(mouseEvent -> {
			onMenuItemExited(MENUITEM_MORE_STACK);
		});
		more.setOnMouseClicked(mouseEvent -> {
			getMoreAbout(shapeGroup.get().getShape());
		});
		shapeGroup.addListener((o, a, n) -> {
			onShapeGroupChanged(a, n);
		});
		moreExpanded.addListener((o, a, n) -> {
			more.setText(n.booleanValue() ? "Few " : "More");
		});
	}

	private static void getMoreAbout(Node node) {
		if (moreExpanded.get()) {
			collapseWith(node);
		} else {
			expandWith(node);
		}
	}

	private static void expandWith(Node node) {
		BODY.getChildren().add(BoundsInfoViewRoot.getRootAndBindOn(node));
		moreExpanded.set(true);
	}

	private static void collapseWith(Node node) {
		BODY.getChildren().remove(BoundsInfoViewRoot.getRootAndUnbindFrom(node));
		moreExpanded.set(false);
	}

	private static void setupStyling() {
		ROOT.getStylesheets().add("com/pyhtag/appfx/css/contextSelectionView.css");
		ROOT.getStyleClass().add("root");
		BODY.getStyleClass().add("body");
		CUSTOM_CONTEXT_MENU.getStyleClass().add("context-menu");
		remove.getStyleClass().add("button-remove");
		more.getStyleClass().add("button-more");
		MENUITEM_DELETE_STACK.getStyleClass().add("menu-item-delete");
		MENUITEM_MORE_STACK.getStyleClass().add("menu-item-more");
	}

	private static void onMenuItemEntered(StackPane stack, ItemAction itemAction) {
		Bounds bounds = stack.getBoundsInParent();
		Rectangle itemSelector = new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(),
				bounds.getHeight());
		itemSelector.getStyleClass().add("item-selector");
		final double OPACITY = 0.5;
		switch (itemAction) {
		case REMOVE:
			itemSelector.setFill(Color.web("#ef5350", OPACITY));
			break;
		case EXPLORE:
			itemSelector.setFill(Color.web("#29b6f6", OPACITY));
			break;
		}
		stack.getChildren().add(0, itemSelector);
	}

	private static void onMenuItemExited(StackPane stack) {
		stack.getChildren().remove(0);
	}

	public static void onShapeGroupChanged(ShapeGroup lastSelected, ShapeGroup newSelected) {
		if (lastSelected != null) {
			((AnchorPane)lastSelected.getParent().getParent()).getChildren().remove(ROOT);
			if (moreExpanded.get()) {
				collapseWith(lastSelected.getShape());
			}
		}
		if (newSelected != null) {
			final int LAYOUT_X_OFFSET = -1;
			final int LAYOUT_Y_OFFSET = 10;
			ROOT.setLayoutX(newSelected.boundsInParentLeftLowerCornerXProperty().add(LAYOUT_X_OFFSET).get());
			newSelected.boundsInParentLeftLowerCornerXProperty().addListener((o, a, n) -> {
				ROOT.setLayoutX(n.doubleValue() + LAYOUT_X_OFFSET);
			});
			ROOT.setLayoutY(newSelected.boundsInParentLeftLowerCornerYProperty().add(LAYOUT_Y_OFFSET).get());
			newSelected.boundsInParentLeftLowerCornerYProperty().addListener((o, a, n) -> {
				ROOT.setLayoutY(n.doubleValue() + LAYOUT_Y_OFFSET);
			});
			newSelected.getParent().getChildren().add(ROOT);
		}
	}

	private static enum ItemAction {
		REMOVE, EXPLORE;
	}
}
