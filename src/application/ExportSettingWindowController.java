package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Window;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jiftech on 2015/07/06.
 */
public class ExportSettingWindowController implements Initializable {
    @FXML
    private CheckBox checkWhite;
    @FXML
    private ChoiceBox<FileFormat> choiceFormat;
    @FXML
    private TextField textZoomLevel;
    @FXML
    private Label labelSize;

    private ValidationSupport vs = new ValidationSupport();

    private GridCanvasProperty property;
    private FileFormat selectedFormat;
    private int selectedZoomLevel;
    private boolean whiteAsTransparent;
    private boolean okPressed = false;

    private static final String VALIDATE_MESSAGE = "1-50の整数を入力してください";
    private static final String VALIDATE_PATTERN = "[1-9]|[1-4][0-9]|50";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vs.registerValidator(textZoomLevel, false,
                Validator.createRegexValidator(VALIDATE_MESSAGE, VALIDATE_PATTERN, Severity.ERROR));
        checkWhite.selectedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    whiteAsTransparent = newValue;
                }
        );
        textZoomLevel.setText("1");

        textZoomLevel.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(textZoomLevel.getText().matches(VALIDATE_PATTERN)) {
                        int zoomLevel = Integer.valueOf(textZoomLevel.getText());
                        labelSize.setText(
                                property.numPixelX * zoomLevel + "*" + property.numPixelY * zoomLevel);
                    }
                    else
                        labelSize.setText("");
                }
        );

        choiceFormat.getItems().addAll(FileFormat.values());
        choiceFormat.getSelectionModel().selectFirst();
    }

    @FXML
    private void onBtnOKPressed() {
        if(textZoomLevel.getText().matches(VALIDATE_PATTERN)){
            selectedZoomLevel = Integer.valueOf(textZoomLevel.getText());
            selectedFormat = choiceFormat.getValue();
            okPressed = true;
            getWindow().hide();
        }
        else
            showAlert();
    }

    @FXML
    private void onBtnCancelPressed() {
        okPressed = false;
        getWindow().hide();
    }

    private void showAlert() {
        textZoomLevel.setText("1");
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("");
        alert.getDialogPane().setHeaderText("警告");
        alert.getDialogPane().setContentText(VALIDATE_MESSAGE);
        alert.show();
    }

    public void receiveProperty(GridCanvasProperty property) {
        this.property = property;
        labelSize.setText(property.numPixelX + "*" + property.numPixelY);
    }

    public void setWhiteAsTransparent(boolean whiteAsTransparent) {
        this.whiteAsTransparent = whiteAsTransparent;
        checkWhite.setSelected(whiteAsTransparent);
    }

    public FileFormat SelectedFormat() {
        return selectedFormat;
    }

    public int SelectedZoomLevel() {
        return selectedZoomLevel;
    }

    public boolean whiteAsTransparent() {
        return whiteAsTransparent;
    }

    public boolean okPressed() {
        return okPressed;
    }

    private Window getWindow() {
        return checkWhite.getScene().getWindow();
    }
}
