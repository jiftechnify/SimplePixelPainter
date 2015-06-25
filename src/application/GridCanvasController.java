package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GridCanvasController implements Initializable{
	@FXML private BorderPane rootPane;
	@FXML private Canvas canvas;
	@FXML private Canvas gridLayer;
	@FXML private MenuItem menuClear;
	@FXML private MenuItem menuGrid;
	@FXML private MenuItem menuResize;

	private GridCanvasProperty p;

	private GraphicsContext canvasGraphics;
	private GraphicsContext gridGraphics;

	private int prevX = -1;
	private int prevY = -1;
	private boolean nowDragging = false;
	// private PixelState[][] arrayPixel;
	private GridCanvasModel pixelArray;

	private static final Color GRID_COLOR = new Color(0.5, 0.5, 0.5, 1.0);

	@FXML
	private void onCanvasDragged(MouseEvent e){
		int x = (int)(e.getX() / p.gridWidth);
		int y = (int)(e.getY() / p.gridHeight);
		PixelState s;
		if(!isSamePos(x, y)){
			if(e.getButton().equals(MouseButton.SECONDARY))
				drawPixel(x, y, PixelState.WHITE);
			else{
				drawPixel(x, y, s = PixelState.nextState(pixelArray.get(x, y)));
				System.out.println(s);
			}
		}
		prevX = x;
		prevY = y;
		nowDragging = true;
	}
	private boolean isSamePos(int x, int y){
		return (x == prevX && y == prevY);
	}

	@FXML
	private void onCanvasClicked(MouseEvent e){
		if(!nowDragging){
			int x = (int)(e.getX() / p.gridWidth);
			int y = (int)(e.getY() / p.gridHeight);
			if(e.getButton().equals(MouseButton.SECONDARY))
				drawPixel(x, y, PixelState.WHITE);
			else
				drawPixel(x, y, PixelState.nextState(pixelArray.get(x, y)));
		}
		nowDragging = false;
	}

	@FXML
	private void onMenuClearClicked(ActionEvent e){
		clearAll();
	}

	@FXML
	private void onMenuGridClicked(ActionEvent e){
		if(gridLayer.isVisible())
			gridLayer.setVisible(false);
		else
			gridLayer.setVisible(true);
	}

	@FXML
	private void onMenuResizeClicked(ActionEvent e) throws Exception{
		showResizeSetting();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		canvasGraphics = canvas.getGraphicsContext2D();
		gridGraphics = gridLayer.getGraphicsContext2D();

		p = new GridCanvasProperty(20, 20, 10, 10);

		pixelArray = new GridCanvasModel(p.numPixelX, p.numPixelY);
		canvas.setWidth(p.gridWidth * p.numPixelX);
		canvas.setHeight(p.gridHeight * p.numPixelY);
		gridLayer.setWidth(p.gridWidth * p.numPixelX);
		gridLayer.setHeight(p.gridHeight * p.numPixelY);
		if(p.gridWidth > 2 && p.gridHeight > 2){
			gridGraphics.setFill(GRID_COLOR);
			for(int x = 0; x <= p.numPixelX; x++)
				gridGraphics.fillRect(p.gridWidth * x, 0.0, 1.0, gridLayer.getHeight());
			for(int y = 0; y <= p.numPixelY; y++)
				gridGraphics.fillRect(0.0, p.gridHeight * y, gridLayer.getWidth(), 1.0);
		}
		canvasGraphics.setFill(Color.WHITE);
		canvasGraphics.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
		gridLayer.toFront();
	}

	private void clearAll(){
		pixelArray.clearAll();
		canvasGraphics.setFill(Color.WHITE);
		canvasGraphics.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
	}

	private void drawPixel(int x, int y, PixelState pixel){
		canvasGraphics.setFill(pixel.pixelColor());
		// arrayPixel[pos.x][pos.y] = pixel;
		pixelArray.set(x, y, pixel);
		canvasGraphics.fillRect(x * p.gridWidth, y * p.gridHeight, p.gridWidth, p.gridHeight);
	}
	private void showResizeSetting() throws Exception{
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("ResizeSettingWindow.fxml"));
		loader.load();
		ResizeSettingWindowController settingController = loader.getController();
		settingController.receiveProperty(p);

		Parent root = loader.getRoot();
		Stage stage = new Stage(StageStyle.UTILITY);
		stage.setScene(new Scene(root));
		stage.initOwner(rootPane.getScene().getWindow());
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setTitle("設定");
		stage.setResizable(false);

		stage.showAndWait();

		if(settingController.okPressed()){
			p = settingController.getNewProperty();
			resizeCanvas(p);
		}
		else{
			System.out.println("cancel");
		}
	}
	private void resizeCanvas(GridCanvasProperty p){
		canvas.setWidth(p.gridWidth * p.numPixelX);
		canvas.setHeight(p.gridHeight * p.numPixelY);
		gridLayer.setWidth(p.gridWidth * p.numPixelX);
		gridLayer.setHeight(p.gridHeight * p.numPixelY);

		pixelArray.resize(p.numPixelX, p.numPixelY);

		canvasGraphics.setFill(Color.WHITE);
		canvasGraphics.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
		redraw();
		gridGraphics.clearRect(0.0, 0.0, gridLayer.getWidth(), gridLayer.getHeight());
		if(p.gridWidth > 2 && p.gridHeight > 2){
			gridGraphics.setFill(GRID_COLOR);
			for(int x = 0; x <= p.numPixelX; x++)
				gridGraphics.fillRect(p.gridWidth * x, 0.0, 1.0, gridLayer.getHeight());
			for(int y = 0; y <= p.numPixelY; y++)
				gridGraphics.fillRect(0.0, p.gridHeight * y, gridLayer.getWidth(), 1.0);
		}

	}
	private void redraw(){
		for(int x = 0; x < p.numPixelX; x++){
			for(int y = 0; y < p.numPixelY; y++){
				canvasGraphics.setFill(pixelArray.get(x, y).pixelColor());
				canvasGraphics.fillRect(x * p.gridWidth, y * p.gridHeight, p.gridWidth, p.gridHeight);
			}
		}
	}
}
