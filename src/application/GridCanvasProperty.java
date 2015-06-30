package application;

class GridCanvasProperty {
	final int gridSize;
	final int numPixelX;
	final int numPixelY;

	GridCanvasProperty(int gridSize, int numPixelX, int numPixelY){
		this.gridSize = gridSize;
		this.numPixelX = numPixelX;
		this.numPixelY = numPixelY;
	}

	public String toString(){
		return "GridSize(" + gridSize + ", " + gridSize + "), CanvasSize(" + numPixelX + ", " + numPixelY + ")";
	}
}
