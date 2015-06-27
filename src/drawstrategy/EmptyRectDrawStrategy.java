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
		// TODO 自動生成されたメソッド・スタブ

	}

	public String toString(){
		return "四角枠";
	}
}
