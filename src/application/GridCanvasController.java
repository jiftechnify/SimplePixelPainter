package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import drawcommand.Command;
import drawstrategy.DrawStrategy;
import drawstrategy.EmptyRectDrawStrategy;
import drawstrategy.FilledRectDrawStrategy;
import drawstrategy.FreelineDrawStrategy;
import drawstrategy.LineDrawStrategy;

public class GridCanvasController implements Initializable{
	@FXML private BorderPane rootPane;
	@FXML private Canvas canvas;
	@FXML private Canvas gridLayer;
	@FXML private CheckMenuItem menuGrid;
	@FXML private MenuItem menuResize;
	@FXML private MenuItem menuDrawtypeNormal;
	@FXML private MenuItem menuDrawtypeCur;
	@FXML private MenuItem menuDrawtypeNext;
	@FXML private ToolBar toolbar;
	@FXML private ChoiceBox<String> choiceShape;
	@FXML private Button btnClear;

	private GridCanvasProperty p;

	private GraphicsContext canvasGraphics;
	private GraphicsContext gridGraphics;

	private GridCanvasModel pixelArray;

	private Point prevPos = new Point(-1, -1);
	private PixelState startPosPixel;

	private enum DrawType{
		NORMAL,
		FILL_WITH_CURRENT_COLOR_AT_START_POS,
		FILL_WITH_NEXT_COLOR_AT_START_POS,
	}
	private final Map<String, DrawStrategy> nameToStrategy = new HashMap<>();
	private List<String> shapeName;

	private DrawType drawType;
	private boolean clear = false;
	private DrawStrategy strategy;
	private List<Command> history = new ArrayList<>();
	// private List<Command> undoHistory = new Stack<>();

	private static final Color GRID_COLOR = new Color(0.5, 0.5, 0.5, 1.0);

	@FXML
	private void onPressed(MouseEvent e){
		Point pos = transformCoordinate(e.getX(), e.getY());
		if(e.getButton() == MouseButton.SECONDARY)
			clear = true;
		else
			startPosPixel = pixelArray.get(pos.x, pos.y);
		strategy.onPressed(transformCoordinate(e.getX(), e.getY()));

		clear = false;
	}

	@FXML
	private void onReleased(MouseEvent e){
		if(e.getButton() == MouseButton.SECONDARY)
			clear = true;
		strategy.onReleased(transformCoordinate(e.getX(), e.getY()));
		clear = false;
	}

	@FXML
	private void onDragged(MouseEvent e){
		Point pos = transformCoordinate(e.getX(), e.getY());
		if(!pos.equals(prevPos)){
			if(e.getButton() == MouseButton.SECONDARY)
				clear = true;
			strategy.onDragged(pos);
		}
		prevPos = pos;
		clear = false;
	}

	private Point transformCoordinate(double x, double y){
		return new Point((int)(x / p.gridWidth), (int)(y / p.gridHeight));
	}

	public void drawPixel(int x, int y){
		PixelState pixel = PixelState.WHITE;
		if(clear){
			pixelArray.set(x, y, pixel = PixelState.WHITE);
		}
		else{
			switch(drawType){
			case NORMAL:
				pixelArray.set(x, y, pixel = PixelState.nextState(pixelArray.get(x, y)));
				break;
			case FILL_WITH_CURRENT_COLOR_AT_START_POS:
				pixelArray.set(x, y, pixel = startPosPixel);
				if(startPosPixel == PixelState.WHITE)
					pixel = PixelState.BLACK;
				break;
			case FILL_WITH_NEXT_COLOR_AT_START_POS:
				pixelArray.set(x, y, pixel = PixelState.nextState(startPosPixel));
				break;
			}
		}
		canvasGraphics.setFill(pixel.color());
		canvasGraphics.fillRect(x * p.gridWidth, y * p.gridHeight, p.gridWidth, p.gridHeight);
	}

	public void addHistory(Command command) {
		history.add(command);
	}


	@FXML
	private void onMenuClearClicked(ActionEvent e){
		clearAll();
	}

	@FXML
	private void onMenuGridClicked(ActionEvent e){
		if(gridLayer.isVisible()){
			gridLayer.setVisible(false);
			menuGrid.setSelected(false);
		}
		else{
			gridLayer.setVisible(true);
			menuGrid.setSelected(true);
		}
	}
	@FXML
	private void onMenuDrawtypeNormalClicked(ActionEvent e){
		drawType = DrawType.NORMAL;
	}
	@FXML
	private void onMenuDrawtypeCurClicked(ActionEvent e){
		drawType = DrawType.FILL_WITH_CURRENT_COLOR_AT_START_POS;
	}
	@FXML
	private void onMenuDrawtypeNextClicked(ActionEvent e){
		drawType = DrawType.FILL_WITH_NEXT_COLOR_AT_START_POS;
	}
	@FXML
	private void onMenuResizeClicked(ActionEvent e) throws Exception{
		showResizeSetting();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initComboShape();
		strategy = nameToStrategy.get("自由線");
		drawType = DrawType.NORMAL;

		canvasGraphics = canvas.getGraphicsContext2D();
		gridGraphics = gridLayer.getGraphicsContext2D();

		p = new GridCanvasProperty(10, 10, 20, 20);

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
				canvasGraphics.setFill(pixelArray.get(x, y).color());
				canvasGraphics.fillRect(x * p.gridWidth, y * p.gridHeight, p.gridWidth, p.gridHeight);
			}
		}
	}

	private void initComboShape(){
		nameToStrategy.put("自由線", new FreelineDrawStrategy(this));
		nameToStrategy.put("直線", new LineDrawStrategy(this));
		nameToStrategy.put("長方形(塗りつぶし)", new FilledRectDrawStrategy(this));
		nameToStrategy.put("長方形(塗りつぶしなし)", new EmptyRectDrawStrategy(this));
		Collections.unmodifiableMap(nameToStrategy);
		shapeName = new ArrayList<>();
		shapeName.add("自由線");
		shapeName.add("直線");
		shapeName.add("長方形(塗りつぶし)");
		shapeName.add("長方形(塗りつぶしなし)");
		Collections.unmodifiableList(shapeName);
		choiceShape.getItems().addAll(shapeName);
		choiceShape.getSelectionModel().selectFirst();
		choiceShape.getSelectionModel().selectedItemProperty().addListener(
				(ov, oldVal, newVal) -> strategy = nameToStrategy.get(newVal));
	}
}
