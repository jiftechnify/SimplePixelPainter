package drawstrategy;

import application.GridCanvasController;
import application.Point;
import drawcommand.FilledRectDrawCommand;

public class FilledRectDrawStrategy implements DrawStrategy {
	private Point startPos;
	private GridCanvasController canvasController;

	public FilledRectDrawStrategy(GridCanvasController canvasController){
		this.canvasController = canvasController;
	}

	@Override
	public void onPressed(Point pos) {
		startPos = pos;
	}

	@Override
	public void onReleased(Point pos) {
		FilledRectDrawCommand rect = new FilledRectDrawCommand(startPos, pos, canvasController);
		rect.execute();
		canvasController.addHistory(rect);
	}

	@Override
	public void onDragged(Point pos) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public String toString(){
		return "長方形(塗りつぶし)";
	}
}
