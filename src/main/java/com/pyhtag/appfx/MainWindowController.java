package com.pyhtag.appfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class MainWindowController {

	// loggers
	private static final Logger logger = LogManager.getLogger();
	private final SceneGrid sceneGrid = new SceneGrid(50);

	@FXML
	private BorderPane root;

	public BorderPane getParent() {
		return root;
	}

	@FXML
	private StackPane ground;

	public StackPane getGround() {
		return ground;
	}

	@FXML
	private AnchorPane graphicLayer;

	public AnchorPane getGraphicLayer() {
		return graphicLayer;
	}

	@FXML
	CheckBox showGrid;
	@FXML
	CheckBox showPanelSetting;
	@FXML
	CheckBox showRelativeParent;

	@FXML
	private SplitPane splitPane;

	public SplitPane getSplitPane() {
		return splitPane;
	}

	private final MouseInteractionUtility setter = new MouseInteractionUtility();

	public MouseInteractionUtility getSetter() {
		return setter;
	}

	@FXML
	protected void initialize() {
		logger.traceEntry();
		setupStyling();
		setupLayout();
		splitPane.getItems().addListener((ListChangeListener<Node>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					double splitPaneWidth = splitPane.getWidth();
					splitPane.getDividers().get(0).setPosition(((splitPaneWidth - 5) / splitPaneWidth));
				}
			}
		});
		ContextSelection.getShapeGroupProperty().addListener((o, a, n) -> {
			if (n != null && n.isToAdd()) {
				graphicLayer.getChildren().add(n.getParent());
				centerOnTheGround(n, graphicLayer);
				n.setToAdd(false);
				n.bindRelativeParentVisibility(showRelativeParent.selectedProperty());
				n.deleteMeProperty().addListener((obsV, oldV, newV) -> {
					ContextSelection.select(null);
					if (newV) {
						graphicLayer.getChildren().remove(n.getParent());
					}
				});
			}
			App.panelSettingController.setTarget(n);
		});
		onGraphicLayerClicked();
		graphicLayer.setPadding(new Insets(Double.NEGATIVE_INFINITY));
		logger.traceExit();
	}

	private void setupStyling() {
		root.getStyleClass().add("root");
		ground.getStyleClass().add("main-layer");
	}

	private void setupLayout() {
		DoubleProperty scale = new SimpleDoubleProperty(1);
		showGrid.setSelected(false);
		sceneGrid.setGround(ground);
		sceneGrid.syncVisibility(showGrid.selectedProperty());
		showPanelSetting.setSelected(false);
		showRelativeParent.setSelected(false);
		App.panelSettingController.visibleProperty().bind(showPanelSetting.selectedProperty());
		splitPane.getDividers().addListener((ListChangeListener<Divider>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					scale.set(c.getList().get(0).getPosition());
				}
			}
		});
		splitPane.widthProperty().addListener((o, a, n) -> {
			if (App.panelSetting.isVisible()) {
				splitPane.getDividers().get(0).setPosition(
						((splitPane.getWidth() - PanelSettingController.DEFAULT_WIDTH) / splitPane.getWidth()) - 0.2);
			} else {
				splitPane.getDividers().get(0).setPosition(((splitPane.getWidth() - 5) / splitPane.getWidth()));
			}
		});
	}

	public static final void centerOnTheGround(ShapeGroup n, Pane graphicLayer) {
		double groupShapeHalfWidth = n.getShape().getBoundsInParent().getWidth() / 2;
		double groupShapeHalfHeight = n.getShape().getBoundsInParent().getHeight() / 2;
		double groundHalfWidth = graphicLayer.getWidth() / 2;
		double groundHalfHeight = graphicLayer.getHeight() / 2;
		n.getParent().setLayoutX(groundHalfWidth - groupShapeHalfWidth);
		n.getParent().setLayoutY(groundHalfHeight - groupShapeHalfHeight);
	}

	private void onGraphicLayerClicked() {
		graphicLayer.setOnMouseClicked(mouseEvent -> {
			if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
				ContextMenuController.show(graphicLayer, mouseEvent);
			}
		});
	}
}
