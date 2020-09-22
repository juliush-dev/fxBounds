module com.pyhtag.appfx {
    requires transitive javafx.controls;
    requires javafx.fxml;
	requires javafx.web;
    requires org.apache.logging.log4j;
    requires transitive org.kordamp.iconli.core;
    requires transitive org.kordamp.ikonli.javafx;
    requires transitive org.kordamp.ikonli.runestroicons;
	requires org.kordamp.ikonli.metrizeicons;

    opens com.pyhtag.appfx to javafx.fxml;
    exports com.pyhtag.appfx;
}