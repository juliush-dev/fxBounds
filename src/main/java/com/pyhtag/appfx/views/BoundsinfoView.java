package com.pyhtag.appfx.views;

import com.pyhtag.appfx.ShapeGroup.BoundsContext;
import com.pyhtag.appfx.ShapeGroup.BoundsType;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BoundsinfoView {

	private Label title = new Label();
	private Label minX = new Label(), minXvalue = new Label();
	private Label minY = new Label(), minYvalue = new Label();
	private Label width = new Label(), widthValue = new Label();
	private Label height = new Label(), heightValue = new Label();

	private HBox infoItem1 = new HBox();
	private HBox infoItem2 = new HBox();
	private HBox infoItem3 = new HBox();
	private HBox infoItem4 = new HBox();

	private VBox container = new VBox();
	private VBox infoItemList = new VBox();
	private final AnchorPane root = new AnchorPane();

	{
		setStyling();
		root.getChildren().add(container);
		container.getChildren().addAll(title, infoItemList);
		infoItemList.getChildren().addAll(infoItem1, infoItem2, infoItem3, infoItem4);
		infoItem1.getChildren().addAll(minX, minXvalue);
		infoItem2.getChildren().addAll(minY, minYvalue);
		infoItem3.getChildren().addAll(width, widthValue);
		infoItem4.getChildren().addAll(height, heightValue);
		minX.setText("MinX: ");
		minXvalue.setText("0");
		minY.setText("MinY: ");
		minYvalue.setText("0");
		width.setText("Width: ");
		widthValue.setText("0");
		height.setText("Height: ");
		heightValue.setText("0");
	}

	public BoundsinfoView(BoundsType boundsType) {
		switch (boundsType) {
		case BOUNDS_IN_LOCAL:
			title.setText("Bounds In Local");
			break;
		case BOUNDS_IN_PARENT:
			title.setText("Bounds In Parent");
			break;
		default:
			title.setText("Layout Bounds");
			break;
		}
		final String styleClass = boundsType.getStyleClass() + "-" + BoundsContext.BACKGROUND_CONTEXT.getContext();
		container.getStyleClass().add(styleClass);
	}

	private void setStyling() {
		root.getStyleClass().add("boundsInfoView");
		title.getStyleClass().add("info-title");
		/* Label styling */
	}

	public AnchorPane getRootAndBindOn(Node node) {
		return root;
	}

	public AnchorPane getRootAndUnbindFrom(Node node) {
		return root;
	}

	/* getters and setters */

	public AnchorPane getRoot() {
		return root;
	}

	public void set(double minX, double minY, double width, double height) {
		minXvalue.setText(String.format("%.1f", minX));
		minYvalue.setText(String.format("%.1f", minY));
		widthValue.setText(String.format("%.1f", width));
		heightValue.setText(String.format("%.1f", height));
	}
}
