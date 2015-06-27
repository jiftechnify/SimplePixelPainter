package application;

import data.PixelState;
import data.Table;

public class GridCanvasModel {
	private Table<PixelState> pixelData;

	public GridCanvasModel(int numPixX, int numPixY){
		pixelData = new Table<>(numPixX, numPixX, PixelState.WHITE);
		pixelData.initiaizeWith(PixelState.WHITE);
	}

	public void setAt(int x, int y, PixelState pixel){
		pixelData.setAt(x, y, pixel);
	}

	public PixelState getAt(int x, int y){
		return pixelData.getAt(x, y);
	}

	public Table<PixelState> getTable(){
		return pixelData;
	}

	public void clearAll(){
		pixelData.initiaizeWith(PixelState.WHITE);
	}

	public void resize(int newX, int newY){
		pixelData.resize(newX, newY, PixelState.WHITE);
	}
}
