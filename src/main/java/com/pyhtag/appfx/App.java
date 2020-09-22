package com.pyhtag.appfx;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class App extends Application {

	private static final Logger logger = LogManager.getLogger();
	static Stage stage;
	private static Scene scene;
	public static BorderPane mainWindow;
	public static MainWindowController mainWindowController;
	public static AnchorPane panelSetting;
	public static PanelSettingController panelSettingController;
	public static Popup contextMenu;
	public static ContextMenuController contextMenuController;

	@Override
	public void start(Stage stage) throws IOException {
		logger.traceEntry();
		App.stage = stage;
		ParentAndController panelSettingAndController = loadFXML("panelSetting");
		panelSetting = (AnchorPane) panelSettingAndController.parent;
		panelSettingController = (PanelSettingController) panelSettingAndController.controller;

		ParentAndController mainWindowAndController = loadFXML("mainWindow");
		mainWindow = (BorderPane) mainWindowAndController.parent;
		mainWindowController = (MainWindowController) mainWindowAndController.controller;

		mainWindowController.getSplitPane().getItems().add(panelSetting);
		scene = new Scene(mainWindow, 740, 680);
		scene.getStylesheets().add("com/pyhtag/appfx/css/mainWindow.css");
		stage.setScene(scene);
		stage.show();
		logger.debug("center width: {}", mainWindowController.getGraphicLayer().getWidth());
		logger.traceExit();
	}

	static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml).parent);
	}

	public static ParentAndController loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return new ParentAndController(fxmlLoader.load(), fxmlLoader.getController());
	}

	public static class ParentAndController {

		Parent parent;
		Object controller;

		public ParentAndController(Parent parent, Object controller) {
			this.parent = parent;
			this.controller = controller;
		}
	}

	public static void main(String[] args) {
		launch();
	}
}