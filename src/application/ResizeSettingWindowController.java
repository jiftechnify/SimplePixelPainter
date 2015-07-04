package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class ResizeSettingWindowController implements Initializable{

	@FXML private Button btnOK;
	@FXML private TextField txtNumPxX;
	@FXML private TextField txtNumPxY;
	@FXML
	private CheckBox chkAspectFix;

	private GridCanvasProperty initProperty;
	private GridCanvasProperty newProperty;

	private boolean okPressed = false;
	private boolean isAspectFix = false;
	private double ratio; // = x / y , x = y * ratio, y = x / ratio

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		chkAspectFix.selectedProperty().addListener(
				(observable, oldValue, newValue) -> {
					isAspectFix = newValue;
				});
		txtNumPxX.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (isAspectFix && !newValue.equals("")) {
						int newY = (int) (Double.valueOf(newValue) / ratio);
						txtNumPxY.setText(String.valueOf(newY));
					}
				});
		txtNumPxY.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (isAspectFix && !newValue.equals("")) {
						int newX = (int) (Double.valueOf(newValue) * ratio);
						txtNumPxX.setText(String.valueOf(newX));
					}
				});
	}
	@FXML
	private void onBtnResetClicked() {
		resetProperty();
	}

	private void resetProperty(){
		txtNumPxX.setText(String.valueOf(initProperty.numPixelX));
		txtNumPxY.setText(String.valueOf(initProperty.numPixelY));
	}

	@FXML
	private void onBtnOKClicked(ActionEvent e){
		int numPxX, numPxY;
		try{
			numPxX = Integer.valueOf(txtNumPxX.getText());
			numPxY = Integer.valueOf(txtNumPxY.getText());
		}
		catch(NumberFormatException ex){
			showAlert();
			return;
		}
		if (numPxX <= 0 || numPxY <= 0) {
			showAlert();
			return;
		}
		newProperty = new GridCanvasProperty(initProperty.gridSize, numPxX, numPxY);
		okPressed = true;
		getWindow().hide();
	}

	private void showAlert(){
		resetProperty();
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("");
		alert.getDialogPane().setHeaderText("警告");
		alert.getDialogPane().setContentText("1以上の整数を入力してください。");
		alert.show();
	}
	@FXML
	private void onBtnCancelClicked(ActionEvent e){
		okPressed = false;
		getWindow().hide();
	}

	public boolean okPressed(){
		return okPressed;
	}
	public void receiveProperty(GridCanvasProperty property){
		initProperty = property;
		ratio = (double) property.numPixelX / property.numPixelY;
		txtNumPxX.setText(String.valueOf(property.numPixelX));
		txtNumPxY.setText(String.valueOf(property.numPixelY));

	}
	public GridCanvasProperty getNewProperty(){
		return newProperty;
	}

	private Window getWindow(){
		return btnOK.getScene().getWindow();
	}
}
