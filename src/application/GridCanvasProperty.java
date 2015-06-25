package application;

class GridCanvasProperty {
	final int gridWidth;
	final int gridHeight;
	final int numPixelX;
	final int numPixelY;

	GridCanvasProperty(int gridWidth, int gridHeight, int numPixelX, int numPixelY){
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.numPixelX = numPixelX;
		this.numPixelY = numPixelY;
	}

	public String toString(){
		return "GridSize(" + gridWidth + ", " + gridHeight + "), CanvasSize(" + numPixelX + ", " + numPixelY + ")";
	}
}
