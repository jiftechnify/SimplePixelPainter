package drawstrategy;

import application.GridCanvasController;
import application.Point;
import drawcommand.LineDrawCommand;

public class LineDrawStrategy implements DrawStrategy {
	private Point startPos;
	private GridCanvasController canvasController;

	public LineDrawStrategy(GridCanvasController canvasController){
		this.canvasController = canvasController;
	}

	@Override
	public void onPressed(Point pos) {
		startPos = pos;
	}

	@Override
	public void onReleased(Point pos) {
		LineDrawCommand line = new LineDrawCommand(startPos, pos, canvasController);
		line.execute();
		canvasController.addHistory(line);
	}

	@Override
	public void onDragged(Point pos) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public String toString(){
		return "直線";
	}
}
