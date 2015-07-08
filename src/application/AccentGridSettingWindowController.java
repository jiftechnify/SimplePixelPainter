package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jiftech on 2015/07/03.
 */
public class AccentGridSettingWindowController implements Initializable {
    @FXML private TextField txtMarginX;
    @FXML private TextField txtMarginY;
    @FXML private Slider sliderColor;
    @FXML private Rectangle rectColor;

    private ValidationSupport vs = new ValidationSupport();

    private AccentGridProperty initProperty;
    private AccentGridProperty newProperty;
    private boolean okPressed = false;

    private static final String VALIDATE_MESSAGE = "1-100の整数を入力してください";
    private static final String VALIDATE_PATTERN = "[1-9][0-9]?|100";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vs.registerValidator(txtMarginX, false,
                Validator.createRegexValidator(VALIDATE_MESSAGE, VALIDATE_PATTERN, Severity.ERROR));
        vs.registerValidator(txtMarginY, false,
                Validator.createRegexValidator(VALIDATE_MESSAGE, VALIDATE_PATTERN, Severity.ERROR));
        sliderColor.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    rectColor.setFill(Color.hsb((double) newValue, 1.0, 0.9));
                }
        );
    }

    @FXML
    private void onOKButton() {
        if(txtMarginX.getText().matches(VALIDATE_PATTERN) && txtMarginY.getText().matches(VALIDATE_PATTERN)) {
            int marginX = Integer.valueOf(txtMarginX.getText());
            int marginY = Integer.valueOf(txtMarginY.getText());
            newProperty = new AccentGridProperty(
                    marginX, marginY, Color.hsb(sliderColor.getValue(), 1.0, 0.9));
            okPressed = true;
            getWindow().hide();
        }
        else
            showAlert();
    }

    private void showAlert() {
        resetProperty();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("");
        alert.getDialogPane().setHeaderText("警告");
        alert.getDialogPane().setContentText(VALIDATE_MESSAGE);
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
