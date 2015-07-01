package application;

import data.PixelState;
import data.Point;
import data.Table;
import drawcommand.ClearAllCommand;
import drawcommand.Command;
import drawcommand.DrawtypeChangeCommand;
import drawcommand.RedrawCommand;
import drawstrategy.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GridCanvasController implements Initializable{
	@FXML private BorderPane rootPane;
	@FXML private Canvas canvas;
	@FXML private Canvas previewLayer;
	@FXML private Canvas gridLayer;
	@FXML private Canvas accentGridLayer;

	private GraphicsContext canvasGraphics;
	private GraphicsContext previewGraphics;
	private GraphicsContext gridGraphics;
	private GraphicsContext accentGraphics;

	@FXML private CheckMenuItem menuGrid;
	@FXML private CheckMenuItem menuAccentGrid;

	@FXML private ChoiceBox<String> choiceShape;
	@FXML private Label labelDrawtype;

	private GridCanvasProperty p;

	private Canvas exportCanvas;
	private GraphicsContext exportCanvasGraphics;

	private GridCanvasModel pixelArray;

	private Point prevPos = Point.DUMMY_POINT;

	public enum Drawtype{
		NORMAL("通常"),
		FILL_WITH_CURRENT_COLOR_AT_START_POS("始点の今の色で全て描画"),
		FILL_WITH_NEXT_COLOR_AT_START_POS("始点の次の色で全て描画"),;

		private String desc;

		private Drawtype(String desc){
			this.desc = desc;
		}
		public String toString(){
			return desc;
		}
	}
	private final Map<String, DrawStrategy> nameToStrategy = new HashMap<>();

	private Drawtype drawtype;
	private DrawStrategy strategy;

	private Stack<Command> history = new Stack<>();
	private Stack<Command> undoHistory = new Stack<>();

	private int gridAccentMergin = 8;
	private boolean isAccentGridVisible = false;
	private static final Color GRID_COLOR = new Color(0.5, 0.5, 0.5, 1.0);
	private static final Color GRID_COLOR_ACCENT = Color.RED;

	/*
	 * 描画
	 * Canvas上でのマウス操作に応じて、選択中の図形のDrawStrategyの対応するメソッドを呼ぶ。
	 * DrawStrategyは操作に応じて図形を描画するDrawCommandを生成し実行。
	 *
	 */
	// コールバックメソッド
	@FXML
	private void onPressed(MouseEvent e){
		Point pos = transformCoordinate(e.getX(), e.getY());
		if(pos != null){
			PixelState startPosPixel = null;
			boolean clear = false;
			if(e.getButton() == MouseButton.SECONDARY)
				clear = true;
			else
				startPosPixel = pixelArray.getAt(pos.x, pos.y);
			strategy.onPressed(pos, startPosPixel, clear);
		}
	}
	@FXML
	private void onReleased(MouseEvent e){
		clearPreview();
		Point pos = transformCoordinate(e.getX(), e.getY());
		if(pos != null)
			strategy.onReleased(pos);
	}
	@FXML
	private void onDragged(MouseEvent e){
		Point pos = transformCoordinate(e.getX(), e.getY());
		if(pos == null){
			clearPreview();
			//prevPos = Point.DUMMY_POINT;
		}
		else if(!pos.equals(prevPos)){
			clearPreview();
			strategy.onDragged(pos);
			prevPos = pos;
		}
	}

	// 画面の座標をグリッドの座標に変換 画面外ならnullを返す
	private Point transformCoordinate(double x, double y){
		int gridX = (int)(x / p.gridSize);
		int gridY = (int)(y / p.gridSize);
		if(gridX < 0 || gridX >= p.numPixelX || gridY < 0 || gridY >= p.numPixelY){
			return null;
		}
		return new Point(gridX, gridY);
	}
	// 描画処理
	public void drawPixel(int x, int y, PixelState startPosPixel, boolean clear){
		PixelState pixel = PixelState.WHITE;
		if(clear){
			pixelArray.setAt(x, y, pixel = PixelState.WHITE);
		}
		else{
			switch(drawtype){
			case NORMAL:
				pixelArray.setAt(x, y, pixel = PixelState.nextState(pixelArray.getAt(x, y)));
				break;
			case FILL_WITH_CURRENT_COLOR_AT_START_POS:
				if(startPosPixel == PixelState.WHITE)
					startPosPixel = PixelState.BLACK;
				pixelArray.setAt(x, y, pixel = startPosPixel);
				break;
			case FILL_WITH_NEXT_COLOR_AT_START_POS:
				pixelArray.setAt(x, y, pixel = PixelState.nextState(startPosPixel));
				break;
			}
		}
		canvasGraphics.setFill(pixel.color());
		canvasGraphics.fillRect(x * p.gridSize, y * p.gridSize, p.gridSize, p.gridSize);
	}
	// プレビュー描画
	public void drawPreview(int x, int y, PixelState startPosPixel, boolean clear){
		if(clear)
			if(pixelArray.getAt(x, y).color().equals(Color.WHITE))
				previewGraphics.setFill(Color.BLACK);
			else
				previewGraphics.setFill(Color.WHITE);
		else{
			switch(drawtype){
			case NORMAL:
				previewGraphics.setFill(PixelState.nextState(pixelArray.getAt(x, y)).color());
				break;
			case FILL_WITH_CURRENT_COLOR_AT_START_POS:
				Color color = startPosPixel.color();
				if(color.equals(pixelArray.getAt(x, y).color()))
					previewGraphics.setFill(color.invert().darker());
				else
					previewGraphics.setFill(color);
				break;
			case FILL_WITH_NEXT_COLOR_AT_START_POS:
				previewGraphics.setFill(PixelState.nextState(startPosPixel).color());
			}
		}
		previewGraphics.fillRect(x * p.gridSize, y * p.gridSize, p.gridSize, p.gridSize);
	}
	// プレビューの消去
	public void clearPreview(){
		previewGraphics.clearRect(0.0, 0.0, previewLayer.getWidth(), previewLayer.getHeight());
	}
	// 全消去
	public void clearAll(){
		pixelArray.clearAll();
		canvasGraphics.setFill(Color.WHITE);
		canvasGraphics.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
	}

	/**
	 * Undo/Redo
	 * 描画、全消去、描画方法変更の度にその操作を表すCommandを生成し、historyに蓄積する。
	 * Undo時はhistoryから1つpopしてRedo用のundoHistoryにpushし、再描画
	 * Redo時はundoHistoryから1つpopしてhistoryにpushし、再描画
	 *
	 */
	@FXML
	private void onMenuUndoClicked(ActionEvent e){
		undoHistory.push(history.pop());
		clearAll();
		for(Command command: history)
			command.execute();
	}
	@FXML
	private void onMenuRedoClicked(ActionEvent e){
		history.push(undoHistory.pop());
		clearAll();
		for(Command command: history)
			command.execute();
	}
	public void pushNewHistory(Command command) {
		undoHistory.removeAllElements();
		history.push(command);
	}

	/**
	 * メニューまわりの処理
	 * いずれは別のクラスに移したい
	 */
	// 画像出力
	@FXML
	private void onMenuExportClicked(ActionEvent e){
		exportCanvas = new Canvas();
		exportCanvas.setWidth(p.numPixelX);
		exportCanvas.setHeight(p.numPixelY);
		exportCanvasGraphics = exportCanvas.getGraphicsContext2D();
		for(int x = 0; x < p.numPixelX; x++){
			for(int y = 0; y < p.numPixelY; y++){
				exportCanvasGraphics.setFill(pixelArray.getAt(x, y).color());
				exportCanvasGraphics.fillRect(x, y, 1.0, 1.0);
			}
		}
		WritableImage writableImage = exportCanvas.snapshot(null, null);

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter("PNGファイル", "*.png"));
		File outFile = fc.showSaveDialog(rootPane.getScene().getWindow());
		if(outFile != null){
			try{
				ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", outFile);
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	// すべて消去
	@FXML
	private void onMenuClearClicked(ActionEvent e){
		pushNewHistory(new ClearAllCommand(this));
		clearAll();
	}
	// グリッド表示切り替え
	@FXML
	private void onMenuGridClicked(ActionEvent e){
		if(gridLayer.isVisible()){
			gridLayer.setVisible(false);
			accentGridLayer.setVisible(false);
			menuGrid.setSelected(false);
			menuAccentGrid.setSelected(false);
			menuAccentGrid.setDisable(true);
		}
		else{
			gridLayer.setVisible(true);
			accentGridLayer.setVisible(isAccentGridVisible);
			menuGrid.setSelected(true);
			menuAccentGrid.setDisable(false);
			menuAccentGrid.setSelected(isAccentGridVisible);
		}
	}
	// 強調グリッド表示切り替え
	@FXML
	private void onMenuAccentGridClicked(ActionEvent e){
		if(gridLayer.isVisible()){
			if(accentGridLayer.isVisible()){
				isAccentGridVisible = false;
				accentGridLayer.setVisible(false);
				menuAccentGrid.setSelected(false);
			}
			else{
				isAccentGridVisible = true;
				accentGridLayer.setVisible(true);
				menuAccentGrid.setSelected(true);
			}
		}
	}
	// 描画方法
	@FXML
	private void onMenuDrawtypeNormalClicked(ActionEvent e){
		Command c = new DrawtypeChangeCommand(this, Drawtype.NORMAL);
		pushNewHistory(c);
		c.execute();
	}
	@FXML
	private void onMenuDrawtypeCurClicked(ActionEvent e){
		Command c = new DrawtypeChangeCommand(this, Drawtype.FILL_WITH_CURRENT_COLOR_AT_START_POS);
		pushNewHistory(c);
		c.execute();
	}
	@FXML
	private void onMenuDrawtypeNextClicked(ActionEvent e){
		Command c = new DrawtypeChangeCommand(this, Drawtype.FILL_WITH_NEXT_COLOR_AT_START_POS);
		pushNewHistory(c);
		c.execute();
	}
	public void setDrawType(Drawtype drawType){
		this.drawtype = drawType;
		labelDrawtype.setText(drawtype.toString());
	}

	/**
	 * キャンバスのリサイズ
	 */
	@FXML
	private void onMenuResizeClicked(ActionEvent e) throws Exception{
		showResizeSetting();
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
			GridCanvasProperty newProperty = settingController.getNewProperty();
			resizeCanvas(newProperty);
		}
		else{
			System.out.println("cancel");
		}
	}
	private void resizeCanvas(GridCanvasProperty newProperty){
		p = newProperty;
		setCanvasSize();

		pixelArray.resize(p.numPixelX, p.numPixelY);

		canvasGraphics.setFill(Color.WHITE);
		canvasGraphics.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

		// リサイズ後はリサイズした時点までしかアンドゥできない
		history.removeAllElements();
		pushNewHistory(new DrawtypeChangeCommand(this, drawtype));
		undoHistory.removeAllElements();
		redrawInResize(p);

		// グリッドを描き換え
		drawGrid();
	}
	private void redrawInResize(GridCanvasProperty newP){
		RedrawCommand c = new RedrawCommand(newP.numPixelX, newP.numPixelY, this);
		for(int x = 0; x < newP.numPixelX; x++){
			for(int y = 0; y < newP.numPixelY; y++)
				c.set(x, y, pixelArray.getAt(x, y));
		}
		pushNewHistory(c);
		c.execute();
	}

	public void redraw(Table<PixelState> pixelRedraw){
		for(int x = 0; x < pixelRedraw.numX(); x++){
			for(int y = 0; y < pixelRedraw.numY(); y++){
				pixelArray.setAt(x, y, pixelRedraw.getAt(x, y));
				canvasGraphics.setFill(pixelRedraw.getAt(x, y).color());
				canvasGraphics.fillRect(x * p.gridSize, y * p.gridSize, p.gridSize, p.gridSize);
			}
		}
	}

	// 初期化
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 描画関係の初期化
		initComboShape();
		strategy = nameToStrategy.get("自由線");
		setDrawType(Drawtype.NORMAL);
		pushNewHistory(new DrawtypeChangeCommand(this, Drawtype.NORMAL));

		// キャンバスとグリッドのレイヤーのGraphicsContextを得る
		canvasGraphics = canvas.getGraphicsContext2D();
		previewGraphics = previewLayer.getGraphicsContext2D();
		gridGraphics = gridLayer.getGraphicsContext2D();
		accentGraphics = accentGridLayer.getGraphicsContext2D();

		// グリッド/キャンバスサイズ設定
		p = new GridCanvasProperty(12, 24, 24);

		// 各ピクセルの内容を管理するModelを生成
		pixelArray = new GridCanvasModel(p.numPixelX, p.numPixelY);

		// グリッドとキャンバスを初期化
		setCanvasSize();
		drawGrid();

		canvasGraphics.setFill(Color.WHITE);
		canvasGraphics.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
	}
	// キャンバスと各レイヤーの大きさを設定する
	private void setCanvasSize(){
		canvas.setWidth(p.gridSize * p.numPixelX);
		canvas.setHeight(p.gridSize * p.numPixelY);
		previewLayer.setWidth(p.gridSize * p.numPixelX);
		previewLayer.setHeight(p.gridSize * p.numPixelY);
		gridLayer.setWidth(p.gridSize * p.numPixelX);
		gridLayer.setHeight(p.gridSize * p.numPixelY);
		accentGridLayer.setWidth(p.gridSize * p.numPixelX);
		accentGridLayer.setHeight(p.gridSize * p.numPixelY);
	}
	// グリッドを描画
	private void drawGrid(){
		gridGraphics.clearRect(0.0, 0.0, gridLayer.getWidth(), gridLayer.getHeight());
		accentGraphics.clearRect(0.0, 0.0, accentGridLayer.getWidth(), accentGridLayer.getHeight());
		if(p.gridSize > 2 && p.gridSize > 2){
			gridGraphics.setFill(GRID_COLOR);
			accentGraphics.setFill(GRID_COLOR_ACCENT);
			for(int x = 0; x <= p.numPixelX; x++){
				gridGraphics.fillRect(p.gridSize * x, 0.0, 1.0, gridLayer.getHeight());
				if(x != 0 && x % gridAccentMergin == 0)
					accentGraphics.fillRect(p.gridSize * x, 0.0, 1.0, accentGridLayer.getHeight());
			}
			for(int y = 0; y <= p.numPixelY; y++){
				gridGraphics.fillRect(0.0, p.gridSize * y, gridLayer.getWidth(), 1.0);
				if(y != 0 && y % gridAccentMergin == 0)
					accentGraphics.fillRect(0.0, p.gridSize * y, accentGridLayer.getWidth(), 1.0);
			}
		}
	}
	private void initComboShape(){
		nameToStrategy.put("自由線", new FreelineDrawStrategy(this));
		nameToStrategy.put("直線", new LineDrawStrategy(this));
		nameToStrategy.put("長方形(塗りつぶし)", new FilledRectDrawStrategy(this));
		nameToStrategy.put("長方形(塗りつぶしなし)", new EmptyRectDrawStrategy(this));
		Collections.unmodifiableMap(nameToStrategy);
		List<String> shapeName = new ArrayList<>();
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

	// DEBUG
	private void showArray(){
		for(int x = 0; x < p.numPixelX; x++){
			for(int y = 0; y < p.numPixelY; y++){
				System.out.print(pixelArray.getAt(y, x).ordinal() + ",");
			}
			System.out.println();
		}
	}

	@FXML
	private void onbtnShow(ActionEvent e){
		showArray();
	}
}
