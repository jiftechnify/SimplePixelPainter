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
		// TODO 自動生成されたメソッド・スタブ

	}

	public String toString(){
		return "長方形(塗りつぶし)";
	}
}
