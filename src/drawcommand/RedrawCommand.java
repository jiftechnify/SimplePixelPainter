package drawcommand;

import application.GridCanvasController;
import data.PixelState;
import data.Table;

public class RedrawCommand implements Command {
	private Table<PixelState> pixelData;
	private GridCanvasController canvasController;

	public RedrawCommand(int numPixX, int numPixY, GridCanvasController canvasController){
		this.canvasController = canvasController;
		pixelData = new Table<>(numPixX, numPixY, PixelState.WHITE);
		pixelData.initializeWith(PixelState.WHITE);
	}
	public void set(int x, int y, PixelState pixel){
		pixelData.setAt(x, y, pixel);
	}
	@Override
	public void execute() {
		canvasController.redraw(pixelData);
	}
}
