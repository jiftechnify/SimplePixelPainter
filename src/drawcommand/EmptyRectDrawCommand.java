package drawcommand;

import application.GridCanvasController;
import application.Point;

public class EmptyRectDrawCommand extends DrawCommand{
	private Point lt;
	private Point rb;

	public EmptyRectDrawCommand(Point start, Point end, GridCanvasController canvasController){
		super(canvasController);
		lt = new Point(Math.min(start.x, end.x), Math.min(start.y, end.y));
		rb = new Point(Math.max(start.x, end.x), Math.max(start.y, end.y));
	}

	@Override
	public void execute(){
		int x, y;
		for(x = lt.x, y = lt.y; x <= rb.x; x++)
			canvasController.drawPixel(x, y);
		
		if(!(lt.y == rb.y)){
			for(x = lt.x, y = rb.y; x <= rb.x; x++)
				canvasController.drawPixel(x, y);
		}
			
		for(x = lt.x, y = lt.y + 1; y < rb.y; y++)
			canvasController.drawPixel(x, y);
		
		if(!(lt.x == rb.x)){
			for(x = rb.x, y = lt.y + 1; y < rb.y; y++)
				canvasController.drawPixel(x, y);
		}
	}
}
