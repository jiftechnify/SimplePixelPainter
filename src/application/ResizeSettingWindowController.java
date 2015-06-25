package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class ResizeSettingWindowController implements Initializable{

	@FXML private Button btnReset;
	@FXML private Button btnOK;
	@FXML private Button btnCancel;
	@FXML private TextField txtGridW;
	@FXML private TextField txtGridH;
	@FXML private TextField txtNumPxX;
	@FXML private TextField txtNumPxY;

	private GridCanvasProperty property;
	private GridCanvasProperty newProperty;

	private boolean okPressed = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void onBtnResetClicked(ActionEvent e){
		resetProperty();
	}
	
	private void resetProperty(){
		txtGridW.setText(String.valueOf(property.gridWidth));
		txtGridH.setText(String.valueOf(property.gridHeight));
		txtNumPxX.setText(String.valueOf(property.numPixelX));
		txtNumPxY.setText(String.valueOf(property.numPixelY));
	}

	@FXML
	private void onBtnOKClicked(ActionEvent e){
		int gridW,gridH,numPxX,numPxY;
		try{
			gridW  = Integer.valueOf(txtGridW.getText());
			gridH  = Integer.valueOf(txtGridH.getText());
			numPxX = Integer.valueOf(txtNumPxX.getText());
			numPxY = Integer.valueOf(txtNumPxY.getText());
		}
		catch(NumberFormatException ex){
			showAlert();
			resetProperty();
			return;
		}
		if(gridW <= 0 || gridH <= 0 || numPxX <= 0 || numPxY <= 0){
			showAlert();
			resetProperty();
			return;
		}
		newProperty = new GridCanvasProperty(gridW, gridH, numPxX, numPxY);
		okPressed = true;
		getWindow().hide();
	}

	private void showAlert(){
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
		this.property = property;
		txtGridW.setText(String.valueOf(property.gridWidth));
		txtGridH.setText(String.valueOf(property.gridHeight));
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
