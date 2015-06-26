package drawcommand;

import application.GridCanvasController;
import application.Point;

public class FilledRectDrawCommand extends DrawCommand {
	private Point lt;	// LeftTop
	private Point rb;	// RightBottom

	public FilledRectDrawCommand(Point start, Point end, GridCanvasController canvasController){
		super(canvasController);
		lt = new Point(Math.min(start.x, end.x), Math.min(start.y, end.y));
		rb = new Point(Math.max(start.x, end.x), Math.max(start.y, end.y));
	}

	@Override
	public void execute() {
		for(int x = lt.x; x <= rb.x; x++){
			for(int y = lt.y; y <= rb.y; y++)
				canvasController.drawPixel(x, y);
		}
	}
}
