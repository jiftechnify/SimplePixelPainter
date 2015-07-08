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
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;

public class ResizeSettingWindowController implements Initializable{

	@FXML private Button btnOK;
	@FXML private TextField txtNumPxX;
	@FXML private TextField txtNumPxY;
	@FXML private CheckBox chkAspectFix;

	private ValidationSupport vs = new ValidationSupport();

	private GridCanvasProperty initProperty;
	private GridCanvasProperty newProperty;

	private boolean okPressed = false;
	private boolean isAspectFix = false;
	private double ratio; // = x / y , x = y * ratio, y = x / ratio

	private static final String VALIDATE_MESSAGE = "1-100の整数を入力してください";
	private static final String VALIDATE_PATTERN = "[1-9][0-9]?|100";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		vs.registerValidator(txtNumPxX, false,
				Validator.createRegexValidator(VALIDATE_MESSAGE, VALIDATE_PATTERN, Severity.ERROR));
		vs.registerValidator(txtNumPxY, false,
				Validator.createRegexValidator(VALIDATE_MESSAGE, VALIDATE_PATTERN, Severity.ERROR));

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
		if(txtNumPxX.getText().matches(VALIDATE_PATTERN) && txtNumPxY.getText().matches(VALIDATE_PATTERN)) {
			int numPxX = Integer.valueOf(txtNumPxX.getText());
			int numPxY = Integer.valueOf(txtNumPxY.getText());
			newProperty = new GridCanvasProperty(initProperty.gridSize, numPxX, numPxY);
			okPressed = true;
			getWindow().hide();
		}
		else
			showAlert();
	}

	private void showAlert(){
		resetProperty();
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("");
		alert.getDialogPane().setHeaderText("警告");
		alert.getDialogPane().setContentText(VALIDATE_MESSAGE);
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
