package application;

import java.awt.Point;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class GridCanvasController implements Initializable{
	@FXML private Canvas canvas;
	@FXML private Canvas gridLayer;
	@FXML private MenuItem menuClear;
	@FXML private MenuItem menuGrid;

	private int gridSize;
	private GraphicsContext graphics;
	private int numPixelX;
	private int numPixelY;
	private Point prevGrid = new Point(-1, -1);
	private boolean nowDragging = false;
	private PixelState[][] arrayPixel;

	private static final Color GRID_COLOR = new Color(0.5, 0.5, 0.5, 1.0);

	@FXML
	private void onCanvasDragged(MouseEvent e){
		Point grid = new Point((int)(e.getX() / gridSize), (int)(e.getY() / gridSize));
		if(!grid.equals(prevGrid)){
			if(e.getButton().equals(MouseButton.SECONDARY))
				drawPixel(grid, PixelState.WHITE);
			else
				drawPixel(grid, PixelState.nextState(arrayPixel[grid.x][grid.y]));
		}
		prevGrid = grid;
		nowDragging = true;
	}

	@FXML
	private void onCanvasClicked(MouseEvent e){
		if(!nowDragging){
			Point grid = new Point((int)(e.getX() / gridSize), (int)(e.getY() / gridSize));
			if(e.getButton().equals(MouseButton.SECONDARY))
				drawPixel(grid, PixelState.WHITE);
			else
				drawPixel(grid, PixelState.nextState(arrayPixel[grid.x][grid.y]));
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		graphics = canvas.getGraphicsContext2D();
		gridSize = 16;
		numPixelX = (int)(canvas.getWidth() / gridSize);
		numPixelY = (int)(canvas.getHeight() / gridSize);
		arrayPixel = new PixelState[numPixelX][numPixelY];
		clearAll();

		if(gridSize > 2){
			GraphicsContext gridLay = gridLayer.getGraphicsContext2D();
			gridLay.setFill(GRID_COLOR);
			for(int x = 0; x <= numPixelX; x++)
				gridLay.fillRect(gridSize * x, 0.0, 1.0, canvas.getHeight());
			for(int y = 0; y <= numPixelY; y++)
				gridLay.fillRect(0.0, gridSize * y, canvas.getWidth(), 1.0);
			gridLayer.toFront();
		}
	}

	private void clearAll(){
		for(int i = 0; i < numPixelX; i++){
			for(int j = 0; j < numPixelY; j++){
				arrayPixel[i][j] = PixelState.WHITE;
			}
		}
		graphics.setFill(Color.WHITE);
		graphics.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
	}

	private void drawPixel(Point pos, PixelState state){
		graphics.setFill(state.pixelColor());
		arrayPixel[pos.x][pos.y] = state;
		graphics.fillRect(pos.x * gridSize, pos.y * gridSize, gridSize, gridSize);
	}
}
