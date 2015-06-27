package drawcommand;

import data.PixelState;
import data.Point;
import application.GridCanvasController;

public class FilledRectDrawCommand extends DrawCommand {
	private Point start;
	private Point end;
	private PixelState startPosPixel;

	public FilledRectDrawCommand(Point start, Point end, PixelState startPosPixel, boolean clear, GridCanvasController canvasController){
		super(clear, canvasController);
		this.start = start;
		this.end   = end;
		this.startPosPixel = startPosPixel;
	}

	@Override
	public void execute() {
		Point lt = new Point(Math.min(start.x, end.x), Math.min(start.y, end.y));
		Point rb = new Point(Math.max(start.x, end.x), Math.max(start.y, end.y));
		for(int x = lt.x; x <= rb.x; x++){
			for(int y = lt.y; y <= rb.y; y++)
				canvasController.drawPixel(x, y, startPosPixel, clear);
		}
	}
}
