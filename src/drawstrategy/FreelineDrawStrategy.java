package drawstrategy;

import application.GridCanvasController;
import data.PixelState;
import data.Point;
import drawcommand.FreelineDrawCommand;
import drawcommand.LineDrawCommand;

public class FreelineDrawStrategy implements DrawStrategy{
	private GridCanvasController canvasController;
	private FreelineDrawCommand freeline;
	private Point prevPos;
	private PixelState startPosPixel;
	private boolean clear;

	public FreelineDrawStrategy(GridCanvasController canvasController){
		this.canvasController = canvasController;
		prevPos = Point.DUMMY_POINT;
	}
	@Override
	public void onPressed(Point pos, PixelState startPosPixel, boolean clear) {
		this.startPosPixel = startPosPixel;
		this.clear = clear;
		freeline = new FreelineDrawCommand(clear, canvasController);
		LineDrawCommand startPoint = new LineDrawCommand(pos, pos, startPosPixel, clear, canvasController);
		startPoint.execute();
		freeline.addLine(startPoint);
		prevPos = Point.DUMMY_POINT;
	}

	@Override
	public void onReleased(Point pos) {
		canvasController.pushNewHistory(freeline);
		prevPos = Point.DUMMY_POINT;
	}
	@Override
	public void onDragged(Point pos) {
		LineDrawCommand line = new LineDrawCommand(prevPos, pos, startPosPixel, clear, canvasController);
		line.notPaintStartPos();
		line.execute();
		freeline.addLine(line);
		prevPos = pos;
	}

	public String toString(){
		return "自由線";
	}
}
