package drawstrategy;

import application.GridCanvasController;
import application.Point;
import drawcommand.EmptyRectDrawCommand;

public class EmptyRectDrawStrategy implements DrawStrategy {
	private Point startPos;
	private GridCanvasController canvasController;

	public EmptyRectDrawStrategy(GridCanvasController canvasController){
		this.canvasController = canvasController;
	}

	@Override
	public void onPressed(Point pos) {
		startPos = pos;
	}

	@Override
	public void onReleased(Point pos) {
		EmptyRectDrawCommand rect = new EmptyRectDrawCommand(startPos, pos, canvasController);
		rect.execute();
		canvasController.addHistory(rect);
	}

	@Override
	public void onDragged(Point pos) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public String toString(){
		return "四角枠";
	}
}
