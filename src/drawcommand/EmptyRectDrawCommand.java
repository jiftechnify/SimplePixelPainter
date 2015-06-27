package drawcommand;

import data.PixelState;
import data.Point;
import application.GridCanvasController;

public class EmptyRectDrawCommand extends DrawCommand{
	private Point start;
	private Point end;
	private PixelState startPosPixel;

	public EmptyRectDrawCommand(Point start, Point end, PixelState startPosPixel, boolean clear, GridCanvasController canvasController){
		super(clear, canvasController);
		this.start = start;
		this.end   = end;
		this.startPosPixel = startPosPixel;
	}

	@Override
	public void execute(){
		int x, y;
		Point lt = new Point(Math.min(start.x, end.x), Math.min(start.y, end.y));
		Point rb = new Point(Math.max(start.x, end.x), Math.max(start.y, end.y));
		for(x = lt.x, y = lt.y; x <= rb.x; x++)
			canvasController.drawPixel(x, y, startPosPixel, clear);

		if(!(lt.y == rb.y)){
			for(x = lt.x, y = rb.y; x <= rb.x; x++)
				canvasController.drawPixel(x, y, startPosPixel, clear);
		}

		for(x = lt.x, y = lt.y + 1; y < rb.y; y++)
			canvasController.drawPixel(x, y, startPosPixel, clear);

		if(!(lt.x == rb.x)){
			for(x = rb.x, y = lt.y + 1; y < rb.y; y++)
				canvasController.drawPixel(x, y, startPosPixel, clear);
		}
	}
}
