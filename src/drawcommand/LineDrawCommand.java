package drawcommand;

import application.GridCanvasController;
import application.Point;

public class LineDrawCommand extends DrawCommand {
	private Point start;
	private Point end;
	private boolean egnoreStartPos = false;

	public LineDrawCommand(Point start, Point end, GridCanvasController canvasController) {
		super(canvasController);
		this.start = start;
		this.end = end;
	}

	public void notPaintStartPos(){
		egnoreStartPos = true;
	}
	@Override
	public void execute() {
		int nextX = start.x;
		int nextY = start.y;
		int deltaX = end.x - start.x;
		int deltaY = end.y - start.y;
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

		if(!egnoreStartPos)
			canvasController.drawPixel(nextX, nextY);

		if (deltaX > deltaY) {
			fraction = deltaY - deltaX / 2;
			while (nextX != end.x) {
				if (fraction >= 0) {
					nextY += stepY;
					fraction -= deltaX;
				}
				nextX += stepX;
				fraction += deltaY;
				canvasController.drawPixel(nextX, nextY);
			}
		} else {
			fraction = deltaX - deltaY / 2;
			while (nextY != end.y) {
				if (fraction >= 0) {
					nextX += stepX;
					fraction -= deltaY;
				}
				nextY += stepY;
				fraction += deltaX;
				canvasController.drawPixel(nextX, nextY);
			}
		}
	}
}
