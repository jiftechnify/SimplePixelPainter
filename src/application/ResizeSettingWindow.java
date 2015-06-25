package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ResizeSettingWindow extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox root = (VBox)FXMLLoader.load(getClass().getResource("ResizeSettingWindow.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("設定");
	}
}
