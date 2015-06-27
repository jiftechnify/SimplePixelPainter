package drawstrategy;

import application.GridCanvasController;
import data.PixelState;
import data.Point;
import drawcommand.LineDrawCommand;

public class LineDrawStrategy implements DrawStrategy {
	private Point startPos;
	private PixelState startPosPixel;
	private boolean clear;
	private GridCanvasController canvasController;

	public LineDrawStrategy(GridCanvasController canvasController){
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
		LineDrawCommand line = new LineDrawCommand(startPos, pos, startPosPixel, clear, canvasController);
		line.execute();
		canvasController.pushNewHistory(line);
	}

	@Override
	public void onDragged(Point pos) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public String toString(){
		return "直線";
	}
}
