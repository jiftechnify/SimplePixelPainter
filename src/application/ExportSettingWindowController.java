package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Window;

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

    private GridCanvasProperty property;
    private FileFormat selectedFormat;
    private int selectedZoomLevel;
    private boolean whiteAsTransparent;
    private boolean okPressed = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkWhite.selectedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    whiteAsTransparent = newValue;
                }
        );
        textZoomLevel.setText("1");

        textZoomLevel.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    int zoomLevel;
                    try {
                        zoomLevel = Integer.valueOf(newValue);
                    } catch (NumberFormatException ex) {
                        labelSize.setText("");
                        return;
                    }
                    labelSize.setText(
                            property.numPixelX * zoomLevel + "*" + property.numPixelY * zoomLevel);
                }
        );

        choiceFormat.getItems().addAll(FileFormat.values());
        choiceFormat.getSelectionModel().selectFirst();
    }

    @FXML
    private void onBtnOKPressed() {
        okPressed = true;
        try {
            selectedZoomLevel = Integer.valueOf(textZoomLevel.getText());
        } catch (NumberFormatException ex) {
            showAlert();
            return;
        }
        selectedFormat = choiceFormat.getValue();
        getWindow().hide();
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
        alert.getDialogPane().setContentText("1以上の整数を入力してください。");
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
