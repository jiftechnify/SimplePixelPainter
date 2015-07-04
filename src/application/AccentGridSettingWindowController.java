package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jiftech on 2015/07/03.
 */
public class AccentGridSettingWindowController implements Initializable {
    @FXML
    private TextField txtMarginX;
    @FXML
    private TextField txtMarginY;
    @FXML
    private Slider sliderColor;
    @FXML
    private Rectangle rectColor;

    private AccentGridProperty initProperty;
    private AccentGridProperty newProperty;
    private boolean okPressed = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sliderColor.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    rectColor.setFill(Color.hsb((double) newValue, 1.0, 0.9));
                }
        );
    }

    @FXML
    private void onOKButton() {
        int marginX, marginY;
        try {
            marginX = Integer.valueOf(txtMarginX.getText());
            marginY = Integer.valueOf(txtMarginY.getText());
        } catch (NumberFormatException ex) {
            showAlert();
            return;
        }
        if (marginX <= 0 || marginY <= 0) {
            showAlert();
            return;
        }
        newProperty = new AccentGridProperty(
                marginX, marginY, Color.hsb(sliderColor.getValue(), 1.0, 0.9));
        okPressed = true;
        getWindow().hide();
    }

    private void showAlert() {
        resetProperty();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("");
        alert.getDialogPane().setHeaderText("警告");
        alert.getDialogPane().setContentText("1以上の整数を入力してください");
        alert.show();
    }

    private void resetProperty() {
        txtMarginX.setText(String.valueOf(initProperty.marginX));
        txtMarginY.setText(String.valueOf(initProperty.marginY));
    }

    @FXML
    private void onCancelButton() {
        okPressed = false;
        getWindow().hide();
    }

    @FXML
    private void onResetButton() {
        resetProperty();
    }

    public void receiveProperty(AccentGridProperty property) {
        initProperty = property;
        txtMarginX.setText(String.valueOf(property.marginX));
        txtMarginY.setText(String.valueOf(property.marginY));
        rectColor.setFill(property.color);
        sliderColor.setValue(property.color.getHue());
    }

    public AccentGridProperty getNewProperty() {
        return newProperty;
    }

    public boolean okPressed() {
        return okPressed;
    }

    private Window getWindow() {
        return txtMarginX.getScene().getWindow();
    }
}
