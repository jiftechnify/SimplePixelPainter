package application;

import data.PixelState;
import data.Point;
import data.Table;
import drawcommand.ClearAllCommand;
import drawcommand.Command;
import drawcommand.DrawtypeChangeCommand;
import drawcommand.RedrawCommand;
import drawstrategy.DrawStrategy;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
import javafx.util.Callback;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

public class GridCanvasController implements Initializable{
	private static GridCanvasController instance;

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

	@FXML private Slider sliderZoom;

	@FXML private ComboBox<Shapes> comboShape;
	@FXML private ToggleButton tBtnSelect;
	@FXML private Label labelDrawtype;
	@FXML private Label labelGridSize;
	@FXML private Label labelPos;
	@FXML private Label labelOptionPos;

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

	private Drawtype drawtype;
	private DrawStrategy strategy;

	private Stack<Command> history = new Stack<>();
	private Stack<Command> undoHistory = new Stack<>();

	private AccentGridProperty accentGridP;
	private boolean isAccentGridVisible = false;
	private static final Color GRID_COLOR = new Color(0.5, 0.5, 0.5, 1.0);

	private int[] gridSizeArray = {1, 2, 4, 6, 8, 10, 12, 14, 16, 20, 24, 28, 32, 38, 44, 50};
	private int gridSizeIndex;
	private static final int DEFAULT_GRID_SIZE_INDEX = 8;

	private boolean nowSelecting = false;

	static GridCanvasController getInstance() {
		return instance;
	}
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
		if(nowSelecting)
			onPressedSelect(pos);
		else
			onPressedDraw(pos, e.getButton());
	}
	private void onPressedSelect(Point pos){

	}
	private void onPressedDraw(Point pos, MouseButton btn){
		PixelState startPosPixel = null;
		boolean clear = false;
		if(btn == MouseButton.SECONDARY)
			clear = true;
		else
			startPosPixel = pixelArray.getAt(pos.x, pos.y);
		strategy.onPressed(pos, startPosPixel, clear);
	}
	@FXML
	private void onReleased(MouseEvent e){
		Point pos = transformCoordinate(e.getX(), e.getY());
		if(nowSelecting)
			onReleasedSelect(pos);
		else
			onReleasedDraw(pos);
	}
	private void onReleasedDraw(Point pos){
		showOptionPos("");
		clearPreview();
		strategy.onReleased(pos);
	}
	private void onReleasedSelect(Point pos){

	}
	@FXML
	private void onDragged(MouseEvent e){
		Point pos = transformCoordinate(e.getX(), e.getY());
		if(!pos.equals(prevPos)){
			clearPreview();
			if(nowSelecting)
				onDraggedSelect(pos);
			else
				onDraggedDraw(pos);
			prevPos = pos;
		}
	}
	private void onDraggedDraw(Point pos){
		strategy.onDragged(pos);
	}
	private void onDraggedSelect(Point pos){

	}
	@FXML
	private void onMoved(MouseEvent e) {
		Point pos = transformCoordinate(e.getX(), e.getY());
		showPosition(pos);
	}
	// 画面の座標をグリッドの座標に変換 画面外ならnullを返す
	private Point transformCoordinate(double x, double y){
		int gridX = (int)(x / p.gridSize);
		int gridY = (int)(y / p.gridSize);

		return new Point(gridX, gridY);
	}
	// マウスポインタのある座標を表示
	private void showPosition(Point pos) {
		labelPos.setText("[" + pos.x + ", " + pos.y + "]");
	}
	// 描画しようとしている図形に合わせたオプション座標情報を表示
	public void showOptionPos(String optPos) {
		labelOptionPos.setText(optPos);
	}
	private boolean isOnCanvas(int x, int y){
		return (0 <= x && x < p.numPixelX) && (0 <= y && y < p.numPixelY);
	}
	// 描画処理
	public void drawPixel(int x, int y, PixelState startPosPixel, boolean clear){
		if(isOnCanvas(x, y)) {
			PixelState pixel = PixelState.WHITE;
			if (clear) {
				pixelArray.setAt(x, y, pixel = PixelState.WHITE);
			} else {
				switch (drawtype) {
					case NORMAL:
						pixelArray.setAt(x, y, pixel = PixelState.nextState(pixelArray.getAt(x, y)));
						break;
					case FILL_WITH_CURRENT_COLOR_AT_START_POS:
						if (startPosPixel == PixelState.WHITE)
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
	}
	// プレビュー描画
	public void drawPreview(int x, int y, PixelState startPosPixel, boolean clear){
		if(isOnCanvas(x, y)) {
			if (clear)
				if (pixelArray.getAt(x, y).color().equals(Color.WHITE))
					previewGraphics.setFill(Color.BLACK);
				else
					previewGraphics.setFill(Color.WHITE);
			else {
				switch (drawtype) {
					case NORMAL:
						previewGraphics.setFill(PixelState.nextState(pixelArray.getAt(x, y)).color());
						break;
					case FILL_WITH_CURRENT_COLOR_AT_START_POS:
						Color color = startPosPixel.color();
						if (color.equals(pixelArray.getAt(x, y).color()))
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
	}
	// プレビューの消去
	private void clearPreview() {
		previewGraphics.clearRect(0.0, 0.0, previewLayer.getWidth(), previewLayer.getHeight());
	}
	// 全消去
	public void clearAll(){
		pixelArray.clearAll();
		canvasGraphics.setFill(Color.WHITE);
		canvasGraphics.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
	}

	/*
	 * Undo/Redo
	 * 描画、全消去、描画方法変更の度にその操作を表すCommandを生成し、historyに蓄積する。
	 * Undo時はhistoryから1つpopしてRedo用のundoHistoryにpushし、再描画
	 * Redo時はundoHistoryから1つpopしてhistoryにpushし、再描画
	 */
	@FXML
	private void onMenuUndoClicked(ActionEvent e){
		undoHistory.push(history.pop());
		clearAll();
		history.forEach(Command::execute);
	}
	@FXML
	private void onMenuRedoClicked(ActionEvent e){
		history.push(undoHistory.pop());
		clearAll();
		history.forEach(Command::execute);
	}
	public void pushNewHistory(Command command) {
		undoHistory.removeAllElements();
		history.push(command);
	}

	/*
	 * 拡大縮小
	 */
	private void zoomCanvas(int zoomLevel) {
		int newGridSize = gridSizeArray[zoomLevel];
		p = new GridCanvasProperty(newGridSize, p.numPixelX, p.numPixelY);
		setCanvasSize();
		clearAll();
		drawGrid();
		for (Command c : history)
			c.execute();
		labelGridSize.setText(String.valueOf(newGridSize));
	}

	/*
	 * メニューまわりの処理
	 * いずれは別のクラスに移したい
	 */
	// 画像出力
	@FXML
	private void onMenuExportClicked() {
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

	// 補助グリッド表示切り替え
	@FXML
	private void onMenuAccentGridClicked() {
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

	// 補助グリッド設定
	@FXML
	private void onMenuAccentGridSettingClicked() throws IOException {
		showAccentGridSetting();
	}

	private void showAccentGridSetting() throws IOException {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("AccentGridSettingWindow.fxml"));
		loader.load();
		AccentGridSettingWindowController settingController = loader.getController();
		settingController.receiveProperty(accentGridP);

		Parent root = loader.getRoot();
		Stage stage = new Stage(StageStyle.UTILITY);
		stage.setScene(new Scene(root));
		stage.initOwner(rootPane.getScene().getWindow());
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setTitle("");
		stage.setResizable(false);

		stage.showAndWait();

		if (settingController.okPressed()) {
			accentGridP = settingController.getNewProperty();
			drawGrid();
		} else {
			System.out.println("cancel");
		}
	}

	// 描画方法
	@FXML
	private void onMenuDrawtypeNormalClicked() {
		Command c = new DrawtypeChangeCommand(this, Drawtype.NORMAL);
		pushNewHistory(c);
		c.execute();
	}
	@FXML
	private void onMenuDrawtypeCurClicked() {
		Command c = new DrawtypeChangeCommand(this, Drawtype.FILL_WITH_CURRENT_COLOR_AT_START_POS);
		pushNewHistory(c);
		c.execute();
	}
	@FXML
	private void onMenuDrawtypeNextClicked() {
		Command c = new DrawtypeChangeCommand(this, Drawtype.FILL_WITH_NEXT_COLOR_AT_START_POS);
		pushNewHistory(c);
		c.execute();
	}
	public void setDrawType(Drawtype drawType){
		this.drawtype = drawType;
		labelDrawtype.setText(drawtype.toString());
	}

	/*
	 * キャンバスのリサイズ
	 */
	@FXML
	private void onMenuResizeClicked() throws Exception {
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
		stage.setTitle("");
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
		instance = this;
		// 描画関係の初期化
		initChoiceShape();
		strategy = Shapes.nameToStrategy("自由線");
		setDrawType(Drawtype.NORMAL);
		pushNewHistory(new DrawtypeChangeCommand(this, Drawtype.NORMAL));
		labelOptionPos.setTextFill(Color.RED);

		// キャンバスとグリッドのレイヤーのGraphicsContextを得る
		canvasGraphics = canvas.getGraphicsContext2D();
		previewGraphics = previewLayer.getGraphicsContext2D();
		gridGraphics = gridLayer.getGraphicsContext2D();
		accentGraphics = accentGridLayer.getGraphicsContext2D();

		// グリッド/キャンバスサイズ設定
		gridSizeIndex = DEFAULT_GRID_SIZE_INDEX;
		p = new GridCanvasProperty(gridSizeArray[gridSizeIndex], 24, 24);
		accentGridP = new AccentGridProperty(8, 8, Color.hsb(0.0, 1.0, 0.9));
		labelGridSize.setText(String.valueOf(gridSizeArray[gridSizeIndex]));
		sliderZoom.setValue(gridSizeIndex);
		sliderZoom.valueProperty().addListener(
				(observable, oldValue, newValue) -> {
					zoomCanvas(newValue.intValue());
				}
		);

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
			accentGraphics.setFill(accentGridP.color);
			for(int x = 0; x <= p.numPixelX; x++){
				gridGraphics.fillRect(p.gridSize * x, 0.0, 1.0, gridLayer.getHeight());
				if (x != 0 && x % accentGridP.marginX == 0)
					accentGraphics.fillRect(p.gridSize * x, 0.0, 1.0, accentGridLayer.getHeight());
			}
			for(int y = 0; y <= p.numPixelY; y++){
				gridGraphics.fillRect(0.0, p.gridSize * y, gridLayer.getWidth(), 1.0);
				if (y != 0 && y % accentGridP.marginY == 0)
					accentGraphics.fillRect(0.0, p.gridSize * y, accentGridLayer.getWidth(), 1.0);
			}
		}
	}

	private void initChoiceShape() {
		comboShape.getItems().addAll(Shapes.values());
		comboShape.setCellFactory(param -> new ShapesListCell());
		comboShape.setButtonCell(new ShapesListCell());
		comboShape.getSelectionModel().selectFirst();
		comboShape.getSelectionModel().selectedItemProperty().addListener(
				(ov, oldVal, newVal) -> strategy = newVal.getStrategy());
	}

	private static class ShapesListCell extends ListCell<Shapes> {
		private final ImageView view;

		ShapesListCell() {
			view = new ImageView();
		}

		@Override
		protected void updateItem(Shapes item, boolean empty) {
			super.updateItem(item, empty);

			if (item == null || empty) {
				setGraphic(null);
				setText(null);
			} else {
				view.setImage(item.getIcon());
				setGraphic(view);
				setText(item.getName());
			}
		}
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
