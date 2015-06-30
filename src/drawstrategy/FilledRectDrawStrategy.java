package drawstrategy;

import application.GridCanvasController;
import data.PixelState;
import data.Point;
import drawcommand.FilledRectDrawCommand;

public class FilledRectDrawStrategy implements DrawStrategy {
	private Point startPos;
	private boolean clear;
	private PixelState startPosPixel;
	private GridCanvasController canvasController;

	public FilledRectDrawStrategy(GridCanvasController canvasController){
		this.canvasController = canvasController;
	}

	@Override
	public void onPressed(Point pos, PixelState startPosPixel, boolean clear) {
		startPos = pos;
		this.startPosPixel = startPosPixel;
		this.clear = clear;
	}

	@Override
	public void onReleased(Point pos) {
		FilledRectDrawCommand rect =
				new FilledRectDrawCommand(startPos, pos, startPosPixel, clear, canvasController);
		rect.execute();
		canvasController.pushNewHistory(rect);
	}

	@Override
	public void onDragged(Point pos) {
		int x, y;
		Point lt = new Point(Math.min(startPos.x, pos.x), Math.min(startPos.y, pos.y));
		Point rb = new Point(Math.max(startPos.x, pos.x), Math.max(startPos.y, pos.y));
		for(x = lt.x, y = lt.y; x <= rb.x; x++)
			canvasController.drawPreview(x, y, startPosPixel, clear);

		if(!(lt.y == rb.y)){
			for(x = lt.x, y = rb.y; x <= rb.x; x++)
				canvasController.drawPreview(x, y, startPosPixel, clear);
		}

		for(x = lt.x, y = lt.y + 1; y < rb.y; y++)
			canvasController.drawPreview(x, y, startPosPixel, clear);

		if(!(lt.x == rb.x)){
			for(x = rb.x, y = lt.y + 1; y < rb.y; y++)
				canvasController.drawPreview(x, y, startPosPixel, clear);
		}
	}

	public String toString(){
		return "長方形(塗りつぶし)";
	}
}
