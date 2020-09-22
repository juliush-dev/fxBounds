package com.pyhtag.appfx;

import com.pyhtag.appfx.views.ContextSelectionView;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ContextSelection {

	private static ObjectProperty<ShapeGroup> shapeGroup = new SimpleObjectProperty<>();

	static {
		ContextSelectionView.setOnActionWith(shapeGroup);
	}

	public static final ShapeGroup getShapeGroup() {
		return shapeGroup.get();
	}

	public static final void select(ShapeGroup shapeGroup) {
		ContextSelection.shapeGroup.set(shapeGroup);
	}

	public static final ObjectProperty<ShapeGroup> getShapeGroupProperty() {
		return shapeGroup;
	}

}
