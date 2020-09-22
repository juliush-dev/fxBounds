package com.pyhtag.appfx.views;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pyhtag.appfx.ShapeGroup.BoundsType;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;

public class BoundsInfoViewRoot {

	private static final Logger logger = LogManager.getLogger();
	private final static Accordion root = new Accordion();
	private final static TitledPane titledPane = new TitledPane();
	private final static SplitPane titledPaneContent = new SplitPane();
	private static final BoundsinfoView boundsInLocalInfo = new BoundsinfoView(BoundsType.BOUNDS_IN_LOCAL);
	private static final BoundsinfoView boundsInParentInfo = new BoundsinfoView(BoundsType.BOUNDS_IN_PARENT);
	private static final BoundsinfoView layoutBoundsInfo = new BoundsinfoView(BoundsType.LAYOUT_BOUNDS);
	private static final ChangeListener<Bounds> boundsInLocalListener = (a, b, c) -> {
		boundsInLocalInfo.set(c.getMinX(), c.getMinY(), c.getWidth(), c.getHeight());
	};
	private static final ChangeListener<Bounds> boundsInParentListener = (a, b, c) -> {
		boundsInParentInfo.set(c.getMinX(), c.getMinY(), c.getWidth(), c.getHeight());
	};
	private static final ChangeListener<Bounds> layoutBoundsListener = (a, b, c) -> {
		layoutBoundsInfo.set(c.getMinX(), c.getMinY(), c.getWidth(), c.getHeight());
	};
	static {
		logger.traceEntry();
		titledPane.setText("Bounds Info");
		setupStyling();
		titledPane.setContent(titledPaneContent);
		titledPane.setCollapsible(false);
		titledPaneContent.getItems().addAll(boundsInLocalInfo.getRoot(), boundsInParentInfo.getRoot(),
				layoutBoundsInfo.getRoot());
		root.getPanes().add(titledPane);
		root.setExpandedPane(titledPane);
		logger.traceExit();
	}

	public final static Accordion getRootAndBindOn(Node node) {
		/* set each infoView with the actual value */
		boundsInLocalInfo.set(node.getBoundsInLocal().getMinX(), node.getBoundsInLocal().getMinY(),
				node.getBoundsInLocal().getWidth(), node.getBoundsInLocal().getHeight());
		boundsInParentInfo.set(node.getBoundsInParent().getMinX(), node.getBoundsInParent().getMinY(),
				node.getBoundsInParent().getWidth(), node.getBoundsInParent().getHeight());
		layoutBoundsInfo.set(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY(),
				node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight());
		/*
		 * add a listener for each bounds property in order to actualize the
		 * corresponding infoView whenever the bounds property changes
		 */
		node.boundsInLocalProperty().addListener(boundsInLocalListener);
		node.boundsInParentProperty().addListener(boundsInParentListener);
		node.layoutBoundsProperty().addListener(layoutBoundsListener);
		return root;
	}

	private static void setupStyling() {
		/*
		 * setting -fx-text-fill: #ececec in the css file do not work for the titledPane
		 */
		titledPane.setTextFill(Color.web("#ececec"));
	}

	public final static Accordion getRootAndUnbindFrom(Node node) {
		node.boundsInLocalProperty().removeListener(boundsInLocalListener);
		node.boundsInParentProperty().removeListener(boundsInParentListener);
		node.layoutBoundsProperty().removeListener(layoutBoundsListener);
		return root;
	}

}
