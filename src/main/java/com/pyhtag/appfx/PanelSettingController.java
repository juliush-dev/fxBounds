package com.pyhtag.appfx;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.runestroicons.Runestroicons;

import com.pyhtag.appfx.ShapeGroup.BoundsType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class PanelSettingController {

	private static final Logger logger = LogManager.getLogger();
	private static final Node panelIcon = FontIcon.of(Runestroicons.WP_COG_O, 30, Color.web("#2296F3"));
	private static final String DEFAULT_STRING_FORMAT = "%.1f";
	private static final int TOOLTIP_WIDTH = 360;
	public static final double DEFAULT_WIDTH = 367;
	private ObjectProperty<ShapeGroup> shapeGroup = new SimpleObjectProperty<>();
	private static final Color DEFAULT = Color.LIGHTGRAY;
	private Tooltip strokeTooltip = new Tooltip(TOOLTIP_TEXT.STROKE_TEXT.toString());
	private Tooltip boundsInLocalTooltip = new Tooltip(TOOLTIP_TEXT.BOUNDS_IN_LOCAL_TEXT.toString());
	private Tooltip boundsInParentTooltip = new Tooltip(TOOLTIP_TEXT.BOUNDS_IN_PARENT_TEXT.toString());
	private Tooltip layoutBoundsTooltip = new Tooltip(TOOLTIP_TEXT.LAYOUT_BOUNDS_TEXT.toString());

	@FXML
	private Separator separator;
	@FXML
	private AnchorPane root;
	@FXML
	private AnchorPane header;
	@FXML
	private HBox headerHBox;

	@FXML
	private Label headerTitle;
	@FXML
	private AnchorPane body;
	@FXML
	private Label optionSection;
	@FXML
	private Label transformSection;

	@FXML
	private CheckBox showStroke;

	public CheckBox getShowStroke() {
		return showStroke;
	}

	@FXML
	private CheckBox showBoundsInLocal;

	public CheckBox getShowBoundsInLocal() {
		return showBoundsInLocal;
	}

	@FXML
	private CheckBox showBoundsInParent;

	public CheckBox getShowBoundsInParent() {
		return showBoundsInParent;
	}

	@FXML
	private CheckBox showLayoutBounds;

	public CheckBox getShowLayoutBounds() {
		return showLayoutBounds;
	}

	@FXML
	private Slider setRotate;

	public Slider getSetRotate() {
		return setRotate;
	}

	@FXML
	private Label rotateValue;

	@FXML
	private Slider setTranslateX;

	public Slider getSetTranslateX() {
		return setTranslateX;
	}

	@FXML
	private Label translateXvalue;

	@FXML
	private Slider setTranslateY;

	public Slider getSetTranslateY() {
		return setTranslateY;
	}

	@FXML
	private Label translateYvalue;

	@FXML
	private Slider setScaleX;

	public Slider getSetScaleX() {
		return setScaleX;
	}

	@FXML
	private Label scaleXvalue;

	@FXML
	private Slider setScaleY;

	public Slider getSetScaleY() {
		return setScaleY;
	}

	@FXML
	private Label scaleYvalue;

	// is expanded attribute
	private BooleanProperty visible = new SimpleBooleanProperty(false);

	public boolean isVisible() {
		return visible.get();
	}

	public void setVisible(boolean value) {
		visible.set(value);
	}

	public BooleanProperty visibleProperty() {
		return visible;
	}

	private List<CheckBox> checkboxList;

	public List<CheckBox> getCheckboxList() {
		return checkboxList;
	}

	private List<Slider> sliderList;

	public List<Slider> getSliderList() {
		return sliderList;
	}

	private List<Tooltip> tooltipList;

	public List<Tooltip> getTooltipList() {
		return tooltipList;
	}

	@FXML
	protected void initialize() {
		logger.traceEntry();
		hideRoot();
		headerHBox.getChildren().add(0, panelIcon);
		root.maxWidthProperty().bind(root.minWidthProperty());
		checkboxList = Arrays.asList(showStroke, showBoundsInLocal, showBoundsInParent, showLayoutBounds);
		sliderList = Arrays.asList(setRotate, setTranslateX, setTranslateY, setScaleX, setScaleY);
		tooltipList = Arrays.asList(strokeTooltip, boundsInLocalTooltip, boundsInParentTooltip, layoutBoundsTooltip);
		setupPanelSetting();
		setupOnVisible();
		syncPanelWithNewSelectedShapeGroup();
		setupStyling();
		logger.traceExit();
	}

	private void setupStyling() {
		root.getStylesheets().add("com/pyhtag/appfx/css/panelSetting.css");
		root.setId("root");
		header.setId("header");
		headerHBox.setId("header-hbox");
		headerTitle.setId("header-title");
		body.setId("body");
		separator.getStyleClass().add("header-separator");

		optionSection.getStyleClass().add("section");
		transformSection.getStyleClass().add("section");

		showStroke.getStyleClass().add("show-stroke");
		showStroke.getGraphic().getStyleClass().add("stroke-selected-graphic");

		showBoundsInLocal.getStyleClass().add("show-bounds-in-local");
		((FontIcon) showBoundsInLocal.getGraphic()).getStyleClass().add("bounds-in-local-selected-graphic");

		showBoundsInParent.getStyleClass().add("show-bounds-in-parent");
		((FontIcon) showBoundsInParent.getGraphic()).getStyleClass().add("bounds-in-parent-selected-graphic");

		showLayoutBounds.getStyleClass().add("show-layout-bounds");
		((FontIcon) showLayoutBounds.getGraphic()).getStyleClass().add("layout-bounds-selected-graphic");
	}

	private void setupOnVisible() {
		visible.addListener((o, a, n) -> {
			logger.debug("Panel setting visible: {}", n);
			if (n.booleanValue()) {
				if (shapeGroup.get() == null) {
					disableSetting(n.booleanValue());
					show();
				} else {
					show();
				}
			} else {
				hide();
			}

		});
	}

	private void hide() {
		root.setMinWidth(0);
		double splitPaneWidth = App.mainWindowController.getSplitPane().getWidth();
		App.mainWindowController.getSplitPane().getDividers().get(0).setPosition((splitPaneWidth - 5) / splitPaneWidth);
	}

	private void show() {
		root.setMinWidth(DEFAULT_WIDTH);
		double splitPaneWidth = App.mainWindowController.getSplitPane().getWidth();
		App.mainWindowController.getSplitPane().getDividers().get(0)
				.setPosition(((splitPaneWidth - App.panelSetting.getWidth()) / splitPaneWidth) - 0.2);
	}

	public static enum SliderContext {
		ROTATE, TRANSLATE, SCALE;
	}

	private void setUpSlider(SliderContext context, Slider slider) {
		switch (context) {
		case ROTATE:
			slider.setMin(0);
			slider.setMax(360);
			slider.setValue(0);
			slider.setMinorTickCount(100);
			break;
		case TRANSLATE:
			slider.setMin(-100);
			slider.setMax(100);
			slider.setValue(0);
			break;
		case SCALE:
			slider.setMin(0.5);
			slider.setMax(3);
			slider.setValue(1);
		default:
			break;
		}
	}

	private void setupPanelSetting() {
		/* sliders setup */
		for (int i = 0; i < sliderList.size(); i++) {
			if (i == 0) {
				setUpSlider(SliderContext.ROTATE, sliderList.get(i));
			} else if (i < 3) {
				setUpSlider(SliderContext.TRANSLATE, sliderList.get(i));
			} else {
				setUpSlider(SliderContext.SCALE, sliderList.get(i));
			}
		}

		for (var checkbox : checkboxList) {
			checkbox.setSelected(false);
			checkbox.setGraphic(FontIcon.of(Runestroicons.OK, DEFAULT));
			checkbox.setContentDisplay(ContentDisplay.RIGHT);
		}

		for (var tooltip : tooltipList) {
			tooltip.setMaxWidth(TOOLTIP_WIDTH);
			tooltip.setGraphic(FontIcon.of(Runestroicons.INFO_CIRCLE, 30, Color.CORNFLOWERBLUE));
			tooltip.setContentDisplay(ContentDisplay.LEFT);
			tooltip.setFont(Font.font(14));
			tooltip.setWrapText(true);
			tooltip.setShowDelay(Duration.millis(500));
			tooltip.setShowDuration(Duration.minutes(2));
		}

		showStroke.setTooltip(strokeTooltip);
		showBoundsInLocal.setTooltip(boundsInLocalTooltip);
		showBoundsInParent.setTooltip(boundsInParentTooltip);
		showLayoutBounds.setTooltip(layoutBoundsTooltip);
		bindLabelsValue();
	}

	private void bindLabelsValue() {
		rotateValue.textProperty().bind(setRotate.valueProperty().asString(DEFAULT_STRING_FORMAT));
		translateXvalue.textProperty().bind(setTranslateX.valueProperty().asString(DEFAULT_STRING_FORMAT));
		translateYvalue.textProperty().bind(setTranslateY.valueProperty().asString(DEFAULT_STRING_FORMAT));
		scaleXvalue.textProperty().bind(setScaleX.valueProperty().asString(DEFAULT_STRING_FORMAT));
		scaleYvalue.textProperty().bind(setScaleY.valueProperty().asString(DEFAULT_STRING_FORMAT));
	}

	public void setTarget(ShapeGroup shapeGroup) {
		this.shapeGroup.set(shapeGroup);
	}

	private void syncPanelWithNewSelectedShapeGroup() {
		logger.traceExit();
		shapeGroup.addListener((obs, oldValue, newValue) -> {
			if (oldValue != null) {
				setFree(oldValue);
			}
			if (newValue != null) {
				disableSetting(false);
//				syncCheckBoxGraphics(newValue);
				setNewSlidersValue(newValue);
				setNewCheckBoxsValue(newValue);
				syncSliders(newValue);
				syncCheckBoxes(newValue);
			} else {
				disableSetting(true);
			}
		});
		logger.traceExit();
	}

	/*
	 * Once the checbox's values have been updated according to the attribute's
	 * values of the new selected shape group, the checkboxes are set as
	 * dependencies for the attributes's values that should depend on them. So that
	 * interaction on each checkbox will replicate on the target shapeGroup's
	 * attributes values.
	 */
	private void syncCheckBoxes(ShapeGroup newValue) {
		newValue.bindBoundingRectangleVisibilityTo(BoundsType.STROKE, showStroke.selectedProperty());
		newValue.bindBoundingRectangleVisibilityTo(BoundsType.BOUNDS_IN_LOCAL, showBoundsInLocal.selectedProperty());
		newValue.bindBoundingRectangleVisibilityTo(BoundsType.BOUNDS_IN_PARENT, showBoundsInParent.selectedProperty());
		newValue.bindBoundingRectangleVisibilityTo(BoundsType.LAYOUT_BOUNDS, showLayoutBounds.selectedProperty());
	}

	/*
	 * Once the sliders's values have been updated according to the attribute's
	 * values of the new selected shape group, the sliders are set as dependencies
	 * for the attributes's values that should depend on them. So that interaction
	 * on each slider will replicate on the target shapeGroup's attributes values.
	 */
	private void syncSliders(ShapeGroup newValue) {
		newValue.bindShapeTransformsTo(setRotate.valueProperty(), setTranslateX.valueProperty(),
				setTranslateY.valueProperty(), setScaleX.valueProperty(), setScaleY.valueProperty());
	}

	/*
	 * Whenever a new shape group is selected, it updates the checkboxes with the
	 * values of its attributes that depend on a checkbox selection.
	 */
	private void setNewCheckBoxsValue(ShapeGroup newValue) {
		showStroke.setSelected(newValue.getShape().getStrokeWidth() > 0);
		showBoundsInLocal.setSelected(newValue.getBoundsInLocal().isVisible());
		showBoundsInParent.setSelected(newValue.getBoundsInParent().isVisible());
		showLayoutBounds.setSelected(newValue.getLayoutBounds().isVisible());
	}

	/*
	 * Whenever a new shape group is selected, it updates the sliders with the
	 * values of its attributes that depends on a slider.
	 */
	private void setNewSlidersValue(ShapeGroup newValue) {
		getSetRotate().setValue(newValue.getShape().getRotate());
		getSetTranslateX().setValue(newValue.getShape().getTranslateX());
		getSetTranslateY().setValue(newValue.getShape().getTranslateY());
		getSetScaleX().setValue(newValue.getShape().getScaleX());
		getSetScaleY().setValue(newValue.getShape().getScaleY());
	}

//	/*
//	 * Whenever a new shape group is selected, it updates checkBox graphics with the
//	 * color of the corresponding bounding rectangle strokes that.
//	 */
//	private void syncCheckBoxGraphics(ShapeGroup newValue) {
//		showStroke.selectedProperty().addListener((o, a, n) -> {
//			if (n) {
//				((FontIcon) showStroke.getGraphic()).setIconColor(newValue.getShape().getStroke());
//				showStroke.getGraphic().getStyleClass().remove("disable-stroke");
//				showStroke.getGraphic().getStyleClass().add("enable-stroke");
//			} else {
//				((FontIcon) showStroke.getGraphic()).setIconColor(DEFAULT);
//			}
//		});
//		showBoundsInLocal.selectedProperty().addListener((o, a, n) -> {
//			if (n) {
//				((FontIcon) showBoundsInLocal.getGraphic()).setIconColor(newValue.getBoundsInLocal().getStroke());
//			} else {
//				((FontIcon) showBoundsInLocal.getGraphic()).setIconColor(DEFAULT);
//			}
//		});
//		showBoundsInParent.selectedProperty().addListener((o, a, n) -> {
//			if (n) {
//				((FontIcon) showBoundsInParent.getGraphic()).setIconColor(newValue.getBoundsInParent().getStroke());
//			} else {
//				((FontIcon) showBoundsInParent.getGraphic()).setIconColor(DEFAULT);
//			}
//		});
//		showLayoutBounds.selectedProperty().addListener((o, a, n) -> {
//			if (n) {
//				((FontIcon) showLayoutBounds.getGraphic()).setIconColor(newValue.getLayoutBounds().getStroke());
//			} else {
//				((FontIcon) showLayoutBounds.getGraphic()).setIconColor(DEFAULT);
//			}
//		});
//	}

	/*
	 * The previous selected shape group should always be set free whenever a new
	 * shape group is selected. Doing so avoid interaction with checkBoxes and
	 * sliders replicate on both the old selected and the new selected shape group.
	 */
	private void setFree(ShapeGroup oldValue) {
		oldValue.getShape().rotateProperty().unbind();
		oldValue.getShape().translateXProperty().unbind();
		oldValue.getShape().translateYProperty().unbind();
		oldValue.getShape().scaleXProperty().unbind();
		oldValue.getShape().scaleYProperty().unbind();
		oldValue.unbindStrokeVisiblity();
		oldValue.getBoundsInLocal().visibleProperty().unbind();
		oldValue.getBoundsInParent().visibleProperty().unbind();
		oldValue.getLayoutBounds().visibleProperty().unbind();
	}

	enum TOOLTIP_TEXT {
		STROKE_TEXT("Represents the stroke of the shape."),
		BOUNDS_IN_LOCAL_TEXT(
				"Each Node has a read-only boundsInLocal variable which specifies the bounding rectangle of the Node in untransformed local coordinates. boundsInLocal includes the Node's shape geometry, including any space required for a non-zero stroke that may fall outside the local position/size variables, and its clip and effect variables."),
		BOUNDS_IN_PARENT_TEXT(
				"Each Node also has a read-only boundsInParent variable which specifies the bounding rectangle of the Node after all transformations have been applied, including those set in transforms, scaleX/scaleY, rotate, translateX/translateY, and layoutX/layoutY. It is called \\\"boundsInParent\\\" because the rectangle will be relative to the parent's coordinate system. This is the 'visual' bounds of the node."),
		LAYOUT_BOUNDS_TEXT(
				"Finally, the layoutBounds variable defines the rectangular bounds of the Node that should be used as the basis for layout calculations and may differ from the visual bounds of the node. For shapes, Text, and ImageView, layoutBounds by default includes only the shape geometry, including space required for a non-zero strokeWidth, but does not include the effect, clip, or any transforms. For resizable classes (Regions and Controls) layoutBounds will always map to 0,0 width x height.");

		private String text;

		private TOOLTIP_TEXT(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}

	}

	private void disableSetting(boolean value) {
		for (CheckBox c : getCheckboxList()) {
			c.setSelected(false);
			((FontIcon) c.getGraphic()).setFill(DEFAULT);
			c.setDisable(value);
		}
		for (Slider s : getSliderList()) {
			double minValue = s.getMin();
			s.setValue(minValue < 0 ? 0 : (minValue < 1 ? 1 : 0));
			s.setDisable(value);
		}
	}

	/*
	 * When the root get added on the splitPane for the first time, it should not be
	 * visible (expanded) and the divider should be set at position 1 so that the
	 * side containing this root is hidden
	 */
	public void hideRoot() {
		root.parentProperty().addListener((obsV, oldV, newV) -> {
			if (newV != null) {
				root.setMinWidth(0);
			}
		});
	}

}
