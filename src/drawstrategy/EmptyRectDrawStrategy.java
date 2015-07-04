package drawstrategy;

import application.GridCanvasController;
import data.PixelState;
import data.Point;
import drawcommand.EmptyRectDrawCommand;

public class EmptyRectDrawStrategy implements DrawStrategy {
	private Point startPos;
	private PixelState startPosPixel;
	private boolean clear;
	private GridCanvasController canvasController;

	public EmptyRectDrawStrategy(GridCanvasController canvasController){
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
		EmptyRectDrawCommand rect =
				new EmptyRectDrawCommand(startPos, pos, startPosPixel, clear, canvasController);
		rect.execute();
		canvasController.pushNewHistory(rect);
	}

	@Override
	public void onDragged(Point pos) {
		int x, y;
		Point lt = new Point(Math.min(startPos.x, pos.x), Math.min(startPos.y, pos.y));
		Point rb = new Point(Math.max(startPos.x, pos.x), Math.max(startPos.y, pos.y));
		canvasController.showOptionPos(" -> " + (rb.x - lt.x + 1) + "*" + (rb.y - lt.y + 1));
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
		return "四角枠";
	}
}
