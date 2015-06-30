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
		int nextX = startPos.x;
		int nextY = startPos.y;
		int deltaX = pos.x - startPos.x;
		int deltaY = pos.y - startPos.y;
		int stepX, stepY;
		int fraction;


		if (deltaX < 0)
			stepX = -1;
		else
			stepX = 1;
		if (deltaY < 0)
			stepY = -1;
		else
			stepY = 1;

		deltaX = Math.abs(deltaX * 2);
		deltaY = Math.abs(deltaY * 2);

		canvasController.drawPreview(nextX, nextY, startPosPixel, clear);

		if (deltaX > deltaY) {
			fraction = deltaY - deltaX / 2;
			while (nextX != pos.x) {
				if (fraction >= 0) {
					nextY += stepY;
					fraction -= deltaX;
				}
				nextX += stepX;
				fraction += deltaY;
				canvasController.drawPreview(nextX, nextY, startPosPixel, clear);
			}
		} else {
			fraction = deltaX - deltaY / 2;
			while (nextY != pos.y) {
				if (fraction >= 0) {
					nextX += stepX;
					fraction -= deltaY;
				}
				nextY += stepY;
				fraction += deltaX;
				canvasController.drawPreview(nextX, nextY, startPosPixel, clear);
			}
		}
	}

	public String toString(){
		return "直線";
	}
}
