package drawstrategy;

import application.GridCanvasController;
import application.Point;
import drawcommand.FreelineDrawCommand;
import drawcommand.LineDrawCommand;

public class FreelineDrawStrategy implements DrawStrategy{
	private static final Point DUMMY = new Point(-1, -1);
	private GridCanvasController canvasController;
	private FreelineDrawCommand freeline;
	private Point prevPos;

	public FreelineDrawStrategy(GridCanvasController canvasController){
		this.canvasController = canvasController;
		prevPos = DUMMY;
	}
	@Override
	public void onPressed(Point pos) {
		freeline = new FreelineDrawCommand(canvasController);
		LineDrawCommand startPoint = new LineDrawCommand(pos, pos, canvasController);
		startPoint.execute();
		freeline.addLine(startPoint);
		prevPos = pos;
	}

	@Override
	public void onReleased(Point pos) {
		canvasController.addHistory(freeline);
		prevPos = DUMMY;
	}
	@Override
	public void onDragged(Point pos) {
		LineDrawCommand line = new LineDrawCommand(prevPos, pos, canvasController);
		line.notPaintStartPos();
		line.execute();
		freeline.addLine(line);
		prevPos = pos;
	}

	public String toString(){
		return "自由線";
	}
}
